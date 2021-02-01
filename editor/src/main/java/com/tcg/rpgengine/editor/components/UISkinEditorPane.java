package com.tcg.rpgengine.editor.components;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.system.WindowSkin;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.SelectImageDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.awt.*;

public class UISkinEditorPane extends GridPane {

    public UISkinEditorPane(Window owner) {
        super();
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-background-radius: 5px;");

        this.addTitleLabel();
        this.addSelectWindowSkinButton(owner);

    }

    private void addSelectWindowSkinButton(Window owner) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final WindowSkin windowSkin = currentProject.systemData.windowSkin;
        final AssetLibrary assetLibrary = currentProject.assetLibrary;
        final ImageAsset windowSkinImageAsset = assetLibrary.getImageAssetById(windowSkin.getWindowSkinId());
        final Button button = new Button(windowSkinImageAsset.path);
        button.setOnAction(event -> {
            final ImageAsset currentImageAsset = assetLibrary.getImageAssetById(windowSkin.getWindowSkinId());
            final SelectImageDialog selectImageDialog = new SelectImageDialog(assetLibrary.getAllImageAssets(),
                    currentImageAsset);
            selectImageDialog.initOwner(owner);
            selectImageDialog.setTitle("Select Window Skin");
            selectImageDialog.showAndWait().ifPresent(selectedImageAsset -> {
                try {
                    this.validateSelectedImageAsset(selectedImageAsset);
                    windowSkin.updateImage(assetLibrary, selectedImageAsset.id);
                    currentProject.saveSystemData();
                    button.setText(selectedImageAsset.path);
                } catch (Exception e) {
                    e.printStackTrace();
                    final ErrorDialog errorDialog = new ErrorDialog(e);
                    errorDialog.initOwner(owner);
                    errorDialog.showAndWait();
                }
            });
        });
        this.add(new Label("Skin Image:"), 0, 1);
        this.add(button, 1, 1);
    }

    private void validateSelectedImageAsset(ImageAsset selectedImageAsset) {
        final FileHandle projectFile = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle selectedImageFile = projectFile.sibling(selectedImageAsset.path);
        final Dimension selectedImageSize = AssetUtils.imageSize(selectedImageFile);
        if (!this.isValidWindowSkinSize(selectedImageSize)) {
            throw new IllegalArgumentException("The selected image must be exactly 192x192 pixels.");
        }
    }

    private boolean isValidWindowSkinSize(Dimension selectedImageSize) {
        return selectedImageSize.width == ApplicationContext.Constants.REQUIRED_WINDOW_SKIN_SIZE &&
                selectedImageSize.height == ApplicationContext.Constants.REQUIRED_WINDOW_SKIN_SIZE;
    }

    private void addTitleLabel() {
        final Label title = new Label("UI Skin");
        title.setStyle("-fx-font-weight: bold;");
        this.add(title, 0, 0);
    }

}
