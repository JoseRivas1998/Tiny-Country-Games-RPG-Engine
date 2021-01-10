package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.ExtensionUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Optional;

public class ImageTab extends Tab {

    private final SimpleAssetListView<ImageAsset> imageAssetListView;
    private final Button remove;
    private final Button preview;

    public ImageTab(Stage owner) {
        super("Images");

        this.imageAssetListView = this.buildImageAssetListView();
        this.remove = this.buildRemoveButton(owner);

        final Button preview = new Button("Preview");
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setDisable(true);
        preview.setOnAction(event -> {
            this.selectedImage().ifPresent(selectedImage -> {
                try {
                    final ApplicationContext context = ApplicationContext.context();
                    final FileHandle projectFileHandle = context.currentProject.getProjectFileHandle();
                    final FileHandle imageFile = projectFileHandle.sibling(selectedImage.path);
                    final Image image = new Image(imageFile.read());
                    final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(imageFile.name());
                    alert.setHeaderText(null);
                    alert.setContentText(null);
                    alert.setGraphic(null);
                    final ImageView imageView = new ImageView(image);
                    alert.getDialogPane().setContent(imageView);
                    alert.getDialogPane().setPadding(new Insets(ApplicationContext.Constants.PADDING));
                    alert.initOwner(owner);
                    alert.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    final ErrorDialog errorDialog = new ErrorDialog(e);
                    errorDialog.initOwner(owner);
                    errorDialog.showAndWait();
                }
            });
        });


        this.preview = preview;

        final VBox buttonBox = new VBox(ApplicationContext.Constants.SPACING);
        buttonBox.getChildren().addAll(
                this.buildImportButton(owner),
                this.remove,
                this.preview);
        GridPane.setVgrow(buttonBox, Priority.ALWAYS);


        final GridPane layout = new GridPane();
        layout.setVgap(ApplicationContext.Constants.SPACING);
        layout.setHgap(ApplicationContext.Constants.SPACING);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        layout.add(this.imageAssetListView, 0, 0);
        layout.add(buttonBox, 1, 0);

        this.setContent(layout);
        this.setClosable(false);
    }

    private Button buildRemoveButton(Stage owner) {
        final Button remove = new Button("Remove");
        remove.setMaxWidth(Double.MAX_VALUE);
        remove.setDisable(true);
        remove.setOnAction(event -> this.removeSelectedImage(owner));
        return remove;
    }

    private void removeSelectedImage(Stage owner) {
        this.selectedImage().ifPresent(selectedImage -> {
            try {
                final ApplicationContext context = ApplicationContext.context();
                context.currentProject.assetLibrary.deleteImageAsset(selectedImage);
                final FileHandle projectFileHandle = context.currentProject.getProjectFileHandle();
                final FileHandle imageAssetFile = projectFileHandle.sibling(selectedImage.path);
                imageAssetFile.delete();
                context.currentProject.saveAssetLibrary();
                this.imageAssetListView.getItems().remove(selectedImage);
                this.setAllButtonsDisable(true);
            } catch (Exception e) {
                e.printStackTrace();
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(owner);
                errorDialog.showAndWait();
            }
        });
    }

    private Button buildImportButton(Stage owner) {
        final Button importButton = new Button("Import");
        importButton.setMaxWidth(Double.MAX_VALUE);
        importButton.setOnAction(event -> this.importImage(owner));
        return importButton;
    }

    private void importImage(Stage owner) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(ExtensionUtils.supportedImageFiles());
        final Optional<File> selectedFileOptional = Optional.ofNullable(fileChooser.showOpenDialog(owner));
        selectedFileOptional.ifPresent(selectedFile -> {
            try {
                final ImageAsset imageAsset = this.createImageAsset(selectedFile);
                final CurrentProject currentProject = ApplicationContext.context().currentProject;
                currentProject.assetLibrary.addImageAsset(imageAsset);
                currentProject.saveAssetLibrary();
                this.imageAssetListView.getItems().add(imageAsset);
            } catch (Exception e) {
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(owner);
                errorDialog.showAndWait();
            }
        });
    }

    private ImageAsset createImageAsset(File selectedFile) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final FileHandle selectedFileHandle = this.validateSelectedFile(selectedFile);
        final FileHandle projectFile = currentProject.getProjectFileHandle();
        final FileHandle assetsFolder = projectFile.sibling(ApplicationContext.Constants.ASSETS_FOLDER_NAME);
        final String assetFileName = selectedFileHandle.name()
                .trim().toLowerCase().replaceAll("\\s", "_");
        final FileHandle assetFile = assetsFolder.child(assetFileName);
        final String imagePath = assetFile.path().substring(projectFile.parent().path().length() + 1);
        selectedFileHandle.copyTo(assetFile);
        return ImageAsset.generateNewImageAsset(imagePath);
    }

    private FileHandle validateSelectedFile(File selectedFile) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle selectedFileHandle = context.files.absolute(selectedFile.getAbsolutePath());
        if (!selectedFileHandle.exists()) {
            throw new IllegalArgumentException("Selected file does not exist.");
        }
        if (!ExtensionUtils.imageExtensionMatches(selectedFileHandle.extension())) {
            throw new IllegalArgumentException("Image file format not supported.");
        }
        return selectedFileHandle;
    }

    private SimpleAssetListView<ImageAsset> buildImageAssetListView() {
        final ApplicationContext context = ApplicationContext.context();
        final SimpleAssetListView<ImageAsset> imageAssetListView = new SimpleAssetListView<>(image -> image.path);
        imageAssetListView.getItems().setAll(context.currentProject.assetLibrary.getAllImageAssets());
        imageAssetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.setAllButtonsDisable(Optional.ofNullable(newValue).isEmpty());
        });
        GridPane.setVgrow(imageAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(imageAssetListView, Priority.ALWAYS);
        return imageAssetListView;
    }

    private void setAllButtonsDisable(boolean disable) {
        this.remove.setDisable(disable);
        this.preview.setDisable(disable);
    }

    private Optional<ImageAsset> selectedImage() {
        return Optional.ofNullable(this.imageAssetListView.getSelectionModel().getSelectedItem());
    }

}
