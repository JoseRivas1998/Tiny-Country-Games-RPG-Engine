package com.tcg.rpgengine.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tcg.rpgengine.TCGRPGGame;

public class TestGameRunner {

    public static void main(String[] args) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new TCGRPGGame(), config);
    }

}
