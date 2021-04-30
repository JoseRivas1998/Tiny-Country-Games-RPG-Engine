package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.common.data.system.UISounds;
import com.tcg.rpgengine.common.data.system.WindowSkin;
import com.tcg.rpgengine.utils.GameConstants;
import com.tcg.rpgengine.utils.GameDataLoader;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingState implements GameState {

    private static final float FADE_TIME = 1.0f;
    private static final float HOLD_TIME = 2.0f;
    private static final Interpolation FADE_FUNCTION = Interpolation.fade;

    private final TCGRPGGame game;
    private final AtomicBoolean decodingAssetData;
    private Viewport viewport;
    private Rectangle splashImageRect;
    private Animation<TextureRegion> splashAnimation;

    private float currentStateTime;
    private LoadingStates currentState;

    private float animationStateTime;

    public LoadingState(TCGRPGGame game) {
        this.game = game;
        this.decodingAssetData = new AtomicBoolean(true);
    }

    @Override
    public void create() {

        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        final AssetDescriptor<Texture> splashImageDescriptor = new AssetDescriptor<>(
                "tcgrotate.png", Texture.class);
        this.game.internalAssetManager.load(splashImageDescriptor);
        Texture splashImage = this.game.internalAssetManager.finishLoadingAsset(splashImageDescriptor);

        final int tileWidth = splashImage.getWidth() / 4;
        final int tileHeight = splashImage.getHeight() / 4;
        final TextureRegion[][] animationMatrix = TextureRegion.split(splashImage, tileWidth, tileHeight);
        final TextureRegion[] animationFrames = new TextureRegion[16];
        for (int row = 0; row < 4; row++) {
            System.arraycopy(animationMatrix[row], 0, animationFrames, row * 4, 4);
        }
        this.splashAnimation = new Animation<>(0.15f, animationFrames);

        this.animationStateTime = 0;

        this.splashImageRect = new Rectangle();
        this.splashImageRect.setSize(tileWidth, tileHeight);
        this.splashImageRect.x = GameConstants.VIEW_WIDTH * 0.5f - this.splashImageRect.width * 0.5f;
        this.splashImageRect.y = GameConstants.VIEW_HEIGHT * 0.5f - this.splashImageRect.height * 0.5f;

        this.loadGothicFont("gothic24.ttf", 24, 2);
        this.loadGothicFont("gothic48.ttf", 48, 2);
        this.loadGothicFont("gothic72.ttf", 72, 3);

        final GameDataLoader gameDataLoader = new GameDataLoader(this.game, () -> {
            this.loadTitleAssets();
            this.loadUiSoundAssets();
            this.loadWindowSkinAssets();

            this.decodingAssetData.set(false);
        });
        gameDataLoader.start();

        this.currentStateTime = 0;
        this.currentState = LoadingStates.FadeIn;
    }

    private void loadWindowSkinAssets() {
        final WindowSkin windowSkin = this.game.systemData.windowSkin;
        this.loadImageAsset(windowSkin.getWindowSkinId());
    }

    private void loadUiSoundAssets() {
        final UISounds uiSounds = this.game.systemData.uiSounds;
        this.loadSoundEffectAsset(uiSounds.getCursorId());
        this.loadSoundEffectAsset(uiSounds.getOkId());
        this.loadSoundEffectAsset(uiSounds.getBuzzerId());
        this.loadSoundEffectAsset(uiSounds.getCancelId());
    }

    private void loadTitleAssets() {
        final Title title = this.game.systemData.title;
        this.loadImageAsset(title.getImageId());
        this.loadMusicAsset(title.getMusicId());
    }

    private void loadSoundEffectAsset(UUID soundId) {
        final SoundAsset sound = this.game.assetLibrary.getSoundEffectAssetBytId(soundId);
        this.game.localAssetManager.load(sound.path, Sound.class);
    }

    private void loadMusicAsset(UUID musicId) {
        final SoundAsset music = this.game.assetLibrary.getMusicAssetById(musicId);
        this.game.localAssetManager.load(music.path, Music.class);
    }

    private void loadImageAsset(UUID imageId) {
        final ImageAsset image = this.game.assetLibrary.getImageAssetById(imageId);
        this.game.localAssetManager.load(image.path, Texture.class);
    }

    protected void loadGothicFont(String fontName, int fontSize, float borderWidth) {
        final FreeTypeFontLoaderParameter gothic = new FreeTypeFontLoaderParameter();
        gothic.fontFileName = "font/gothic_regular.ttf";
        gothic.fontParameters.size = fontSize;
        gothic.fontParameters.color = Color.WHITE;
        gothic.fontParameters.borderWidth = borderWidth;
        gothic.fontParameters.borderColor = Color.BLACK;
        this.game.internalAssetManager.load(fontName, BitmapFont.class, gothic);
    }

    @Override
    public void handleInput(float deltaTime) {

    }

    @Override
    public void update(float delta) {
        if (!this.game.localAssetManager.isFinished()) {
            this.game.localAssetManager.update();
        }
        if (!this.game.internalAssetManager.isFinished()) {
            this.game.internalAssetManager.update();
        }

        switch (this.currentState) {
            case FadeIn:
                this.renderFadeIn(delta);
                break;
            case Holding:
                this.renderHold(delta);
                break;
            case FadeOut:
                this.renderFadeOut(delta);
                break;
        }

    }

    @Override
    public void draw(float delta) {
        this.animationStateTime += delta;
        final TextureRegion currentFrame = this.splashAnimation.getKeyFrame(this.animationStateTime, true);
        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.game.batch.draw(currentFrame, this.splashImageRect.x, this.splashImageRect.y);
        this.game.batch.end();
    }

    private void renderFadeOut(float delta) {
        final float fadeOutAlpha = FADE_FUNCTION.apply(1f, 0f, this.currentStateTime / FADE_TIME);
        this.game.batch.setColor(1f, 1f, 1f, fadeOutAlpha);
        if (Float.compare(this.currentStateTime, FADE_TIME) >= 0) {
            this.game.batch.setColor(Color.WHITE);
            this.game.stateEngine.setState(new TitleState(this.game));
        } else {
            this.currentStateTime += delta;
        }
    }

    private void renderHold(float delta) {
        this.game.batch.setColor(Color.WHITE);
        if (Float.compare(this.currentStateTime, HOLD_TIME) >= 0 && this.isDoneLoading()) {
            this.currentStateTime = 0;
            this.currentState = LoadingStates.FadeOut;
        } else {
            this.currentStateTime += delta;
        }
    }

    private void renderFadeIn(float delta) {
        final float fadeInAlpha = FADE_FUNCTION.apply(this.currentStateTime / FADE_TIME);
        this.game.batch.setColor(1f, 1f, 1f, fadeInAlpha);
        if (Float.compare(this.currentStateTime, FADE_TIME) >= 0) {
            this.currentStateTime = 0;
            this.currentState = LoadingStates.Holding;
            this.game.batch.setColor(Color.WHITE);
        } else {
            this.currentStateTime += delta;
        }
    }

    private boolean isDoneLoading() {
        return !this.decodingAssetData.get()
                && this.game.localAssetManager.isFinished()
                && this.game.internalAssetManager.isFinished();
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

    private enum LoadingStates {
        FadeIn, Holding, FadeOut
    }

}
