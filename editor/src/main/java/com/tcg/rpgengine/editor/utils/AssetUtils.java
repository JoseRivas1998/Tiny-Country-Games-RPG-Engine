package com.tcg.rpgengine.editor.utils;

import com.badlogic.gdx.files.FileHandle;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface AssetUtils {

    static float audioFileLength(FileHandle audioFile) {
        try {
            final Mp3File mp3File = new Mp3File(audioFile.file());
            return mp3File.getLengthInMilliseconds() / 1000f;
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }
    }

}
