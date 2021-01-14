package com.tcg.rpgengine.common.utils;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataCompressionTest {

    @Test
    public void verifyCompressingAndDecompressionProducesOriginalBytes() {
        final AssetLibrary assetLibrary = AssetLibrary.newAssetLibrary();
        assetLibrary.addImageAsset(ImageAsset.generateNewImageAsset("some_asset"));
        assetLibrary.addImageAsset(ImageAsset.generateNewImageAsset("some_other_asset"));
        assetLibrary.addImageAsset(ImageAsset.generateNewImageAsset("some_third_asset"));
        assetLibrary.addImageAsset(ImageAsset.generateNewImageAsset("some_fourth_asset"));
        final byte[] original = assetLibrary.imageAssetBytes();
        final byte[] compressed = DataCompression.compress(original);
        final byte[] decompressed = DataCompression.decompress(compressed);
        assertArrayEquals(original, decompressed);
    }
}
