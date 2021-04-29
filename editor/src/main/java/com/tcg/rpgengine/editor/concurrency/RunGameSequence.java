package com.tcg.rpgengine.editor.concurrency;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.Asset;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.utils.DataCompression;
import com.tcg.rpgengine.editor.TestGameRunner;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.JavaProcess;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.function.Function;

public class RunGameSequence extends TaskSequence{

    public RunGameSequence(Stage progressStage) {
        this.addTask("Compiling Music", this::compileMusic);
        this.addTask("Compiling Images", this::compileImages);
        this.addTask("Compiling Sound Effects", this::compileSoundEffects);
        this.addTask("Compiling Spritesheet Pages", this::compileSpritesheetPages);
        this.addTask("Compiling Tilesets", this::compileTilesets);
        this.addTask("Compiling Icon Pages", this::compileIconPages);
        this.addTask("Compiling System", this::compileSystem);
        this.addTask("Compiling Elements", this::compileElements);
        this.addTask("Compiling Actors", this::compileActors);

        this.addTask("Running Game", () -> this.runGame(progressStage));

        this.addTask("Cleaning Up", this::cleanUp);
    }

    private void cleanUp() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle assetsFolder = context.files.local("assets/");
        final FileHandle dataFolder = context.files.local("data/");
        assetsFolder.deleteDirectory();
        dataFolder.deleteDirectory();
    }

    private void runGame(Stage progressStage) {
        try {
            JavaProcess.exec(TestGameRunner.class, null);
        } catch (Exception e) {
            Platform.runLater(() -> {
                final ErrorDialog errorDialog = new ErrorDialog(e);
                errorDialog.initOwner(progressStage);
                errorDialog.showAndWait();
            });
        }
    }

    private void compileActors() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle actorsBytes = context.files.local("data/actors.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(context.currentProject.database.actors.toBytes());
        actorsBytes.writeBytes(compressedBytes, false);
    }

    protected void compileElements() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle elementsBytes = context.files.local("data/elements.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(context.currentProject.database.elements.toBytes());
        elementsBytes.writeBytes(compressedBytes, false);
    }

    protected void compileSystem() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle systemDataFile = context.files.local("data/system.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(context.currentProject.systemData.toBytes());
        systemDataFile.writeBytes(compressedBytes, false);
    }

    protected void compileIconPages() {
        final ApplicationContext context = ApplicationContext.context();
        final CurrentProject currentProject = context.currentProject;
        this.copyAssetCollectionToLocal(currentProject.assetLibrary.getAllIconPages(), TiledImageAsset::getPath);
        final FileHandle iconsDataFile = context.files.local("data/icons.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(currentProject.assetLibrary.iconPagesBytes());
        iconsDataFile.writeBytes(compressedBytes, false);
    }

    protected void compileTilesets() {
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.copyAssetCollectionToLocal(assetLibrary.getAllTilesets(), TiledImageAsset::getPath);
        final FileHandle tilesetsDataFile = context.files.local("data/tilesets.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(assetLibrary.tilesetsBytes());
        tilesetsDataFile.writeBytes(compressedBytes, false);
    }

    private void compileSpritesheetPages() {
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.copyAssetCollectionToLocal(assetLibrary.getAllSpritesheetPages(), TiledImageAsset::getPath);
        final FileHandle spritesheetsDataFile = context.files.local("data/spritesheets.tcgdat");
        final byte[] decompressedBytes = assetLibrary.spritesheetPagesBytes();
        final byte[] compressedBytes = DataCompression.compress(decompressedBytes);
        spritesheetsDataFile.writeBytes(compressedBytes, false);
    }

    protected void compileSoundEffects() {
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.copyAssetCollectionToLocal(assetLibrary.getAllSoundEffectAssets(), soundAsset -> soundAsset.path);
        final FileHandle soundDataFile = context.files.local("data/sound.tcgdat");
        byte[] compressedBytes = DataCompression.compress(assetLibrary.soundAssetBytes());
        soundDataFile.writeBytes(compressedBytes, false);
    }

    private void compileImages() {
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.copyAssetCollectionToLocal(assetLibrary.getAllImageAssets(), imageAsset -> imageAsset.path);
        final FileHandle imageDataFile = context.files.local("data/images.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(assetLibrary.imageAssetBytes());
        imageDataFile.writeBytes(compressedBytes, false);
    }

    protected void compileMusic() {
        final ApplicationContext context = ApplicationContext.context();
        final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
        this.copyAssetCollectionToLocal(assetLibrary.getAllMusicAssets(), soundAsset -> soundAsset.path);
        final FileHandle musicDataFile = context.files.local("data/music.tcgdat");
        final byte[] compressedBytes = DataCompression.compress(assetLibrary.musicAssetBytes());
        musicDataFile.writeBytes(compressedBytes, false);
    }

    private <T extends Asset> void copyAssetCollectionToLocal(Collection<T> assets, Function<T, String> pathCreator) {
        final ApplicationContext context = ApplicationContext.context();
        assets.forEach(asset -> {
            final String path = pathCreator.apply(asset);
            final FileHandle sourceFile = context.currentProject.getProjectFileHandle().sibling(path);
            final FileHandle targetFile = context.files.local(path);
            sourceFile.copyTo(targetFile);
        });
    }
    
}
