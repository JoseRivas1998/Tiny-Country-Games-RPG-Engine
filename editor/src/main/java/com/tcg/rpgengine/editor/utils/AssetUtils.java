package com.tcg.rpgengine.editor.utils;

import com.badlogic.gdx.files.FileHandle;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.Float2;
import com.tcg.rpgengine.editor.context.ApplicationContext;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

public interface AssetUtils {

    static float audioFileLength(FileHandle audioFile) {
        try {
            final Mp3File mp3File = new Mp3File(audioFile.file());
            return Math.max(mp3File.getLengthInMilliseconds() / 1000f, 0); // length will be zero if things dont work
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }
    }

    static Dimension imageSize(FileHandle imageFile) {
        final Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(imageFile.extension());
        while (iter.hasNext()) {
            final ImageReader reader = iter.next();
            try (final FileImageInputStream stream = new FileImageInputStream(imageFile.file())) {
                reader.setInput(stream);
                final int width = reader.getWidth(reader.getMinIndex());
                final int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.dispose();
            }
        }
        throw new RuntimeException("Unable to read image size.");
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

    static String importExternalFileIntoAssetsFolder(FileHandle externalFileHandle) {
        final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle assetsFolder = projectFileHandle.sibling(ApplicationContext.Constants.ASSETS_FOLDER_NAME);
        final String assetFileName = externalFileHandle.name()
                .trim().toLowerCase().replaceAll("\\s", "_");
        final FileHandle assetFile = AssetUtils.getFileAsNonExistent(assetsFolder.child(assetFileName));
        externalFileHandle.copyTo(assetFile);
        return AssetUtils.getFilePathRelativeTo(assetFile, projectFileHandle.parent());
    }

    class TiledImageSizeSupplier implements Function<TiledImageAsset, Float2> {

        @Override
        public Float2 apply(TiledImageAsset tiledImageAsset) {
            final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
            final FileHandle imageFile = projectFileHandle.sibling(tiledImageAsset.getPath());
            final Dimension imageSize = AssetUtils.imageSize(imageFile);
            return Float2.of(imageSize.width, imageSize.height);
        }
    }

}
