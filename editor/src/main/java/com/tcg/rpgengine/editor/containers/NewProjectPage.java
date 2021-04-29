package com.tcg.rpgengine.editor.containers;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.Project;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.Database;
import com.tcg.rpgengine.common.data.database.entities.Actor;
import com.tcg.rpgengine.common.data.database.entities.Element;
import com.tcg.rpgengine.common.data.misc.IconCell;
import com.tcg.rpgengine.common.data.misc.SpritesheetCharacter;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.common.data.system.UISounds;
import com.tcg.rpgengine.common.data.system.WindowSkin;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class NewProjectPage extends BorderPane {

    private final BorderPane parent;
    private final TextField titleField;
    private final TextField destinationField;

    public NewProjectPage(BorderPane parent) {
        super();
        this.parent = parent;
        this.titleField = new TextField();
        this.destinationField = new TextField();
        this.setCenter(this.buildMainForm());
        this.setBottom(this.buildButtonRow());

    }

    private HBox buildButtonRow() {
        final HBox buttonRow = new HBox(ApplicationContext.Constants.SPACING);
        buttonRow.getChildren().addAll(this.buildCreateButton(), this.buildCancelButton());
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        return buttonRow;
    }

    private Button buildCreateButton() {
        final Button create = new Button("Create");
        create.setOnAction(event -> this.validateAndGenerateProjectFile());
        return create;
    }

    private void validateAndGenerateProjectFile() {
        final ApplicationContext context = ApplicationContext.context();
        try {
            this.validateInputFields();
            final FileHandle destination = context.files.absolute(this.destinationField.getText());
            this.validateDestinationFolder(destination);
            if (this.confirmNonEmptyFolder(destination)) {
                final String projTitle = this.titleField.getText();

                final FileHandle projectFile = this.generateProjectFile(projTitle, destination);
                final FileHandle assetsFolder = projectFile.sibling(ApplicationContext.Constants.ASSETS_FOLDER_NAME);
                final FileHandle dataFolder = projectFile.sibling(ApplicationContext.Constants.DATA_FOLDER_NAME);

                final SoundAsset theme6 = this.copyTheme6IntoProjectFolder(projectFile, assetsFolder);
                final ImageAsset titleImage = this.copyImageIntoProjectFolder(projectFile, assetsFolder,
                        "initial_assets/title.png");

                final SoundAsset cursor2 = this.copyAudioToProjectFolder(projectFile, assetsFolder,
                        "initial_assets/cursor2.mp3");
                final SoundAsset decision1 = this.copyAudioToProjectFolder(projectFile, assetsFolder,
                        "initial_assets/decision1.mp3");
                final SoundAsset cancel1 = this.copyAudioToProjectFolder(projectFile, assetsFolder,
                        "initial_assets/cancel2.mp3");
                final SoundAsset buzzer1 = this.copyAudioToProjectFolder(projectFile, assetsFolder,
                        "initial_assets/buzzer1.mp3");

                final ImageAsset uiSkinImage = this.copyImageIntoProjectFolder(projectFile, assetsFolder,
                        "initial_assets/ui_skin.png");


                final TiledImageAsset defaultIconSet = this.copyTiledImageIntoProjectFolder(projectFile, assetsFolder,
                        "initial_assets/iconset.png", 39, 16);

                final TiledImageAsset terrySpritesheet = this.copyTiledImageIntoProjectFolder(projectFile, assetsFolder,
                        "initial_assets/terry_spritesheet.png", 1, 1);

                final AssetLibrary assetLibrary = AssetLibrary.newAssetLibrary();
                assetLibrary.addMusicAsset(theme6);
                assetLibrary.addImageAsset(titleImage);

                assetLibrary.addSoundEffectAsset(cursor2);
                assetLibrary.addSoundEffectAsset(decision1);
                assetLibrary.addSoundEffectAsset(cancel1);
                assetLibrary.addSoundEffectAsset(buzzer1);

                assetLibrary.addImageAsset(uiSkinImage);

                assetLibrary.addIconPageAsset(defaultIconSet);

                assetLibrary.addSpritesheetPageAsset(terrySpritesheet);

                final FileHandle assetLibraryFile = projectFile.sibling(
                        ApplicationContext.Constants.ASSET_LIB_FILE_NAME
                );
                assetLibraryFile.writeString(assetLibrary.jsonString(4), false);

                final Title initialTitle = Title.createNewTitle(assetLibrary, projTitle, titleImage.id, theme6.id);
                final UISounds initialUiSounds = UISounds.createNewUISounds(assetLibrary, cursor2.id, decision1.id,
                        cancel1.id, buzzer1.id);
                final WindowSkin windowSkin = WindowSkin.createWindowSkin(assetLibrary, uiSkinImage.id);

                final JSONObject initialContent = this.loadInitialContent(context);

                final Database database = new Database(assetLibrary);
                this.loadInitialElements(defaultIconSet, assetLibrary, initialContent, database);

                final FileHandle elementsFile = dataFolder.child(ApplicationContext.Constants.ELEMENTS_FILE_NAME);
                elementsFile.writeString(database.elements.jsonString(4), false);

                database.actors.add(this.createTerryActor(terrySpritesheet, assetLibrary));
                final FileHandle actorsFile = dataFolder.child(ApplicationContext.Constants.ACTORS_FILE_NAME);
                actorsFile.writeString(database.actors.jsonString(4), false);

                final SystemData systemData = SystemData.createNewSystemData(initialTitle, initialUiSounds, windowSkin,
                        database.actors.getAll());

                final FileHandle systemFile = projectFile.sibling(ApplicationContext.Constants.SYSTEM_FILE_NAME);
                systemFile.writeString(systemData.jsonString(4), false);

                ApplicationContext.context().openProject(projectFile);
            }
        } catch (Exception exception) {
            final ErrorDialog errorDialog = new ErrorDialog(exception);
            errorDialog.initOwner(context.primaryStage);
            errorDialog.showAndWait();
        }
    }

    private Actor createTerryActor(TiledImageAsset terrySpritesheet, AssetLibrary assetLibrary) {
        final SpritesheetCharacter character = SpritesheetCharacter.createNewSpritesheetCharacter(assetLibrary,
                terrySpritesheet.id, 0, 0);
        return Actor.createNewActor("Terry", character);
    }

    private void loadInitialElements(TiledImageAsset defaultIconSet, AssetLibrary assetLibrary,
                                     JSONObject initialContent, Database database) {
        final JSONArray elements = initialContent.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            final JSONObject element = elements.getJSONObject(i);
            final int row = element.getInt("row");
            final int column = element.getInt("column");
            final IconCell iconCell = IconCell.createNewIconCell(assetLibrary, defaultIconSet.id, row, column);
            database.elements.add(Element.createNewElement(element.getString("name"), iconCell));
        }
    }

    private JSONObject loadInitialContent(ApplicationContext context) {
        return new JSONObject(context.files.internal("initial_assets/initial_content.json").readString());
    }

    private TiledImageAsset copyTiledImageIntoProjectFolder(FileHandle projectFile, FileHandle assetsFolder,
                                                            String internalPath, int rows, int columns) {
        final String imagePath = this.copyInternalFileToAssetsFolder(projectFile, assetsFolder, internalPath);
        return TiledImageAsset.createNewTiledImageAsset(imagePath, rows, columns);
    }

    private String copyInternalFileToAssetsFolder(FileHandle projectFile, FileHandle assetsFolder, String internalPath) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle internalFile = context.files.internal(internalPath);
        final FileHandle assetFile = assetsFolder.child(internalFile.name());
        internalFile.copyTo(assetFile);
        return AssetUtils.getFilePathRelativeTo(assetFile, projectFile.parent());
    }

    private SoundAsset copyAudioToProjectFolder(FileHandle projectFile, FileHandle assetsFolder, String internalPath) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle internalFile = context.files.internal(internalPath);
        final FileHandle assetFile = assetsFolder.child(internalFile.name());
        internalFile.copyTo(assetFile);
        final String assetPath = assetFile.path().substring(projectFile.parent().path().length() + 1);
        final float assetDuration = AssetUtils.audioFileLength(assetFile);
        return SoundAsset.generateNewSoundAsset(assetFile.nameWithoutExtension(), assetPath, assetDuration);
    }

    private ImageAsset copyImageIntoProjectFolder(FileHandle projectFile, FileHandle assetsFolder, String path) {
        final String imagePath = this.copyInternalFileToAssetsFolder(projectFile, assetsFolder, path);
        return ImageAsset.generateNewImageAsset(imagePath);
    }

    private SoundAsset copyTheme6IntoProjectFolder(FileHandle projectFile, FileHandle assetsFolder) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle initialTheme6File = context.files.internal("initial_assets/theme6.mp3");
        final FileHandle theme6AssetFile = assetsFolder.child(initialTheme6File.name());
        initialTheme6File.copyTo(theme6AssetFile);
        final String theme6Path = theme6AssetFile.path().substring(projectFile.parent().path().length() + 1);
        final float theme6Duration = AssetUtils.audioFileLength(theme6AssetFile);
        return SoundAsset.generateNewSoundAsset("Theme 6", theme6Path, theme6Duration);
    }

    private FileHandle generateProjectFile(String title, FileHandle destination) {
        final String formattedTitle = title.trim().toLowerCase().replaceAll("\\s+", "-");
        final String projectFileName = String.format("%s.%s",
                formattedTitle, ApplicationContext.Constants.PROJECT_FILE_EXTENSION);
        final FileHandle projectFile = destination.child(projectFileName);
        this.deleteProjectFileIfExists(projectFile);
        final Project project = Project.generateNewProject(title);
        projectFile.writeString(project.jsonString(4), false, "UTF-8");
        return projectFile;
    }

    private void deleteProjectFileIfExists(FileHandle projectFile) {
        if(projectFile.exists()) {
            projectFile.delete();
        }
    }

    private void validateDestinationFolder(FileHandle destination) {
        this.verifyDestinationExists(destination);
        this.verifyDestinationIsDirectory(destination);
    }

    private void validateInputFields() {
        this.verifyTitleFieldIsNotBlank();
        this.verifyDestinationFieldIsNotBlank();
    }

    private boolean confirmNonEmptyFolder(FileHandle destination) {
        boolean shouldContinue = true;
        if (destination.list().length > 0) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Destination Not Empty");
            alert.setHeaderText(null);
            alert.setContentText("The destination folder is not empty, would you like to continue creating project?");
            alert.initOwner(ApplicationContext.context().primaryStage);
            final Optional<ButtonType> result = alert.showAndWait();
            shouldContinue = result.isPresent() && result.get() == ButtonType.OK;
        }
        return shouldContinue;
    }

    private void verifyDestinationIsDirectory(FileHandle destination) {
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException("Destination must be a folder.");
        }
    }

    private void verifyDestinationExists(FileHandle destination) {
        if (!destination.exists()) {
            throw new IllegalArgumentException("Destination must exist.");
        }
    }

    private void verifyDestinationFieldIsNotBlank() {
        if (this.destinationField.getText().isBlank()) {
            throw new IllegalArgumentException("Destination field cannot be blank.");
        }
    }

    private void verifyTitleFieldIsNotBlank() {
        if (this.titleField.getText().isBlank()) {
            throw new IllegalArgumentException("Title field cannot be blank.");
        }
    }

    private Button buildCancelButton() {
        final Button cancel = new Button("Cancel");
        cancel.setOnAction(event -> this.parent.setCenter(new WelcomePage(this.parent)));
        return cancel;
    }

    private GridPane buildMainForm() {
        final GridPane mainForm = new GridPane();
        mainForm.setHgap(ApplicationContext.Constants.SPACING);
        mainForm.setVgap(ApplicationContext.Constants.SPACING);
        this.addTitleField(mainForm);
        this.addDestinationField(mainForm);
        return mainForm;
    }

    private void addDestinationField(GridPane mainForm) {
        mainForm.add(new Label("Destination:"), 0, 1);
        GridPane.setHgrow(this.destinationField, Priority.ALWAYS);
        mainForm.add(this.destinationField, 1, 1);
        mainForm.add(this.buildSelectButton(), 2, 1);
    }

    private void addTitleField(GridPane mainForm) {
        mainForm.add(new Label("Project Title:"), 0, 0);
        GridPane.setHgrow(this.titleField, Priority.ALWAYS);
        GridPane.setColumnSpan(this.titleField, 2);
        mainForm.add(this.titleField, 1, 0);
    }

    private Button buildSelectButton() {
        final Button select = new Button("Select");
        select.setOnAction(event -> {
            final Optional<String> optionalDirectory = this.selectDirectory();
            optionalDirectory.ifPresent(selectedDirectory -> {
                if (this.titleField.getText().isBlank()) {
                    final ApplicationContext context = ApplicationContext.context();
                    final FileHandle selectedDirectoryHandle = context.files.absolute(selectedDirectory);
                    this.titleField.setText(selectedDirectoryHandle.name());
                }
                this.destinationField.setText(selectedDirectory);
            });
        });
        return select;
    }

    private Optional<String> selectDirectory() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle homeDirectory = context.files.external("");
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(homeDirectory.file());
        final File file = directoryChooser.showDialog(context.primaryStage);
        return file == null ? Optional.empty() : Optional.of(file.getAbsolutePath());
    }
}
