package com.tcg.rpgengine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.common.data.assets.SoundAsset;
import com.tcg.rpgengine.common.data.system.SystemData;
import com.tcg.rpgengine.common.utils.DataCompression;

import java.nio.ByteBuffer;

public class GameDataLoader extends Thread{

    private final TCGRPGGame game;
    private final Runnable onCompletion;

    public GameDataLoader(TCGRPGGame game, Runnable onCompletion) {
        super();
        this.game = game;
        this.onCompletion = onCompletion;
    }

    @Override
    public void run() {
        final FileHandle musicDataFile = Gdx.files.local("data/music.tcgdat");
        final ByteBuffer musicDataBytes = ByteBuffer.wrap(DataCompression.decompress(musicDataFile.readBytes()));
        while (musicDataBytes.hasRemaining()) {
            final SoundAsset soundAsset = SoundAsset.createFromBytes(musicDataBytes);
            this.game.assetLibrary.addMusicAsset(soundAsset);
        }

        final FileHandle imagesDataFile = Gdx.files.local("data/images.tcgdat");
        final ByteBuffer imageDataBytes = ByteBuffer.wrap(DataCompression.decompress(imagesDataFile.readBytes()));
        while(imageDataBytes.hasRemaining()) {
            final ImageAsset imageAsset = ImageAsset.createFromBytes(imageDataBytes);
            this.game.assetLibrary.addImageAsset(imageAsset);
        }

        final FileHandle systemDataFile = Gdx.files.local("data/system.tcgdat");
        final ByteBuffer systemDataBytes = ByteBuffer.wrap(DataCompression.decompress(systemDataFile.readBytes()));
        this.game.systemData = SystemData.createFromBytes(this.game.assetLibrary, systemDataBytes);

        Gdx.app.postRunnable(this.onCompletion);
    }
}
