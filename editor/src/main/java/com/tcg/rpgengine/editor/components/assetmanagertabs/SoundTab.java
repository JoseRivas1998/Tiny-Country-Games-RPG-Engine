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
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SoundTab extends Tab {

    private final SoundAssetListView soundAssetListView;
    private final Slider volumeSlider;

    public SoundTab(Window owner) {
        super("Sound Effects");
        this.setClosable(false);

        this.soundAssetListView = this.buildSoundAssetListView();

        final GridPane gridPane = new GridPane();
        gridPane.setVgap(ApplicationContext.Constants.SPACING);
        gridPane.setHgap(ApplicationContext.Constants.SPACING);
        gridPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        gridPane.add(this.soundAssetListView, 0, 0);

        this.volumeSlider = new Slider(0f, 100f, 90f);
        final HBox volumeBox = new HBox(ApplicationContext.Constants.SPACING);
        volumeBox.getChildren().addAll(
                new Icon(Icons.FA_VOLUME_UP),
                this.volumeSlider
        );

        final VBox rightControls = new VBox(ApplicationContext.Constants.SPACING);
        rightControls.getChildren().addAll(
                this.buildImportButton(owner),
                this.buildRemoveButton(owner),
                this.buildPreviewButton(owner),
                volumeBox
        );

        GridPane.setVgrow(rightControls, Priority.ALWAYS);
        gridPane.add(rightControls, 1, 0);

        this.setContent(gridPane);

    }

    private Button buildPreviewButton(Window owner) {
        final Button preview = new Button("Preview");
        preview.setOnAction(event -> this.playSelectedSoundAsset(owner));
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.disableProperty().bind(this.soundAssetListView.getSelectionModel().selectedItemProperty().isNull());
        return preview;
    }

    private void playSelectedSoundAsset(Window owner) {
        this.getSelectedAsset().ifPresent(selectedAsset -> this.playSoundAsset(owner, selectedAsset));
    }

    private void playSoundAsset(Window owner, SoundAsset toPlay) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            context.jukebox.stopAll();
            context.jukebox.playSoundEffect(toPlay, (float) this.volumeSlider.getValue() / 100f);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Button buildRemoveButton(Window owner) {
        final Button remove = new Button("Remove");
        remove.setOnAction(event -> this.removeSelectedSoundAsset(owner));
        remove.setMaxWidth(Double.MAX_VALUE);
        remove.disableProperty().bind(this.soundAssetListView.getSelectionModel().selectedItemProperty().isNull());
        return remove;
    }

    private void removeSelectedSoundAsset(Window owner) {
        this.getSelectedAsset().ifPresent(selectedAsset -> this.removeSoundAsset(owner, selectedAsset));
    }

    private Optional<SoundAsset> getSelectedAsset() {
        return Optional.of(this.soundAssetListView.getSelectionModel().getSelectedItem());
    }

    private void removeSoundAsset(Window owner, SoundAsset toRemove) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            context.currentProject.assetLibrary.deleteSoundEffectAsset(toRemove);
            final FileHandle projectFile = context.currentProject.getProjectFileHandle();
            final FileHandle assetFile = projectFile.sibling(toRemove.path);
            assetFile.delete();
            context.currentProject.saveAssetLibrary();
            this.soundAssetListView.getItems().remove(toRemove);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Button buildImportButton(Window owner) {
        final Button importButton = new Button("Import");
        importButton.setOnAction(event -> this.importSound(owner));
        importButton.setMaxWidth(Double.MAX_VALUE);
        return importButton;
    }

    private void importSound(Window owner) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(ExtensionUtils.supportedSoundFiles());
        final Optional<File> optionalSelectedFile = Optional.ofNullable(fileChooser.showOpenDialog(owner));
        optionalSelectedFile.ifPresent(selectedFile -> this.importSelectedSoundFile(owner, selectedFile));
    }

    private void importSelectedSoundFile(Window owner, File selectedFile) {
        try {
            final SoundAsset soundAsset = this.importSoundFile(selectedFile);
            final CurrentProject currentProject = ApplicationContext.context().currentProject;
            currentProject.assetLibrary.addSoundEffectAsset(soundAsset);
            currentProject.saveAssetLibrary();
            this.soundAssetListView.getItems().add(soundAsset);
        } catch (Exception e) {
            e.printStackTrace();
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private SoundAsset importSoundFile(File selectedFile) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle projectFile = context.currentProject.getProjectFileHandle();
        final FileHandle selectedFileHandle = this.validateSelectedFile(selectedFile);
        final String soundTitle = selectedFileHandle.nameWithoutExtension();
        final float soundLength = AssetUtils.audioFileLength(selectedFileHandle);
        final FileHandle assetsFolder = projectFile.sibling(ApplicationContext.Constants.ASSETS_FOLDER_NAME);
        final FileHandle assetFile = AssetUtils.getFileAsNonExistent(assetsFolder.child(selectedFileHandle.name()));
        final String soundPath = AssetUtils.getFilePathRelativeTo(assetFile, projectFile.parent());
        selectedFileHandle.copyTo(assetFile);
        return SoundAsset.generateNewSoundAsset(soundTitle, soundPath, soundLength);
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

    private Optional<SoundAsset> getSelectedSound() {
        return Optional.ofNullable(this.soundAssetListView.getSelectionModel().getSelectedItem());
    }

    private SoundAssetListView buildSoundAssetListView() {
        final SoundAssetListView soundAssetListView = new SoundAssetListView();
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        soundAssetListView.getItems().setAll(this.getAllSoundAssetsSorted(assetLibrary));

        GridPane.setVgrow(soundAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(soundAssetListView, Priority.ALWAYS);
        return soundAssetListView;
    }

    private List<SoundAsset> getAllSoundAssetsSorted(AssetLibrary assetLibrary) {
        final Comparator<SoundAsset> comparing = Comparator.comparing(soundAsset -> soundAsset.title);
        return assetLibrary.getAllSoundAssetsSorted(comparing);
    }

}
