package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class SelectImageDialog extends Dialog<ImageAsset> {

    public SelectImageDialog(Collection<ImageAsset> assets, ImageAsset initialImage) {
        super();
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final SimpleAssetListView<ImageAsset> imageAssetListView = new SimpleAssetListView<>(image -> image.path);
        imageAssetListView.getItems().setAll(assets);
        imageAssetListView.getSelectionModel().select(initialImage);
        imageAssetListView.setMinWidth(Region.USE_PREF_SIZE);
        imageAssetListView.setMaxHeight(720.0);

        final ImageView imageView = new ImageView(this.imageFromImageAsset(initialImage));
        imageView.fitHeightProperty().bind(imageAssetListView.heightProperty());
        imageView.setFitWidth(ApplicationContext.Constants.EDITOR_WIDTH);
        imageView.setPreserveRatio(true);

        imageAssetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Optional.ofNullable(newValue).ifPresent(selectedAsset -> {
                imageView.setImage(this.imageFromImageAsset(selectedAsset));
                this.getDialogPane().getScene().getWindow().sizeToScene();
            });
        });

        final HBox hBox = new HBox(ApplicationContext.Constants.SPACING);
        hBox.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        hBox.getChildren().addAll(imageAssetListView, imageView);

        this.setResultConverter(dialogButton -> {
            final Optional<ImageAsset> selectedItem = Optional.ofNullable(
                    imageAssetListView.getSelectionModel().getSelectedItem());
            if (dialogButton == ButtonType.OK && selectedItem.isPresent()) {
                return selectedItem.get();
            }
            return null;
        });

        this.getDialogPane().setContent(hBox);
    }

    private Image imageFromImageAsset(ImageAsset selectedAsset) {
        final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle imageFile = projectFileHandle.sibling(selectedAsset.path);
        return new Image(imageFile.read());
    }

}
