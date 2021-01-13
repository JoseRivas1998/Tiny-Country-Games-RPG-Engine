package com.tcg.rpgengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.game.GameStateEngine;
import com.tcg.rpgengine.gamestates.LoadingState;

public class TCGRPGGame extends ApplicationAdapter {
	public SpriteBatch batch;
	public AssetManager localAssetManager;
	public AssetManager internalAssetManager;
	public AssetLibrary assetLibrary;
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
		this.localAssetManager = new AssetManager(new LocalFileHandleResolver());
		this.internalAssetManager = new AssetManager();
		this.assetLibrary = AssetLibrary.newAssetLibrary();
		this.stateEngine = new GameStateEngine();
		this.stateEngine.setState(new LoadingState(this));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		final float deltaTime = Gdx.graphics.getDeltaTime();
		this.stateEngine.step(deltaTime);
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
		this.localAssetManager.dispose();
		this.stateEngine.dispose();
	}
}
