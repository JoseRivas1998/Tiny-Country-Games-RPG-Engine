package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.Float2;
import com.tcg.rpgengine.common.utils.DataCompression;
import com.tcg.rpgengine.entities.GameMap;
import com.tcg.rpgengine.utils.GameConstants;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingMapState implements GameState {

    private final TCGRPGGame game;
    private final UUID mapToLoad;

    private final AtomicBoolean loadedTextures;
    private final AtomicBoolean startedLoading;
    private final AtomicBoolean doneLoading;

    private Viewport viewport;

    private Rectangle loadAnimationRect;
    private Animation<TextureRegion> loadAnimation;

    private float animationStateTime;

    private GameMap loadedMap;

    public LoadingMapState(TCGRPGGame game, UUID mapToLoad) {
        this.game = game;
        this.mapToLoad = mapToLoad;
        this.loadedTextures = new AtomicBoolean(false);
        this.startedLoading = new AtomicBoolean(false);
        this.doneLoading = new AtomicBoolean(false);
    }

    @Override
    public void create() {

        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        this.initAnimation();

        new Thread(() -> {

            final FileHandle mapFile = Gdx.files.local(String.format("maps/%s.tcgmap", this.mapToLoad));
            final byte[] mapBytes = DataCompression.decompress(mapFile.readBytes());
            final AssetTable<TiledImageAsset> assetTable = MapEntity.assetTableFromBytes(
                    this.game.assetLibrary, mapBytes
            );
            Gdx.app.postRunnable(() -> {
                for (int i = 0; i < assetTable.size(); i++) {
                    final TiledImageAsset tiledImageAsset = assetTable.get(this.game.assetLibrary, i);
                    this.game.localAssetManager.load(tiledImageAsset.getPath(), Texture.class);
                }
                this.startedLoading.set(true);
            });

            while (true) {
                if (this.loadedTextures.get()) break;
            }

            final MapEntity map = MapEntity.ofBytes(this.game.assetLibrary, tiledImageAsset -> {
                final Texture tilesetImage = this.game.localAssetManager.get(tiledImageAsset.getPath());
                return Float2.of(tilesetImage.getWidth(), tilesetImage.getHeight());
            }, ByteBuffer.wrap(mapBytes));

            Gdx.app.postRunnable(() -> {
                this.loadedMap = new GameMap(this.game, map);
                this.doneLoading.set(true);
            });

        }).start();
    }

    private void initAnimation() {
        final Texture animationTexture = this.game.internalAssetManager.get("tcgrotate.png");
        final int frameWidth = animationTexture.getWidth() / 4;
        final int frameHeight = animationTexture.getHeight() / 4;
        final TextureRegion[][] animationMatrix = TextureRegion.split(animationTexture, frameWidth, frameHeight);
        final TextureRegion[] animationFrames = new TextureRegion[4 * 4];
        for (int row = 0; row < 4; row++) {
            System.arraycopy(animationMatrix[row], 0, animationFrames, row * 4, 4);
        }
        this.loadAnimation = new Animation<TextureRegion>(0.075f, animationFrames);
        this.loadAnimationRect = new Rectangle();
        this.loadAnimationRect.setSize(75, 75);
        this.loadAnimationRect.setY(25);
        this.loadAnimationRect.setX(GameConstants.VIEW_WIDTH - 100);
        this.animationStateTime = 0;
    }

    @Override
    public void handleInput(float deltaTime) {

    }

    @Override
    public void update(float deltaTime) {
        if (this.startedLoading.get()) {
            if (!this.game.localAssetManager.isFinished()) {
                this.game.localAssetManager.update();
            } else {
                this.loadedTextures.set(true);
            }
        }
        if (this.doneLoading.get()) {
            this.game.stateEngine.setState(new MapDemoState(this.game, this.loadedMap));
        }
    }

    @Override
    public void draw(float deltaTime) {
        this.animationStateTime += deltaTime;
        final TextureRegion animationFrame = this.loadAnimation.getKeyFrame(this.animationStateTime, true);
        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.game.batch.draw(animationFrame, this.loadAnimationRect.x, this.loadAnimationRect.y,
                this.loadAnimationRect.width, this.loadAnimationRect.height);
        this.game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
