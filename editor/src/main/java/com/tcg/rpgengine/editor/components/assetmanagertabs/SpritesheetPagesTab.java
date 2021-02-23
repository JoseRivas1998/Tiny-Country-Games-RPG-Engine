package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.SpritesheetPageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.SpritesheetDialog;
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
import java.util.Optional;

public class SpritesheetPagesTab extends Tab {

    private final SimpleAssetListView<SpritesheetPageAsset> pageAssetSimpleAssetListView;

    public SpritesheetPagesTab(Window owner) {
        super("Spritesheet Pages");

        final SimpleAssetListView<SpritesheetPageAsset> pageAssetSimpleAssetListView = new SimpleAssetListView<>(
                SpritesheetPageAsset::getPath
        );
        pageAssetSimpleAssetListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pageAssetSimpleAssetListView.getItems().setAll(ApplicationContext.context().currentProject.assetLibrary.getAllSpritesheetPages());
        GridPane.setVgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);
        GridPane.setHgrow(pageAssetSimpleAssetListView, Priority.ALWAYS);

        this.pageAssetSimpleAssetListView = pageAssetSimpleAssetListView;


        final Button importButton = new Button("Import");
        importButton.setMaxWidth(Double.MAX_VALUE);
        importButton.setOnAction(event -> {
            try {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().setAll(ExtensionUtils.supportedImageFiles());
                final Optional<File> selectedFileOptional = Optional.ofNullable(fileChooser.showOpenDialog(owner));
                selectedFileOptional.ifPresent(selectedFile -> {
                    final FileHandle selectedFileHandle = this.selectedFileToValidatedFileHandle(selectedFile);
                    final SpritesheetDialog spritesheetDialog = new SpritesheetDialog(selectedFileHandle);
                    spritesheetDialog.initOwner(owner);
                    final Optional<Pair<Integer, Integer>> optionalRowColPair = spritesheetDialog.showAndWait();
                    if (optionalRowColPair.isPresent()) {
                        final Pair<Integer, Integer> rowColPair = optionalRowColPair.get();
                        final SpritesheetPageAsset spritesheetPageAsset = this.createSpritePageAsset(
                                selectedFileHandle, rowColPair.getKey(), rowColPair.getValue()
                        );
                        ApplicationContext.context().currentProject.assetLibrary.addSpritesheetPageAsset(spritesheetPageAsset);
                        ApplicationContext.context().currentProject.saveAssetLibrary();
                        this.pageAssetSimpleAssetListView.getItems().add(spritesheetPageAsset);
                    }

                });
            } catch (Exception e) {
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(owner);
                errorDialog.showAndWait();
            }
        });

        final VBox vBox = new VBox(ApplicationContext.Constants.SPACING);
        vBox.getChildren().addAll(importButton);

        final GridPane layout = new GridPane();
        layout.setHgap(ApplicationContext.Constants.SPACING);
        layout.setVgap(ApplicationContext.Constants.SPACING);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        layout.add(this.pageAssetSimpleAssetListView, 0, 0);
        layout.add(vBox, 1, 0);

        this.setContent(layout);
        this.setClosable(false);

    }

    private SpritesheetPageAsset createSpritePageAsset(FileHandle selectedFileHandle, int rows, int columns) {
        if (rows <= 0) {
            throw new IllegalArgumentException("The number of rows must be at least one.");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("The number of columns must be at least one.");
        }
        final String imagePath = AssetUtils.importExternalFileIntoAssetsFolder(selectedFileHandle);
        return SpritesheetPageAsset.createNewSpritesheetPageAsset(imagePath, rows, columns);
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
