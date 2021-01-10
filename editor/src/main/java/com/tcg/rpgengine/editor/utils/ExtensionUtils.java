package com.tcg.rpgengine.editor.utils;

import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExtensionUtils {

    private static final List<Pair<String, String>> supportedSoundFileTypes = Collections.singletonList(
            new Pair<>("MP3 Audio File", "mp3")
    );

    private static final List<Pair<String, String>> supportedImageFileTypes = Arrays.asList(
            new Pair<>("PNG Image File", "png"),
            new Pair<>("JPEG Image File", "jpg"),
            new Pair<>("Bitmap Image File", "bmp")
    );

    public static boolean soundExtensionMatches(String extension) {
        return extensionMatches(extension, supportedSoundFileTypes);
    }

    public static boolean imageExtensionMatches(String extension) {
        return extensionMatches(extension, supportedImageFileTypes);
    }

    private static boolean extensionMatches(String extension, List<Pair<String, String>> supportedFileTypes) {
        return supportedFileTypes.stream()
                .anyMatch(fileType -> fileType.getValue().equalsIgnoreCase(extension));
    }

    public static List<FileChooser.ExtensionFilter> supportedSoundFiles() {
        return getExtensionFilters(supportedSoundFileTypes);
    }

    public static List<FileChooser.ExtensionFilter> supportedImageFiles() {
        return getExtensionFilters(supportedImageFileTypes);
    }

    private static List<FileChooser.ExtensionFilter> getExtensionFilters(
            List<Pair<String, String>> supportedFileTypes) {
        return supportedFileTypes.stream()
                .map(typePair -> new FileChooser.ExtensionFilter(typePair.getKey(),
                        "*." + typePair.getValue()))
                .collect(Collectors.toList());
    }

}
