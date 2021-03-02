package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import com.tcg.rpgengine.editor.utils.ExtensionUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class TiledImageTab extends Tab {

    protected final SimpleAssetListView<TiledImageAsset> tiledImageListView;

    public TiledImageTab(Window owner, String title) {
        super(title);

        this.tiledImageListView = this.buildTiledImageListView();

        final GridPane layout = new GridPane();
        layout.setHgap(ApplicationContext.Constants.SPACING);
        layout.setVgap(ApplicationContext.Constants.SPACING);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        final Button remove = new Button("Remove");
        remove.setMaxWidth(Double.MAX_VALUE);
        remove.disableProperty().bind(this.tiledImageListView.getSelectionModel().selectedItemProperty().isNull());
        remove.setOnAction(event -> this.removeSelectedTiledImage(owner));

        final VBox buttons = new VBox(ApplicationContext.Constants.SPACING);
        buttons.getChildren().addAll(
                this.buildImportButton(owner),
                remove,
                this.buildPreviewButton(owner)
        );

        layout.add(this.tiledImageListView, 0, 0);
        layout.add(buttons, 1, 0);

        this.setContent(layout);
        this.setClosable(false);
    }

    private void removeSelectedTiledImage(Window owner) {
        this.getSelectedTiledImage().ifPresent(tiledImageAsset -> {
            try {
                final CurrentProject currentProject = ApplicationContext.context().currentProject;
                final FileHandle imageFile = currentProject.getProjectFileHandle().sibling(tiledImageAsset.getPath());
                imageFile.delete();
                this.removeFromAssetLibrary(tiledImageAsset);
                currentProject.saveAssetLibrary();
                final int selectedIndex = this.tiledImageListView.getSelectionModel().getSelectedIndex();
                this.tiledImageListView.getItems().remove(selectedIndex);
            } catch (Exception e) {
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(owner);
                errorDialog.showAndWait();
            }
        });
    }

    protected abstract void removeFromAssetLibrary(TiledImageAsset tiledImageAsset);

    private Button buildPreviewButton(Window owner) {
        final Button preview = new Button("Preview");
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.disableProperty().bind(this.tiledImageListView.getSelectionModel().selectedItemProperty().isNull());
        preview.setOnAction(event -> this.previewSelectedAsset(owner));
        return preview;
    }

    private void previewSelectedAsset(Window owner) {
        this.getSelectedTiledImage().ifPresent(tiledImageAsset -> this.previewAsset(owner, tiledImageAsset));
    }

    private void previewAsset(Window owner, TiledImageAsset tiledImageAsset) {
        try {
            this.showPreviewDialog(owner, tiledImageAsset);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    protected abstract void showPreviewDialog(Window owner, TiledImageAsset tiledImageAsset);

    private Optional<TiledImageAsset> getSelectedTiledImage() {
        return Optional.ofNullable(this.tiledImageListView.getSelectionModel().getSelectedItem());
    }

    private Button buildImportButton(Window owner) {
        final Button importBtn = new Button("Import");
        importBtn.setOnAction(event -> this.importTiledImage(owner));
        importBtn.setMaxWidth(Double.MAX_VALUE);
        return importBtn;
    }

    private void importTiledImage(Window owner) {
        try {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().setAll(ExtensionUtils.supportedImageFiles());
            final Optional<File> optionalSelectedFile = Optional.ofNullable(fileChooser.showOpenDialog(owner));
            optionalSelectedFile.ifPresent(selectedFile -> {
                this.importSelectedFile(owner, selectedFile);
            });
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void importSelectedFile(Window owner, File selectedFile) {
        final FileHandle selectedFileHandle = this.validateSelectedFileToFileHandle(selectedFile);
        final Optional<Pair<Integer, Integer>> optionalRowColPair = this.inputTiledImageValues(
                owner, selectedFileHandle);
        optionalRowColPair.ifPresent(rowColPair -> {
            final int rows = rowColPair.getKey();
            final int cols = rowColPair.getValue();
            final TiledImageAsset tiledImage = this.createTiledImageAsset(selectedFileHandle, rows, cols);
            this.addToAssetLibrary(tiledImage);
            ApplicationContext.context().currentProject.saveAssetLibrary();
            this.tiledImageListView.getItems().add(tiledImage);
        });
    }

    protected abstract Optional<Pair<Integer, Integer>> inputTiledImageValues(Window owner, FileHandle selectedFileHandle);

    protected abstract void addToAssetLibrary(TiledImageAsset tiledImage);

    private SimpleAssetListView<TiledImageAsset> buildTiledImageListView() {
        final SimpleAssetListView<TiledImageAsset> pageAssetSimpleAssetListView = new SimpleAssetListView<>(
                TiledImageAsset::getPath
        );
        pageAssetSimpleAssetListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pageAssetSimpleAssetListView.getItems().setAll(this.getTiledImageAssets());
        GridPane.setVgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);
        return pageAssetSimpleAssetListView;
    }

    protected abstract List<TiledImageAsset> getTiledImageAssets();

    private TiledImageAsset createTiledImageAsset(FileHandle selectedFileHandle, int rows, int cols) {
        if (rows <= 0) {
            throw new IllegalArgumentException("Tiled image must have positive rows.");
        }
        if (cols <= 0) {
            throw new IllegalArgumentException("Tiled image must have positive columns.");
        }
        final String path = AssetUtils.importExternalFileIntoAssetsFolder(selectedFileHandle);
        return TiledImageAsset.createNewTiledImageAsset(path, rows, cols);
    }

    private FileHandle validateSelectedFileToFileHandle(File selectedFile) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle selectedFileHandle = context.files.absolute(selectedFile.getAbsolutePath());
        if (!selectedFileHandle.exists()) {
            throw new IllegalArgumentException("Selected file does not exists.");
        }
        if (!ExtensionUtils.imageExtensionMatches(selectedFileHandle.extension())) {
            throw new IllegalArgumentException("Image file format not supported.");
        }
        return selectedFileHandle;
    }

}
