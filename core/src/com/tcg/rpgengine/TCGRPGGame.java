package com.tcg.rpgengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.database.Database;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.game.GameStateEngine;
import com.tcg.rpgengine.gamestates.LoadingState;
import com.tcg.rpgengine.input.ControlInput;
import com.tcg.rpgengine.input.ControllerInputProcessor;
import com.tcg.rpgengine.input.KeyboardInputProcessor;

public class TCGRPGGame extends ApplicationAdapter {
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public AssetManager localAssetManager;
	public AssetManager internalAssetManager;
	public AssetLibrary assetLibrary;
	public Database database;
	public SystemData systemData;
	public GameStateEngine stateEngine;
	private final int logLevel;

	public TCGRPGGame(int logLevel) {
		this.logLevel = logLevel;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(this.logLevel);
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.localAssetManager = new AssetManager(new LocalFileHandleResolver());

		this.internalAssetManager = new AssetManager();
		this.initializeInternalFreetypeFontLoader();

		Gdx.input.setInputProcessor(new KeyboardInputProcessor());
		Controllers.addListener(new ControllerInputProcessor());

		this.assetLibrary = AssetLibrary.newAssetLibrary();
		this.database = new Database(this.assetLibrary);
		this.stateEngine = new GameStateEngine();
		this.stateEngine.setState(new LoadingState(this));

	}

	private void initializeInternalFreetypeFontLoader() {
		final InternalFileHandleResolver internalResolver = new InternalFileHandleResolver();
		final FreeTypeFontGeneratorLoader internalFontLoader = new FreeTypeFontGeneratorLoader(internalResolver);
		this.internalAssetManager.setLoader(FreeTypeFontGenerator.class, internalFontLoader);
		this.internalAssetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(internalResolver));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		final float deltaTime = Gdx.graphics.getDeltaTime();
		this.stateEngine.step(deltaTime);
		ControlInput.update();
	}

	@Override
	public void resize(int width, int height) {
		this.stateEngine.resize(width, height);
	}

	@Override
	public void pause() {
		this.stateEngine.pause();
	}

	@Override
	public void resume() {
		this.stateEngine.resume();
	}

	@Override
	public void dispose () {
		this.batch.dispose();
		this.shapeRenderer.dispose();
		this.localAssetManager.dispose();
		this.stateEngine.dispose();
	}
}
