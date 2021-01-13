package com.tcg.rpgengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.screens.LoadingScreen;

public class TCGRPGGame extends Game {
	public SpriteBatch batch;
	public AssetManager localAssetManager;
	public AssetManager internalAssetManager;
	public AssetLibrary assetLibrary;
	public SystemData systemData;
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
		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose () {
		this.batch.dispose();
		this.localAssetManager.dispose();
	}
}
