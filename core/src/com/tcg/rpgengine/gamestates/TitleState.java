package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.gamestates.GameState;
import com.tcg.rpgengine.utils.GameConstants;

public class TitleState implements GameState {

    private final TCGRPGGame game;
    private Texture backgroundImage;
    private Viewport viewport;
    private Music titleMusic;

    public TitleState(TCGRPGGame game) {
        this.game = game;
    }

    @Override
    public void create() {
        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        final Title titleData = this.game.systemData.title;
        final ImageAsset titleImageAsset = this.game.assetLibrary.getImageAssetById(titleData.getImageId());
        this.backgroundImage = this.game.localAssetManager.get(titleImageAsset.path, Texture.class);
        final SoundAsset titleMusicAsset = this.game.assetLibrary.getMusicAssetById(titleData.getMusicId());
        this.titleMusic = this.game.localAssetManager.get(titleMusicAsset.path, Music.class);
        this.titleMusic.setLooping(true);
        Gdx.graphics.setTitle(titleData.title);

        this.titleMusic.play();
    }

    @Override
    public void handleInput(float deltaTime) {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(float delta) {

        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.game.batch.draw(this.backgroundImage, 0, 0, GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);
        this.game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        this.titleMusic.pause();
    }

    @Override
    public void resume() {
        this.titleMusic.play();
    }

    @Override
    public void dispose() {
        this.titleMusic.stop();
    }
}
