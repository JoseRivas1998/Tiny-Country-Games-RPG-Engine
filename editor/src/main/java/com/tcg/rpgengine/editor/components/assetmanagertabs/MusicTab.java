package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.editor.components.SoundAssetListView;
import com.tcg.rpgengine.editor.components.fontawesome.Icon;
import com.tcg.rpgengine.editor.components.fontawesome.Icons;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import com.tcg.rpgengine.editor.utils.ExtensionUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MusicTab extends Tab {

    private final Button remove;
    private final SoundAssetListView soundAssetListView;
    private final Button preview;
    private final Slider volumeSlider;

    private SoundAsset selectedMusicAsset;

    public MusicTab(Stage owner) {
        super("Music");
        this.setClosable(false);
        this.remove = this.buildRemoveButton(owner);
        this.soundAssetListView = this.buildSoundAssetListView();
        this.preview = this.buildPreviewButton(owner);


        final GridPane gridPane = new GridPane();
        gridPane.setVgap(ApplicationContext.Constants.SPACING);
        gridPane.setHgap(ApplicationContext.Constants.SPACING);
        gridPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        gridPane.add(this.soundAssetListView, 0, 0);

        this.volumeSlider = new Slider(0f, 100f, 90f);
        final HBox volumeBox = new HBox(ApplicationContext.Constants.SPACING,
                new Icon(Icons.FA_VOLUME_UP),
                this.volumeSlider);

        final VBox rightMenu = new VBox(ApplicationContext.Constants.SPACING,
                this.buildImportButton(owner),
                this.remove,
                this.preview,
                volumeBox);
        gridPane.add(rightMenu, 1, 0);
        this.setContent(gridPane);

    }

    private Button buildPreviewButton(Stage owner) {
        final Button preview = new Button("Preview");
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setDisable(true);
        preview.setOnAction(event -> this.playSelectedSong(owner));
        return preview;
    }

    private void playSelectedSong(Stage owner) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            final SoundAsset toPlay = Objects.requireNonNull(this.selectedMusicAsset);
            context.jukebox.stopAll();
            context.jukebox.playMusic(toPlay, (float) this.volumeSlider.getValue() / 100.0f);
        } catch (Exception e) {
            e.printStackTrace();
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Button buildImportButton(Stage owner) {
        final Button importButton = new Button("Import");
        importButton.setOnAction(event -> this.importMusic(owner));
        importButton.setMaxWidth(Double.MAX_VALUE);
        return importButton;
    }

    private void importMusic(Stage owner) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ExtensionUtils.supportedSoundFiles());
        final Optional<File> selectedFileOptional = Optional.ofNullable(fileChooser.showOpenDialog(owner));
        selectedFileOptional.ifPresent(selectedFile -> {
            final ApplicationContext context = ApplicationContext.context();
            try {
                final SoundAsset musicAsset = this.createSoundAsset(owner, selectedFile);
                final CurrentProject currentProject = context.currentProject;
                currentProject.assetLibrary.addMusicAsset(musicAsset);
                currentProject.saveAssetLibrary();
                this.soundAssetListView.getItems().add(musicAsset);
            } catch (Exception e) {
                e.printStackTrace();
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(owner);
                errorDialog.showAndWait();
            }
        });
    }

    private void setDisableToAllButtons(boolean disabled) {
        this.remove.setDisable(disabled);
        this.preview.setDisable(disabled);
    }

    private SoundAsset createSoundAsset(Stage owner, File selectedFile) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final FileHandle selectedFileHandle = this.validateSelectedFile(selectedFile);
        final FileHandle projectFile = currentProject.getProjectFileHandle();
        final String musicTitle = this.inputMusicTitle(selectedFileHandle, owner);
        final float musicDuration = AssetUtils.audioFileLength(selectedFileHandle);
        final FileHandle assetsFolder = projectFile.sibling(ApplicationContext.Constants.ASSETS_FOLDER_NAME);
        final FileHandle assetFile = assetsFolder.child(selectedFileHandle.name());
        final String musicPath = assetFile.path().substring(projectFile.parent().path().length() + 1);
        selectedFileHandle.copyTo(assetFile);
        return SoundAsset.generateNewSoundAsset(musicTitle, musicPath, musicDuration);
    }

    private String inputMusicTitle(FileHandle selectedFileHandle, Stage owner) {
        String musicTitle = null;
        do {
            final String initialName = selectedFileHandle.nameWithoutExtension();
            final TextInputDialog textInputDialog = new TextInputDialog(initialName);
            textInputDialog.setTitle("Import Music");
            textInputDialog.setHeaderText(null);
            textInputDialog.setContentText("Music Title:");
            textInputDialog.initOwner(owner);
            final Optional<String> titleOptional = textInputDialog.showAndWait();
            if (titleOptional.isPresent() && !titleOptional.get().isBlank()) {
                musicTitle = titleOptional.get();
            } else {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("You must enter a music title.");
                alert.setContentText(null);
                alert.initOwner(owner);
                alert.showAndWait();
            }
        } while (musicTitle == null);
        return musicTitle;
    }

    private FileHandle validateSelectedFile(File selectedFile) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle selectedFileHandle = context.files.absolute(selectedFile.getAbsolutePath());
        if (!selectedFileHandle.exists()) {
            throw new IllegalArgumentException("Selected file does not exist.");
        }
        if (!ExtensionUtils.soundExtensionMatches(selectedFileHandle.extension())) {
            throw new IllegalArgumentException("Selected file type is not supported.");
        }
        return selectedFileHandle;
    }

    private Button buildRemoveButton(Stage owner) {
        final Button remove = new Button("Remove");
        remove.setDisable(true);
        remove.setMaxWidth(Double.MAX_VALUE);
        remove.setOnAction(event -> this.removeSelectedMusic(owner));
        return remove;
    }

    private void removeSelectedMusic(Stage owner) {
        final ApplicationContext context = ApplicationContext.context();
        try {
            context.jukebox.stopAll();
            final SoundAsset toRemove = Objects.requireNonNull(this.selectedMusicAsset);
            context.currentProject.assetLibrary.deleteMusicAsset(toRemove);
            final FileHandle projectFileHandle = context.currentProject.getProjectFileHandle();
            final FileHandle soundAssetFile = projectFileHandle.sibling(toRemove.path);
            soundAssetFile.delete();
            context.currentProject.saveAssetLibrary();
            this.soundAssetListView.getItems().remove(toRemove);
            this.selectedMusicAsset = null;
            this.setDisableToAllButtons(true);
        } catch (Exception e) {
            e.printStackTrace();
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private SoundAssetListView buildSoundAssetListView() {
        final SoundAssetListView soundAssetListView = new SoundAssetListView();
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.updateSoundAssetListViewItems(soundAssetListView, assetLibrary);

        soundAssetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.selectedMusicAsset = newValue;
                this.setDisableToAllButtons(false);
            } else {
                this.setDisableToAllButtons(true);
            }
        });

        GridPane.setVgrow(soundAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(soundAssetListView, Priority.ALWAYS);
        return soundAssetListView;
    }

    private void updateSoundAssetListViewItems(SoundAssetListView soundAssetListView, AssetLibrary assetLibrary) {
        final Comparator<SoundAsset> comparingTitle = Comparator.comparing(soundAsset -> soundAsset.title);
        final List<SoundAsset> allMusicAssets = assetLibrary.getAllMusicAssetsSorted(comparingTitle);
        soundAssetListView.getItems().setAll(allMusicAssets);
    }

}
