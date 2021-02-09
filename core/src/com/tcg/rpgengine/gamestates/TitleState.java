package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.common.data.system.UISounds;
import com.tcg.rpgengine.input.ControlInput;
import com.tcg.rpgengine.input.Controls;
import com.tcg.rpgengine.ui.Menu;
import com.tcg.rpgengine.ui.TextBounds;
import com.tcg.rpgengine.ui.Window;
import com.tcg.rpgengine.utils.GameConstants;

import java.util.Optional;

public class TitleState implements GameState {

    private final TCGRPGGame game;
    private Texture backgroundImage;
    private Viewport viewport;
    private Music titleMusic;
    private TextBounds titleText;
    private Menu<String> mainMenu;
    private Sound cursor;
    private Sound ok;
    private Sound buzzer;

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

        final BitmapFont gothic72Font = this.game.internalAssetManager.get("gothic72.ttf", BitmapFont.class);
        this.titleText = new TextBounds(gothic72Font, titleData.title);
        this.titleText.setX(GameConstants.VIEW_WIDTH * 0.5f - this.titleText.getWidth() * 0.5f);
        this.titleText.setY(GameConstants.VIEW_HEIGHT * 0.75f + this.titleText.getHeight() * 0.5f);

        Gdx.graphics.setTitle(titleData.title);

        final BitmapFont gothic48Font = this.game.internalAssetManager.get("gothic48.ttf", BitmapFont.class);
        this.mainMenu = Menu.newMenu(this.game, gothic48Font, "New Game", "Continue", "Quit");
        this.mainMenu.setWidth(GameConstants.VIEW_WIDTH * 0.25f);
        this.mainMenu.update();
        this.mainMenu.setCenter(GameConstants.VIEW_WIDTH * 0.5f, GameConstants.VIEW_HEIGHT * .25f);

        final UISounds uiSounds = this.game.systemData.uiSounds;
        final SoundAsset cursorAsset = this.game.assetLibrary.getSoundEffectAssetBytId(uiSounds.getCursorId());
        final SoundAsset okAsset = this.game.assetLibrary.getSoundEffectAssetBytId(uiSounds.getOkId());
        final SoundAsset buzzerAsset = this.game.assetLibrary.getSoundEffectAssetBytId(uiSounds.getBuzzerId());
        this.cursor = this.game.localAssetManager.get(cursorAsset.path, Sound.class);
        this.ok = this.game.localAssetManager.get(okAsset.path, Sound.class);
        this.buzzer = this.game.localAssetManager.get(buzzerAsset.path, Sound.class);

//        this.titleMusic.play();
    }

    @Override
    public void handleInput(float deltaTime) {
        if (ControlInput.controlCheckPressed(Controls.MOVE_UP)) {
            this.cursor.play();
            this.mainMenu.previous();
        }
        if (ControlInput.controlCheckPressed(Controls.MOVE_DOWN)) {
            this.cursor.play();
            this.mainMenu.next();
        }
        if (ControlInput.controlCheckPressed(Controls.ACTION)) {
            final Optional<String> selectedItemOptional = this.mainMenu.getSelectedItem();
            if (selectedItemOptional.isPresent()) {
                this.ok.play();
                final String selectedItem = selectedItemOptional.get();
                System.out.println(selectedItem);

            } else {
                this.buzzer.play();
            }
        }
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(float delta) {

        this.game.batch.begin();
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.game.batch.draw(this.backgroundImage, 0, 0, GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);
        this.titleText.draw(this.game.batch);
        this.mainMenu.draw(this.game.batch, delta);
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
