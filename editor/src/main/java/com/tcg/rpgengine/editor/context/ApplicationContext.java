package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.utils.DataCompression;
import com.tcg.rpgengine.editor.TestGameRunner;
import com.tcg.rpgengine.editor.components.IconBar;
import com.tcg.rpgengine.editor.concurrency.TaskSequence;
import com.tcg.rpgengine.editor.containers.AssetManagerPage;
import com.tcg.rpgengine.editor.containers.DatabaseManagerPage;
import com.tcg.rpgengine.editor.containers.EditorPane;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.JavaProcess;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Collection;
import java.util.function.Function;

public class ApplicationContext {

    private static final EventHandler<WindowEvent> PREVENT_CLOSE = Event::consume;
    private static ApplicationContext instance;

    public Stage primaryStage;
    public Scene primaryScene;
    public final Files files;
    public CurrentProject currentProject;
    public final AppData appData;
    public Jukebox jukebox;

    private ApplicationContext() {
        this.files = new LwjglFiles();
        this.appData = new AppData();
        this.jukebox = new Jukebox();
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
            CurrentProject.selectAndOpenProject().ifPresent(newOpenProject -> {
                this.currentProject = newOpenProject;
                this.openEditorWindow();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
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
            exception.printStackTrace();
            final ErrorDialog errorDialog = new ErrorDialog(exception);
            errorDialog.initOwner(this.primaryStage);
            errorDialog.showAndWait();
        }
    }

    private void openEditorWindow() {
        this.appData.addOpenProject(this.currentProject.getTitle(), this.currentProject.getProjectFilePath());
        final BorderPane editorPaneWithIconBar = new BorderPane();
        final EditorPane editorPane = new EditorPane();
        editorPaneWithIconBar.setTop(new IconBar());
        editorPaneWithIconBar.setCenter(editorPane);
        final Scene editorScene = new Scene(editorPaneWithIconBar, Constants.EDITOR_WIDTH, Constants.EDITOR_HEIGHT);
        final Stage editorStage = new Stage();
        editorStage.setTitle(String.format("Tiny Country Games RPG Engine | %s", this.currentProject.getTitle()));
        if (this.primaryStage != null) {
            this.primaryStage.close();
        }
        this.primaryScene = editorScene;
        this.primaryStage = editorStage;
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setOnCloseRequest(this.defaultStageCloseEventListener());
        this.primaryStage.show();
    }

    public void openAssetManager() {
        final Stage stage = new Stage();
        final Scene scene = new Scene(new AssetManagerPage(stage),
                Constants.ASSET_MANAGER_WIDTH, Constants.ASSET_MANAGER_HEIGHT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Asset Manager");
        stage.initOwner(this.primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(event -> {
            this.jukebox.stopAll();
        });
        stage.showAndWait();
    }

    public void openDatabaseManager() {
        final Stage stage = new Stage();
        final Scene scene = new Scene(new DatabaseManagerPage(stage), Constants.EDITOR_WIDTH, Constants.EDITOR_HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Database Manager");
        stage.initOwner(this.primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void playGame() {
        final Stage progressStage = new Stage();

        final TaskSequence taskSequence = new TaskSequence();
        final AssetLibrary assetLibrary = this.currentProject.assetLibrary;
        taskSequence.addTask("Compiling Music", () -> {
            this.copyAssetCollectionToLocal(assetLibrary.getAllMusicAssets(), soundAsset -> soundAsset.path);
            final FileHandle musicDataFile = this.files.local("data/music.tcgdat");
            final byte[] compressedBytes = DataCompression.compress(this.currentProject.assetLibrary.musicAssetBytes());
            musicDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Compiling Images", () -> {
            this.copyAssetCollectionToLocal(assetLibrary.getAllImageAssets(), imageAsset -> imageAsset.path);
            final FileHandle imageDataFile = this.files.local("data/images.tcgdat");
            final byte[] compressedBytes = DataCompression.compress(this.currentProject.assetLibrary.imageAssetBytes());
            imageDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Compiling Sound Effects", () -> {
            this.copyAssetCollectionToLocal(assetLibrary.getAllSoundEffectAssets(), soundAsset -> soundAsset.path);
            final FileHandle soundDataFile = this.files.local("data/sound.tcgdat");
            byte[] compressedBytes = DataCompression.compress(this.currentProject.assetLibrary.soundAssetBytes());
            soundDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Compiling Spritesheet Pages", () -> {
            this.copyAssetCollectionToLocal(assetLibrary.getAllSpritesheetPages(), TiledImageAsset::getPath);
            final FileHandle spritesheetsDataFile = this.files.local("data/spritesheets.tcgdat");
            final byte[] decompressedBytes = this.currentProject.assetLibrary.spritesheetPagesBytes();
            final byte[] compressedBytes = DataCompression.compress(decompressedBytes);
            spritesheetsDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Compiling Tilesets", () -> {
            this.copyAssetCollectionToLocal(assetLibrary.getAllTilesets(), TiledImageAsset::getPath);
            final FileHandle tilesetsDataFile = this.files.local("data/tilesets.tcgdat");
            final byte[] compressedBytes = DataCompression.compress(this.currentProject.assetLibrary.tilesetsBytes());
            tilesetsDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Compiling System", () -> {
            final FileHandle systemDataFile = this.files.local("data/system.tcgdat");
            final byte[] compressedBytes = DataCompression.compress(this.currentProject.systemData.toBytes());
            systemDataFile.writeBytes(compressedBytes, false);
        });
        taskSequence.addTask("Running Game", () -> {
            try {
                JavaProcess.exec(TestGameRunner.class, null);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    final ErrorDialog errorDialog = new ErrorDialog(e);
                    errorDialog.initOwner(progressStage);
                    errorDialog.showAndWait();
                });
            }
        });
        taskSequence.addTask("Cleaning Up", () -> {
            final FileHandle assetsFolder = this.files.local("assets/");
            final FileHandle dataFolder = this.files.local("data/");
            assetsFolder.deleteDirectory();
            dataFolder.deleteDirectory();
        });

        final ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(taskSequence.progressProperty());
        final Label messageLabel = new Label();
        messageLabel.textProperty().bind(taskSequence.messageProperty());

        final VBox vBox = new VBox(Constants.SPACING, progressBar, messageLabel);
        vBox.setPadding(new Insets(Constants.PADDING));

        progressStage.setScene(new Scene(vBox));
        progressStage.initOwner(this.primaryStage);
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setResizable(false);
        progressStage.setOnCloseRequest(Event::consume);
        progressStage.setTitle("Running Game");
        taskSequence.setOnScheduled(event -> progressStage.show());
        taskSequence.setOnSucceeded(event -> {
            progressStage.close();

        });

        taskSequence.start();
    }

    private <T extends Asset> void copyAssetCollectionToLocal(Collection<T> assets, Function<T, String> pathCreator) {
        assets.forEach(asset -> {
            final String path = pathCreator.apply(asset);
            final FileHandle sourceFile = this.currentProject.getProjectFileHandle().sibling(path);
            final FileHandle targetFile = this.files.local(path);
            sourceFile.copyTo(targetFile);
        });
    }

    private EventHandler<WindowEvent> defaultStageCloseEventListener() {
        return event -> {
            this.jukebox.stopAll();
            this.jukebox.dispose();
        };
    }

    public static class Constants {
        public static final double SPACING = 5.0;
        public static final double PADDING = 10.0;
        public static final String PROJECT_FILE_EXTENSION = "tcgproj";
        public static final double EDITOR_WIDTH = 1280.0;
        public static final double EDITOR_HEIGHT = 720.0;
        public static final String ASSET_LIB_FILE_NAME = "asset_lib.json";
        public static final String ASSETS_FOLDER_NAME = "assets/";
        public static final String SYSTEM_FILE_NAME = "system.json";
        public static final int ASSET_MANAGER_WIDTH = 600;
        public static final int ASSET_MANAGER_HEIGHT = 600;
        public static final int REQUIRED_WINDOW_SKIN_SIZE = 192;
        public static final String IMAGE_PREVIEW_BACKGROUND = "#000080";
    }

}
