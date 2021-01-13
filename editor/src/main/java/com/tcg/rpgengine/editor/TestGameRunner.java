package com.tcg.rpgengine.editor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.utils.GameConstants;

public class TestGameRunner {

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = GameConstants.VIEW_WIDTH;
        config.height = GameConstants.VIEW_HEIGHT;
        new LwjglApplication(new TCGRPGGame(Application.LOG_DEBUG), config);
    }

}
