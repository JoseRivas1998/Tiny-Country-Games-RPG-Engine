package com.tcg.rpgengine.editor;

import com.tcg.rpgengine.common.Version;
import com.tcg.rpgengine.editor.containers.WelcomePage;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int START_SCREEN_WIDTH = 600;
    private static final int START_SCREEN_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final ApplicationContext context = ApplicationContext.context();
        context.primaryStage = primaryStage;
        context.primaryStage.setOnCloseRequest(event -> {
            ApplicationContext.context().jukebox.dispose();
        });
        context.primaryScene = new Scene(this.buildWelcomePagePane(), START_SCREEN_WIDTH, START_SCREEN_HEIGHT);
        context.primaryStage.setScene(context.primaryScene);
        context.primaryStage.setTitle("Tiny Country Games RPG Engine");
        context.primaryStage.show();
    }

    private BorderPane buildWelcomePagePane() {
        final BorderPane welcomePagePane = new BorderPane();
        final WelcomePage welcomePage = new WelcomePage(welcomePagePane);
        welcomePagePane.setCenter(welcomePage);

        welcomePagePane.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        return welcomePagePane;
    }
}
