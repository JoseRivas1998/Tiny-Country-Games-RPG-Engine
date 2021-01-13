package com.tcg.rpgengine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.utils.GameConstants;

public class TitleScreen extends ScreenAdapter {

    private final TCGRPGGame game;
    private Texture backgroundImage;
    private Viewport viewport;

    public TitleScreen(TCGRPGGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        final Title titleData = this.game.systemData.title;
        final ImageAsset titleImageAsset = this.game.assetLibrary.getImageAssetById(titleData.getImageId());
        this.backgroundImage = this.game.localAssetManager.get(titleImageAsset.path, Texture.class);
    }

    @Override
    public void render(float delta) {

        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);

        this.game.batch.draw(this.backgroundImage, 0, 0, GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        this.game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }
}
