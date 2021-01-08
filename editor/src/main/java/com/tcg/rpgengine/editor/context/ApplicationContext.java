package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.containers.EditorPane;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationContext {

    private static ApplicationContext instance;

    public Stage primaryStage;
    public Scene primaryScene;
    public final Files files;
    public CurrentProject currentProject;


    private ApplicationContext() {
        this.files = new LwjglFiles();
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

    public void selectAndOpenProject() {
        try {
            CurrentProject.selectAndOpenProject().ifPresent(newOpenProject -> this.currentProject = newOpenProject);
            this.openEditorWindow();
        } catch (Exception exception) {
            final ErrorDialog errorDialog = new ErrorDialog(exception);
            errorDialog.initOwner(this.primaryStage);
            errorDialog.showAndWait();
        }
    }

    public void openProject(FileHandle projectFile) {
        try {
            this.currentProject = CurrentProject.openProject(projectFile);
            this.openEditorWindow();
        } catch (Exception exception) {
            final ErrorDialog errorDialog = new ErrorDialog(exception);
            errorDialog.initOwner(this.primaryStage);
            errorDialog.showAndWait();
        }
    }

    private void openEditorWindow() {
        final Scene editorScene = new Scene(new EditorPane(), Constants.EDITOR_WIDTH, Constants.EDITOR_HEIGHT);
        final Stage editorStage = new Stage();
        editorStage.setTitle(String.format("Tiny Country Games RPG Engine | %s", this.currentProject.getTitle()));
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }
        this.primaryScene = editorScene;
        this.primaryStage = editorStage;
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.show();
    }

    public static class Constants {
        public static final double SPACING = 5.0;
        public static final double PADDING = 10.0;
        public static final String PROJECT_FILE_EXTENSION = "tcgproj";
        public static final double EDITOR_WIDTH = 1280.0;
        public static final double EDITOR_HEIGHT = 720.0;
    }

}
