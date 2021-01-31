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
            return Math.max(mp3File.getLengthInMilliseconds() / 1000f, 0); // length will be zero if things dont work
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }
    }

    static FileHandle getFileAsNonExistent(FileHandle fileHandle) {
        if (!fileHandle.exists()) {
            return fileHandle;
        }
        final String baseName = fileHandle.nameWithoutExtension();
        final String extension = fileHandle.extension();
        int fileNameEnumerator = 1;
        FileHandle result = fileHandle;
        while (result.exists()) {
            result = result.sibling(String.format("%s_%d.%s", baseName, fileNameEnumerator, extension));
            fileNameEnumerator++;
        }
        return result;
    }

    static String getFilePathRelativeTo(FileHandle file, FileHandle relativeTo) {
        return relativeTo.file().toPath().relativize(file.file().toPath()).toString().replace("\\", "/");
    }

}
