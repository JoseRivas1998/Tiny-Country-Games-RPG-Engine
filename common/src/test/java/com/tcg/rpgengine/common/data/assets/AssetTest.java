package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class AssetTest {

    @Test
    public void canCreateAsset() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = this.createMockedAssetFromUUID(assetID);
        assertNotNull(asset);
        assertEquals(assetID, asset.id);
    }

    @Test
    public void cannotCreateNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Asset(null) {
                @Override
                protected void addAdditionalJSONData(JSONObject jsonObject) {

                }

                @Override
                protected int contentLength() {
                    return 0;
                }

                @Override
                protected void encodeContent(ByteBuffer byteBuffer) {

                }
            };
        });
    }

    private Asset createMockedAssetFromUUID(UUID assetID) {
        return Mockito.mock(Asset.class, Mockito.withSettings().useConstructor(assetID));
    }

    @Test
    public void toJSONReturnsNonNull() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = this.createMockedAssetFromUUID(assetID);
        Mockito.doCallRealMethod()
                .when(asset)
                .toJSON();
        assertNotNull(asset.toJSON());
    }

    @Test
    public void jsonContainsId() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = this.createMockedAssetFromUUID(assetID);
        Mockito.doCallRealMethod()
                .when(asset)
                .toJSON();
        final JSONObject assetJSON = asset.toJSON();
        assertTrue(assetJSON.has("id"));
    }

    @Test
    public void jsonIdMatchesAsset() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = this.createMockedAssetFromUUID(assetID);
        Mockito.doCallRealMethod()
                .when(asset)
                .toJSON();
        final JSONObject jsonObject = asset.toJSON();
        final String expected = assetID.toString();
        final String actual = jsonObject.getString("id");
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() {
        final UUID assetID = UUID.randomUUID();
        class ConcreteAsset extends Asset {
            public ConcreteAsset(UUID id) {
                super(id);
            }

            @Override
            protected void addAdditionalJSONData(JSONObject jsonObject) {

            }

            @Override
            protected int contentLength() {
                return 0;
            }

            @Override
            protected void encodeContent(ByteBuffer byteBuffer) {

            }
        }
        final Asset asset1 = new ConcreteAsset(assetID);
        final Asset asset2 = new ConcreteAsset(assetID);
        final Asset asset3 = new ConcreteAsset(UUID.randomUUID());

        assertEquals(asset1, asset1);
        assertEquals(asset2, asset2);
        assertEquals(asset3, asset3);

        assertEquals(asset1, asset2);
        assertEquals(asset2, asset1);

        assertNotEquals(asset1, asset3);
        assertNotEquals(asset3, asset1);

        assertNotEquals(asset2, asset3);
        assertNotEquals(asset3, asset2);

        assertFalse(asset1.equals(null));
        assertFalse(asset1.equals("Some String Lol"));
    }

    @Test
    public void testHashCode() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = new Asset(assetID) {
            @Override
            protected void addAdditionalJSONData(JSONObject jsonObject) {
            }

            @Override
            protected int contentLength() {
                return 0;
            }

            @Override
            protected void encodeContent(ByteBuffer byteBuffer) {

            }
        };
        final int expected = Objects.hash(assetID);
        final int actual = asset.hashCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testToString() {
        final UUID assetID = UUID.randomUUID();
        final Asset asset = new Asset(assetID) {
            @Override
            protected void addAdditionalJSONData(JSONObject jsonObject) {
            }

            @Override
            protected int contentLength() {
                return 0;
            }

            @Override
            protected void encodeContent(ByteBuffer byteBuffer) {

            }
        };

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", assetID);
        final String expected = jsonObject.toString();

        final String actual = asset.toString();

        assertEquals(expected, actual);

    }

    @Test
    public void verifyHeaderLength() {
        assertEquals(20, Asset.HEADER_NUMBER_OF_BYTES);
    }

    @Test
    public void verifyEmptyBinaryLength() {
        final Asset noContentAsset = this.createMockedAssetFromUUID(UUID.randomUUID());
        Mockito.when(noContentAsset.contentLength()).thenReturn(0);
        Mockito.when(noContentAsset.toBytes()).thenCallRealMethod();
        final byte[] noContentBytes = noContentAsset.toBytes();
        assertEquals(Asset.HEADER_NUMBER_OF_BYTES, noContentBytes.length);

        final Asset tenBytesAsset = this.createMockedAssetFromUUID(UUID.randomUUID());
        Mockito.when(tenBytesAsset.contentLength()).thenReturn(10);
        Mockito.when(tenBytesAsset.toBytes()).thenCallRealMethod();
        final byte[] tenBytesAssets = tenBytesAsset.toBytes();
        assertEquals(Asset.HEADER_NUMBER_OF_BYTES + 10, tenBytesAssets.length);
    }

    @Test
    public void verifyHeaderBytesWithoutContent() {
        final UUID assetId = UUID.fromString("49a0d09a-7b20-4350-beb0-5a694515d77a");
        final Asset asset = this.createMockedAssetFromUUID(assetId);
        Mockito.when(asset.contentLength()).thenReturn(0);
        Mockito.when(asset.toBytes()).thenCallRealMethod();
        byte[] expected = {
                // UUID
                0x49, (byte) 0xa0, (byte) 0xd0, (byte) 0x9a,
                0x7b, 0x20,
                0x43, 0x50,
                (byte) 0xbe, (byte) 0xb0,
                0x5a, 0x69, 0x45, 0x15, (byte) 0xd7, 0x7a,
                // Content Length
                0x00, 0x00, 0x00, 0x00
        };
        final byte[] actual = asset.toBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void verifyHeaderBytesWith3BytesContent() {
        final UUID assetId = UUID.fromString("49a0d09a-7b20-4350-beb0-5a694515d77a");
        final Asset asset = this.createMockedAssetFromUUID(assetId);
        Mockito.when(asset.contentLength()).thenReturn(3);
        Mockito.when(asset.toBytes()).thenCallRealMethod();
        byte[] expected = {
                // UUID
                0x49, (byte) 0xa0, (byte) 0xd0, (byte) 0x9a,
                0x7b, 0x20,
                0x43, 0x50,
                (byte) 0xbe, (byte) 0xb0,
                0x5a, 0x69, 0x45, 0x15, (byte) 0xd7, 0x7a,
                // Content Length
                0x00, 0x00, 0x00, 0x03,
                // Content bytes
                0x00, 0x00, 0x00
        };
        final byte[] actual = asset.toBytes();
        assertArrayEquals(expected, actual);
    }

}
