package com.tcg.rpgengine.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.utils.GameConstants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameConstants.VIEW_WIDTH;
		config.height = GameConstants.VIEW_HEIGHT;
		config.foregroundFPS = 0;
		config.vSyncEnabled = false;
		new LwjglApplication(new TCGRPGGame(Application.LOG_DEBUG), config);
	}
}
