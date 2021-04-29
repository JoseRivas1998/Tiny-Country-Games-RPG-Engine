package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.components.SimpleEntityListView;
import com.tcg.rpgengine.editor.components.canvasses.TiledImageSelectTileCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TileSelectDialog extends Dialog<Pair<UUID, RowColumnPair>> {

    private final SimpleEntityListView<TiledImageAsset> listView;
    private final TiledImageSelectTileCanvas imagePreviewCanvas;

    public TileSelectDialog(String title, Collection<TiledImageAsset> assets) {
        super();
        this.setTitle(title);
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        this.listView = this.buildImageList(assets);

        final TiledImageAsset selectedItem = this.listView.getSelectionModel().getSelectedItem();
        final FileHandle projectFile = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle imageFile = projectFile.sibling(selectedItem.getPath());
        this.imagePreviewCanvas = new TiledImageSelectTileCanvas(imageFile, selectedItem.rows, selectedItem.columns);

        final HBox layout = new HBox(ApplicationContext.Constants.SPACING,
                this.listView,
                this.buildCanvasScrollPane(imageFile)
        );
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.getDialogPane().setMinHeight(ApplicationContext.Constants.EDITOR_HEIGHT);
        this.getDialogPane().setMaxHeight(ApplicationContext.Constants.EDITOR_HEIGHT);
        this.getDialogPane().setMinWidth(ApplicationContext.Constants.EDITOR_WIDTH);
        this.getDialogPane().setMaxWidth(ApplicationContext.Constants.EDITOR_WIDTH);

        this.getDialogPane().setContent(layout);

        this.setResultConverter(param -> {
            if (param != ButtonType.OK) return null;
            final Optional<TiledImageAsset> optionalSelectedItem = Optional.ofNullable(
                    this.listView.getSelectionModel().getSelectedItem());
            if (optionalSelectedItem.isEmpty()) return null;
            final TiledImageAsset tiledImageAsset = optionalSelectedItem.get();
            final RowColumnPair tileIndex = RowColumnPair.of(
                    Math.max(Math.min(tiledImageAsset.rows - 1, this.imagePreviewCanvas.getSelectedRow()), 0),
                    Math.max(Math.min(tiledImageAsset.columns - 1, this.imagePreviewCanvas.getSelectedColumn()), 0)
            );
            return new Pair<>(tiledImageAsset.id, tileIndex);
        });

    }

    private ScrollPane buildCanvasScrollPane(FileHandle imageFile) {
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setContent(this.buildCanvasHolder(imageFile));
        return scrollPane;
    }

    private StackPane buildCanvasHolder(FileHandle imageFile) {
        final StackPane canvasHolder = new StackPane();
        this.imagePreviewCanvas.setHeight(AssetUtils.imageSize(imageFile).height);
        this.imagePreviewCanvas.widthProperty().bind(canvasHolder.widthProperty());
        canvasHolder.getChildren().setAll(this.imagePreviewCanvas);
        return canvasHolder;
    }

    private SimpleEntityListView<TiledImageAsset> buildImageList(Collection<TiledImageAsset> assets) {
        final SimpleEntityListView<TiledImageAsset> listView = new SimpleEntityListView<>(TiledImageAsset::getPath);
        listView.getItems().setAll(assets);
        listView.getSelectionModel().select(0);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedAsset) -> {
            try {
                this.imagePreviewCanvas.setSelectedRow(0);
                this.imagePreviewCanvas.setSelectedColumn(0);
                final FileHandle projectFile = ApplicationContext.context().currentProject.getProjectFileHandle();
                final FileHandle imageFile = projectFile.sibling(selectedAsset.getPath());
                this.imagePreviewCanvas.setImage(imageFile, selectedAsset.rows, selectedAsset.columns);
                this.imagePreviewCanvas.setHeight(AssetUtils.imageSize(imageFile).getHeight());
            } catch (Exception e) {
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(this.getDialogPane().getScene().getWindow());
                errorDialog.showAndWait();
            }
        });
        return listView;
    }

    public TileSelectDialog(String title, Collection<TiledImageAsset> assets, UUID selectedAsset, RowColumnPair iconIndex) {
        this(title, assets);
        int selectedAssetIndex = -1;
        for (int i = 0; i < this.listView.getItems().size() && selectedAssetIndex == -1; i++) {
            if (this.listView.getItems().get(i).id.equals(selectedAsset)) {
                selectedAssetIndex = i;
            }
        }
        if (selectedAssetIndex != -1) {
            this.listView.getSelectionModel().select(selectedAssetIndex);
        }
        this.imagePreviewCanvas.setSelectedRow(iconIndex.row);
        this.imagePreviewCanvas.setSelectedColumn(iconIndex.column);
    }

}
