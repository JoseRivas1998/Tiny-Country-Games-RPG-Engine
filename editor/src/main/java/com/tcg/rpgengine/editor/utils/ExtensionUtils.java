package com.tcg.rpgengine.editor.utils;

import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExtensionUtils {

    private static final List<Pair<String, String>> supportedMusicFileTypes = Collections.singletonList(
            new Pair<>("MP3 Audio File", "mp3")
    );

    public static boolean extensionMatches(String extension) {
        return supportedMusicFileTypes.stream()
                .anyMatch(fileType -> fileType.getValue().equalsIgnoreCase(extension));
    }

    public static List<FileChooser.ExtensionFilter> supportedMusicFiles() {
        return supportedMusicFileTypes.stream()
                .map(typePair -> new FileChooser.ExtensionFilter(typePair.getKey(),
                        "*." + typePair.getValue()))
                .collect(Collectors.toList());
    }

}
