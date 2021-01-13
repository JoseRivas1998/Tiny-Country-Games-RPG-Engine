package com.tcg.rpgengine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.utils.GameConstants;
import com.tcg.rpgengine.utils.GameDataLoader;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingScreen extends ScreenAdapter {

    private static final float FADE_TIME = 1.0f;
    private static final float HOLD_TIME = 2.0f;
    private static final Interpolation FADE_FUNCTION = Interpolation.fade;

    private final TCGRPGGame game;
    private final AtomicBoolean decodingAssetData;
    private Viewport viewport;
    private Texture splashImage;
    private Rectangle splashImageRect;

    private float currentStateTime;
    private LoadingStates currentState;

    public LoadingScreen(TCGRPGGame game) {
        this.game = game;
        this.decodingAssetData = new AtomicBoolean(true);
    }

    @Override
    public void show() {

        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);

        final AssetDescriptor<Texture> splashImageDescriptor = new AssetDescriptor<>(
                "badlogic.jpg", Texture.class);
        this.game.internalAssetManager.load(splashImageDescriptor);
        this.splashImage = this.game.internalAssetManager.finishLoadingAsset(splashImageDescriptor);

        this.splashImageRect = new Rectangle();
        this.splashImageRect.setSize(this.splashImage.getWidth(), this.splashImage.getHeight());
        this.splashImageRect.x = GameConstants.VIEW_WIDTH * 0.5f - this.splashImageRect.width * 0.5f;
        this.splashImageRect.y = GameConstants.VIEW_HEIGHT * 0.5f - this.splashImageRect.height * 0.5f;

        final GameDataLoader gameDataLoader = new GameDataLoader(this.game, () -> {
            final Title title = this.game.systemData.title;
            final ImageAsset titleImage = this.game.assetLibrary.getImageAssetById(title.getImageId());
            this.game.localAssetManager.load(titleImage.path, Texture.class);
            this.decodingAssetData.set(false);
        });
        gameDataLoader.start();

        this.currentStateTime = 0;
        this.currentState = LoadingStates.FadeIn;
    }

    @Override
    public void render(float delta) {
        if (!this.game.localAssetManager.isFinished()) {
            this.game.localAssetManager.update();
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

    private void renderFadeOut(float delta) {
        final float fadeOutAlpha = FADE_FUNCTION.apply(1f, 0f, this.currentStateTime / FADE_TIME);
        this.game.batch.setColor(1f, 1f, 1f, fadeOutAlpha);
        if (Float.compare(this.currentStateTime, FADE_TIME) >= 0) {
            this.game.batch.setColor(Color.WHITE);
            this.game.setScreen(new TitleScreen(this.game));
        } else {
            this.currentStateTime += delta;
            this.draw();
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
        this.draw();
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
        this.draw();
    }

    private void draw() {
        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.game.batch.draw(this.splashImage, this.splashImageRect.x, this.splashImageRect.y);
        this.game.batch.end();
    }

    private boolean isDoneLoading() {
        return !this.decodingAssetData.get() && this.game.localAssetManager.isFinished();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    private enum LoadingStates {
        FadeIn, Holding, FadeOut
    }

}
