package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.SpritesheetDialog;
import com.tcg.rpgengine.editor.dialogs.SpritesheetPreviewDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import com.tcg.rpgengine.editor.utils.ExtensionUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.util.Optional;

public class SpritesheetPagesTab extends Tab {

    private final SimpleAssetListView<TiledImageAsset> pageAssetSimpleAssetListView;

    public SpritesheetPagesTab(Window owner) {
        super("Spritesheet Pages");
        this.pageAssetSimpleAssetListView = this.buildSpritesheetPageAssetListView();

        final VBox vBox = new VBox(ApplicationContext.Constants.SPACING);
        vBox.getChildren().addAll(
                this.buildImportButton(owner),
                this.buildRemoveButton(owner),
                this.buildPreviewButton(owner)
        );

        final GridPane layout = new GridPane();
        layout.setHgap(ApplicationContext.Constants.SPACING);
        layout.setVgap(ApplicationContext.Constants.SPACING);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        layout.add(this.pageAssetSimpleAssetListView, 0, 0);
        layout.add(vBox, 1, 0);

        this.setContent(layout);
        this.setClosable(false);

    }

    private Button buildRemoveButton(Window owner) {
        final Button remove = new Button("Remove");
        remove.setMaxWidth(Double.MAX_VALUE);
        remove.disableProperty().bind(this.selectionIsNullBinding());
        remove.setOnAction(event -> this.removeSelectedSpritesheetAsset(owner));
        return remove;
    }

    private void removeSelectedSpritesheetAsset(Window owner) {
        this.getSelectedSpritesheet()
                .ifPresent(spritesheetPageAsset -> this.removeSpritesheetAsset(owner, spritesheetPageAsset));
    }

    private void removeSpritesheetAsset(Window owner, TiledImageAsset tiledImageAsset) {
        try {
            final CurrentProject currentProject = ApplicationContext.context().currentProject;
            currentProject.assetLibrary.deleteSpritesheetPageAsset(tiledImageAsset);
            currentProject.saveAssetLibrary();
            final FileHandle assetFile = currentProject.getProjectFileHandle().sibling(tiledImageAsset.getPath());
            assetFile.delete();
            final int selectedIndex = this.pageAssetSimpleAssetListView.getSelectionModel().getSelectedIndex();
            this.pageAssetSimpleAssetListView.getItems().remove(selectedIndex);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Button buildPreviewButton(Window owner) {
        final Button preview = new Button("Preview");
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.disableProperty().bind(this.selectionIsNullBinding());
        preview.setOnAction(event -> this.previewSelectedAsset(owner));
        return preview;
    }

    private BooleanBinding selectionIsNullBinding() {
        return this.pageAssetSimpleAssetListView.getSelectionModel().selectedItemProperty().isNull();
    }

    private void previewSelectedAsset(Window owner) {
        this.getSelectedSpritesheet().ifPresent(selectedAsset -> this.previewAsset(owner, selectedAsset));
    }

    private void previewAsset(Window owner, TiledImageAsset selectedAsset) {
        try {
            final SpritesheetPreviewDialog previewDialog = new SpritesheetPreviewDialog(selectedAsset);
            previewDialog.initOwner(owner);
            previewDialog.initModality(Modality.APPLICATION_MODAL);
            previewDialog.showAndWait();
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Optional<TiledImageAsset> getSelectedSpritesheet() {
        return Optional.ofNullable(this.pageAssetSimpleAssetListView.getSelectionModel().getSelectedItem());
    }

    private Button buildImportButton(Window owner) {
        final Button importButton = new Button("Import");
        importButton.setMaxWidth(Double.MAX_VALUE);
        importButton.setOnAction(event -> this.importSpritesheetPage(owner));
        return importButton;
    }

    private void importSpritesheetPage(Window owner) {
        try {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().setAll(ExtensionUtils.supportedImageFiles());
            final Optional<File> selectedFileOptional = Optional.ofNullable(fileChooser.showOpenDialog(owner));
            selectedFileOptional.ifPresent(selectedFile -> this.importSpritesheetFromFile(owner, selectedFile));
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void importSpritesheetFromFile(Window owner, File selectedFile) {
        final FileHandle selectedFileHandle = this.selectedFileToValidatedFileHandle(selectedFile);
        final SpritesheetDialog spritesheetDialog = new SpritesheetDialog(selectedFileHandle);
        spritesheetDialog.initOwner(owner);
        final Optional<Pair<Integer, Integer>> optionalRowColPair = spritesheetDialog.showAndWait();
        if (optionalRowColPair.isPresent()) {
            final Pair<Integer, Integer> rowColPair = optionalRowColPair.get();
            final TiledImageAsset tiledImageAsset = this.createSpritePageAsset(
                    selectedFileHandle, rowColPair.getKey(), rowColPair.getValue()
            );
            final CurrentProject currentProject = ApplicationContext.context().currentProject;
            currentProject.assetLibrary.addSpritesheetPageAsset(tiledImageAsset);
            currentProject.saveAssetLibrary();
            this.pageAssetSimpleAssetListView.getItems().add(tiledImageAsset);
        }
    }

    private SimpleAssetListView<TiledImageAsset> buildSpritesheetPageAssetListView() {
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final SimpleAssetListView<TiledImageAsset> pageAssetSimpleAssetListView = new SimpleAssetListView<>(
                TiledImageAsset::getPath
        );
        pageAssetSimpleAssetListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pageAssetSimpleAssetListView.getItems().setAll(assetLibrary.getAllSpritesheetPages());
        GridPane.setVgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);
        return pageAssetSimpleAssetListView;
    }

    private TiledImageAsset createSpritePageAsset(FileHandle selectedFileHandle, int rows, int columns) {
        if (rows <= 0) {
            throw new IllegalArgumentException("The number of rows must be at least one.");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("The number of columns must be at least one.");
        }
        final String imagePath = AssetUtils.importExternalFileIntoAssetsFolder(selectedFileHandle);
        return TiledImageAsset.createNewSpritesheetPageAsset(imagePath, rows, columns);
    }

    private FileHandle selectedFileToValidatedFileHandle(File selectedFile) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle selectedFileHandle = context.files.absolute(selectedFile.getAbsolutePath());
        if (!selectedFileHandle.exists()) {
            throw new IllegalArgumentException("Selected file does not exist");
        }
        if (!ExtensionUtils.imageExtensionMatches(selectedFileHandle.extension())) {
            throw new IllegalArgumentException("Image file format not supported");
        }
        return selectedFileHandle;
    }
}
