package com.tcg.rpgengine.editor.dialogs;

import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.editor.components.SoundAssetListView;
import com.tcg.rpgengine.editor.components.fontawesome.Icon;
import com.tcg.rpgengine.editor.components.fontawesome.Icons;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SelectSoundDialog extends Dialog<SoundAsset> {

    private BiConsumer<SoundAsset, Float> soundPlayer;

    public SelectSoundDialog(Collection<SoundAsset> soundAssets, SoundAsset initialAsset) {
        super();
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final SoundAssetListView soundAssetListView = new SoundAssetListView();
        soundAssetListView.getItems().setAll(soundAssets);
        soundAssetListView.getSelectionModel().select(initialAsset);

        final Button preview = new Button("Preview");
        preview.setMaxWidth(Double.MAX_VALUE);

        final Slider volumeSlider = new Slider(0.0, 100.0, 90.0);
        final HBox volume = new HBox(ApplicationContext.Constants.SPACING, new Icon(Icons.FA_VOLUME_UP), volumeSlider);

        preview.setOnAction(event -> {
            this.getSelectedSound(soundAssetListView).ifPresent(selectedSoundAsset -> {
                if (this.soundPlayer != null) {
                    this.soundPlayer.accept(selectedSoundAsset, (float) volumeSlider.getValue() / 100.0f);
                }
            });
        });

        final HBox hBox = new HBox(ApplicationContext.Constants.SPACING);
        hBox.getChildren().add(soundAssetListView);
        hBox.getChildren().add(new VBox(ApplicationContext.Constants.SPACING, preview, volume));
        hBox.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.setResultConverter(dialogButton -> {
            final Optional<SoundAsset> selectedSound = this.getSelectedSound(soundAssetListView);
            if (dialogButton == ButtonType.OK && selectedSound.isPresent()) {
                return selectedSound.get();
            }
            return null;
        });

        this.getDialogPane().setContent(hBox);
    }

    private Optional<SoundAsset> getSelectedSound(SoundAssetListView soundAssetListView) {
        return Optional.ofNullable(soundAssetListView.getSelectionModel().getSelectedItem());
    }

    public void setSoundPlayer(BiConsumer<SoundAsset, Float> soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

}
