package com.tcg.rpgengine.common.data.assets;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        };

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", assetID);
        final String expected = jsonObject.toString();

        final String actual = asset.toString();

        assertEquals(expected, actual);

    }

}
