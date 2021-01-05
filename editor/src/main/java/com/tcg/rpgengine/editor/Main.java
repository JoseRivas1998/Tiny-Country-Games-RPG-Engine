package com.tcg.rpgengine.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tcg.rpgengine.TCGRPGGame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final VBox stackPane = new VBox(10);
        stackPane.getChildren().add(new Label("Hello world!"));
        final Button run_game = new Button("Run Game");
        run_game.setOnAction(event -> {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.forceExit = false;
            new LwjglApplication(new TCGRPGGame(), config);
        });
        stackPane.getChildren().add(run_game);
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.show();
    }
}
