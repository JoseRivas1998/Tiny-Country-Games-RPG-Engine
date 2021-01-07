package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationContext {

    private static ApplicationContext instance;

    public Stage primaryStage;
    public Scene primaryScene;
    public final Files files;


    private ApplicationContext() {
        files = new LwjglFiles();
    }

    public static ApplicationContext context() {
        if (instance == null) {
            synchronized (ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
        return instance;
    }

    public static class Constants {
        public static final double SPACING = 5.0;
        public static final double PADDING = 10.0;
        public static final String PROJECT_FILE_EXTENSION = "tcgproj";
    }

}
