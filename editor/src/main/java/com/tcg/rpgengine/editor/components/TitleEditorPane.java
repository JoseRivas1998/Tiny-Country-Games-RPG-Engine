package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.common.data.system.Title;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.context.Jukebox;
import com.tcg.rpgengine.editor.dialogs.SelectImageDialog;
import com.tcg.rpgengine.editor.dialogs.SelectSoundDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TitleEditorPane extends GridPane {

    public TitleEditorPane(Window owner) {
        super();
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.addTitleLabel();

        final Title title = ApplicationContext.context().currentProject.systemData.title;

        this.addGameTitleButton(owner, title);

        this.add(new Label("Title Image:"), 0, 2);
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final Button titleImageButton = new Button(assetLibrary.getImageAssetById(title.getImageId()).path);
        titleImageButton.setMaxWidth(Double.MAX_VALUE);
        titleImageButton.setOnAction(event -> this.updateTitleImage(owner, titleImageButton));
        this.add(titleImageButton, 1, 2);

        this.add(new Label("Title Music:"), 0, 3);
        final Button titleMusicButton = new Button(assetLibrary.getMusicAssetById(title.getMusicId()).title);
        titleMusicButton.setMaxWidth(Double.MAX_VALUE);
        titleMusicButton.setOnAction(event -> this.updateTitleMusic(owner, titleMusicButton));
        this.add(titleMusicButton, 1, 3);

        this.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-background-radius: 5px;");

    }

    private void updateTitleMusic(Window owner, Button titleMusicButton) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final AssetLibrary assetLibrary = currentProject.assetLibrary;
        final SystemData systemData = currentProject.systemData;
        final Jukebox jukebox = ApplicationContext.context().jukebox;
        final SoundAsset initialAsset = assetLibrary.getMusicAssetById(systemData.title.getMusicId());
        final List<SoundAsset> soundAssets = assetLibrary.getAllMusicAssetsSorted(Comparator.comparing(
                soundAsset -> soundAsset.title));

        final SelectSoundDialog selectSoundDialog = new SelectSoundDialog(soundAssets, initialAsset);
        selectSoundDialog.setTitle("Select Title Music");
        selectSoundDialog.initOwner(owner);
        selectSoundDialog.setSoundPlayer((soundAsset, volume) -> {
            jukebox.stopAll();
            jukebox.playMusic(soundAsset, volume);
        });
        selectSoundDialog.showAndWait().ifPresent(selectedSound -> {
            titleMusicButton.setText(selectedSound.title);
            systemData.title.updateMusic(assetLibrary, selectedSound.id);
            currentProject.saveSystemData();
        });
        jukebox.stopAll();
    }

    private void updateTitleImage(Window owner, Button titleImageButton) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final AssetLibrary assetLibrary = currentProject.assetLibrary;
        final SystemData systemData = currentProject.systemData;
        final ImageAsset initialAsset = assetLibrary.getImageAssetById(systemData.title.getImageId());
        final List<ImageAsset> imageAssets = assetLibrary.getAllImageAssets();

        final SelectImageDialog selectImageDialog = new SelectImageDialog(imageAssets, initialAsset);
        selectImageDialog.setTitle("Select Title Image");
        selectImageDialog.initOwner(owner);
        final Optional<ImageAsset> imageAssetOptional = selectImageDialog.showAndWait();
        imageAssetOptional.ifPresent(selectedAsset -> {
            titleImageButton.setText(selectedAsset.path);
            systemData.title.updateImage(assetLibrary, selectedAsset.id);
            currentProject.saveSystemData();
        });
    }

    private void addGameTitleButton(Window owner, Title title) {
        this.add(new Label("Game Title:"), 0, 1);
        final Button gameTitleButton = new Button(title.title);
        gameTitleButton.setMaxWidth(Double.MAX_VALUE);
        gameTitleButton.setOnAction(event -> this.updateGameTitle(owner, gameTitleButton));
        this.add(gameTitleButton, 1, 1);
    }

    private void updateGameTitle(Window owner, Button gameTitleButton) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final TextInputDialog textInputDialog = new TextInputDialog(currentProject.systemData.title.title);
        textInputDialog.setTitle("Update Game Title");
        textInputDialog.setHeaderText(null);
        textInputDialog.setContentText("New Game Title:");
        textInputDialog.initOwner(owner);
        final Optional<String> newTitle = textInputDialog.showAndWait();
        if (newTitle.isPresent() && !newTitle.get().isEmpty()) {
            final String gameTitle = newTitle.get();
            currentProject.systemData.title.title = gameTitle;
            currentProject.saveSystemData();
            gameTitleButton.setText(gameTitle);
        }
    }

    private void addTitleLabel() {
        final Label title = new Label("Title");
        title.setStyle("-fx-font-weight: bold;");
        this.add(title, 0, 0);
    }

}
