package com.tcg.rpgengine.editor.components.databasemanager.systemmanagerpages;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.UISounds;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.SelectSoundDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;


import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SystemSoundEditorPane extends GridPane {

    public SystemSoundEditorPane(Window owner) {
        super();
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.addTitleLabel();

        final ApplicationContext context = ApplicationContext.context();
        final UISounds uiSounds = context.currentProject.systemData.uiSounds;
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;

        int row = 1;
        this.addSoundButtonRow(owner, row++, "Cursor", uiSounds::getCursorId,
                soundAsset -> uiSounds.updateCursor(assetLibrary, soundAsset.id));
        this.addSoundButtonRow(owner, row++, "Cancel", uiSounds::getCancelId,
                soundAsset -> uiSounds.updateCancel(assetLibrary, soundAsset.id));
        this.addSoundButtonRow(owner, row++, "Ok", uiSounds::getOkId,
                soundAsset -> uiSounds.updateOk(assetLibrary, soundAsset.id));
        this.addSoundButtonRow(owner, row, "Buzzer", uiSounds::getBuzzerId,
                soundAsset -> uiSounds.updateBuzzer(assetLibrary, soundAsset.id));

        this.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-background-radius: 5px;");
    }

    private void addSoundButtonRow(Window owner, int row, String label, Supplier<UUID> soundIdSupplier,
                                   Consumer<SoundAsset> onSoundSelected) {
        final ApplicationContext context = ApplicationContext.context();
        final CurrentProject currentProject = context.currentProject;
        final SoundAsset soundAsset = currentProject.assetLibrary.getSoundEffectAssetBytId(soundIdSupplier.get());
        final Button selectButton = new Button(soundAsset.title);
        selectButton.setOnAction(
                event -> this.selectSound(selectButton, owner, soundIdSupplier, label, onSoundSelected));
        this.add(new Label(label + ":"), 0, row);
        this.add(selectButton, 1, row);
    }

    private void selectSound(Button selectButton, Window owner, Supplier<UUID> soundIdSupplier, String label,
                             Consumer<SoundAsset> onSoundSelected) {
        final ApplicationContext context = ApplicationContext.context();
        final CurrentProject currentProject = context.currentProject;
        final AssetLibrary assetLibrary = currentProject.assetLibrary;
        final SelectSoundDialog selectSoundDialog = new SelectSoundDialog(assetLibrary.getAllSoundEffectAssets(),
                assetLibrary.getSoundEffectAssetBytId(soundIdSupplier.get()));

        selectSoundDialog.setTitle("Select " + label);
        selectSoundDialog.setSoundPlayer((selectedSound, volume) -> {
            context.jukebox.stopAll();
            context.jukebox.playSoundEffect(selectedSound, volume);
        });
        selectSoundDialog.initOwner(owner);
        selectSoundDialog.showAndWait()
                .ifPresent(selectedSound -> this.updateSound(selectButton, owner, onSoundSelected, selectedSound));
    }

    private void updateSound(Button selectButton, Window owner, Consumer<SoundAsset> onSoundSelected,
                             SoundAsset selectedSound) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        try {
            onSoundSelected.accept(selectedSound);
            selectButton.setText(selectedSound.title);
            currentProject.saveSystemData();
        } catch (Exception e) {
            e.printStackTrace();
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private SoundAsset getSoundFromAssets(UUID assetId) {
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        return assetLibrary.getSoundEffectAssetBytId(assetId);
    }

    private void addTitleLabel() {
        final Label uiSounds = new Label("System Sounds");
        uiSounds.setStyle("-fx-font-weight: bold;");
        this.add(uiSounds, 0, 0);
    }

}
