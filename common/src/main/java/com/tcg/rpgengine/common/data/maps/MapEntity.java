package com.tcg.rpgengine.common.data.maps;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.BinaryDocument;
import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.misc.AssetTable;
import com.tcg.rpgengine.common.data.misc.Float2;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.common.utils.UuidUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapEntity extends Entity implements BinaryDocument {

    private static final String JSON_NAME_FIELD = "name";
    private static final String JSON_SIZE_FIELD = "size";
    private static final String JSON_TILESETS_FIELD = "tilesets";
    private static final String JSON_LAYERS_FIELD = "layers";
    private String name;
    private final RowColumnPair mapSize;
    private final Map<UUID, Character> tilesets;
    private final List<MapLayer> layers;

    private MapEntity(UUID id, AssetLibrary assetLibrary, String name, RowColumnPair mapSize, List<UUID> tilesetIds,
                      Function<TiledImageAsset, Float2> imageSizeSupplier) {
        super(id);
        this.setName(name);
        if (mapSize.row <= 0 || mapSize.column <= 0) {
            throw new IllegalArgumentException("Map size must be greater than zero.");
        }
        this.mapSize = mapSize.copy();
        this.tilesets = this.buildTilesets(assetLibrary, tilesetIds, imageSizeSupplier);
        this.layers = new ArrayList<>();
    }

    public static MapEntity createNewMap(AssetLibrary assetLibrary, String name, RowColumnPair mapSize,
                                         List<UUID> tilesetIds, Function<TiledImageAsset, Float2> imageSizeSupplier) {
        final UUID id = UuidUtils.generateUuid();
        final MapEntity map = new MapEntity(id, assetLibrary, name, mapSize, tilesetIds, imageSizeSupplier);
        final UUID groundTilesetId = map.getAllTilesetIds().get(0);
        final MapLayer ground = MapLayer.createNewLayer("ground", map.mapSize);
        ground.fillAllBlanks(assetLibrary, groundTilesetId, RowColumnPair.of(0, 0));
        map.layers.add(ground);
        return map;
    }

    public static MapEntity ofJSON(AssetLibrary assetLibrary, Function<TiledImageAsset, Float2> imageSizeSupplier,
                                   String json) {
        final JSONObject jsonObject = new JSONObject(json);
        final UUID id = UuidUtils.fromString(jsonObject.getString(JSON_ID_FIELD));
        final String name = jsonObject.getString(JSON_NAME_FIELD);
        final RowColumnPair size = RowColumnPair.ofJSON(jsonObject.getJSONObject(JSON_SIZE_FIELD).toString());
        final JSONArray tilesetsArray = jsonObject.getJSONArray(JSON_TILESETS_FIELD);
        final List<UUID> tilesetIds = new ArrayList<>();
        for (int i = 0; i < tilesetsArray.length(); i++) {
            tilesetIds.add(UUID.fromString(tilesetsArray.getString(i)));
        }
        final MapEntity map = new MapEntity(id, assetLibrary, name, size, tilesetIds, imageSizeSupplier);
        final JSONArray layersArray = jsonObject.getJSONArray(JSON_LAYERS_FIELD);
        for (int i = 0; i < layersArray.length(); i++) {
            map.layers.add(MapLayer.fromJSON(assetLibrary, size, layersArray.getJSONObject(i).toString()));
        }
        return map;
    }

    public static MapEntity ofBytes(AssetLibrary assetLibrary, Function<TiledImageAsset, Float2> imageSizeSupplier,
                            ByteBuffer bytes) {
        final UUID id = BinaryDocument.getUuid(bytes);
        final String name = BinaryDocument.getUTF8String(bytes);
        final RowColumnPair size = RowColumnPair.ofBytes(bytes);
        final AssetTable<TiledImageAsset> assetTable = AssetTable.fromBytes(AssetLibrary::getTilesetAssetById, bytes);
        final List<UUID> tilesetIds = new ArrayList<>();
        for (int i = 0; i < assetTable.size(); i++) {
            tilesetIds.add(assetTable.get(assetLibrary, i).id);
        }
        final MapEntity map = new MapEntity(id, assetLibrary, name, size, tilesetIds, imageSizeSupplier);
        final int numLayers = bytes.getInt();
        for (int i = 0; i < numLayers; i++) {
            map.layers.add(MapLayer.fromBytes(assetLibrary, assetTable, map.mapSize, bytes));
        }
        return map;
    }

    private Map<UUID, Character> buildTilesets(AssetLibrary assetLibrary, List<UUID> tilesetIds,
                                               Function<TiledImageAsset, Float2> imageSizeSupplier) {
        if (tilesetIds.isEmpty()) throw new IllegalArgumentException("There must be at least one tileset.");
        final Map<UUID, Character> tilesets = new HashMap<>();
        char currentChar = 'A';
        Float2 mapSize = null;
        for (UUID tilesetId : tilesetIds) {
            if (!tilesets.containsKey(tilesetId)) {
                final TiledImageAsset tilesetAsset = assetLibrary.getTilesetAssetById(tilesetId);
                final Float2 tiledImageTileSize = this.getTiledImageTileSize(tilesetAsset, imageSizeSupplier);
                if (mapSize == null) {
                    mapSize = tiledImageTileSize;
                } else if (this.tileSizesDoNotMatch(mapSize, tiledImageTileSize)) {
                    throw new IllegalArgumentException("Tilesets must all have the same tile size.");
                }
                assetLibrary.incrementReferenceCount(tilesetAsset);
                tilesets.put(tilesetAsset.id, currentChar);
                currentChar++;
            }
        }
        if (tilesets.isEmpty()) throw new IllegalArgumentException("There must be at least one tileset.");
        if (tilesets.size() > 26) throw new IllegalArgumentException("There can only be at most 26 tilesets per map.");
        return tilesets;
    }

    private boolean tileSizesDoNotMatch(Float2 tilesetTileSize, Float2 mapTileSize) {
        return Float.compare(tilesetTileSize.x, mapTileSize.x) != 0
                || Float.compare(tilesetTileSize.y, mapTileSize.y) != 0;
    }

    private Character getMaxLetter() {
        return this.tilesets.values()
                .stream()
                .max(Character::compareTo)
                .orElseThrow(() -> new IllegalStateException("No tileset in map."));
    }

    public List<UUID> getAllTilesetIds() {
        return this.tilesets.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void addTileset(AssetLibrary assetLibrary, UUID tilesetId,
                           Function<TiledImageAsset, Float2> imageSizeSupplier) {
        if (!this.tilesets.containsKey(tilesetId)) {
            char maxLetter = this.getMaxLetter();
            if (maxLetter == 'Z') {
                throw new IllegalStateException("There can only be at most 26 tilesets per map.");
            }
            maxLetter++;
            final TiledImageAsset tilesetAsset = assetLibrary.getTilesetAssetById(tilesetId);
            this.validateTileSize(assetLibrary, imageSizeSupplier, tilesetAsset);
            this.tilesets.put(tilesetAsset.id, maxLetter);
        }
    }

    public Float2 getTileSize(AssetLibrary assetLibrary, Function<TiledImageAsset, Float2> imageSizeSupplier) {
        final UUID tilesetId = this.tilesets.keySet()
                .stream().findFirst().orElseThrow(() -> new IllegalStateException(""));
        final TiledImageAsset tilesetAsset = assetLibrary.getTilesetAssetById(tilesetId);
        return this.getTiledImageTileSize(tilesetAsset, imageSizeSupplier);
    }

    private void validateTileSize(AssetLibrary assetLibrary, Function<TiledImageAsset, Float2> imageSizeSupplier,
                                  TiledImageAsset tilesetAsset) {
        final Float2 tilesetTileSize = this.getTiledImageTileSize(tilesetAsset, imageSizeSupplier);
        final Float2 mapTileSize = this.getTileSize(assetLibrary, imageSizeSupplier);
        if (this.tileSizesDoNotMatch(tilesetTileSize, mapTileSize)) {
            throw new IllegalArgumentException("Tilesets must all have the same tile size.");
        }
    }

    private Float2 getTiledImageTileSize(TiledImageAsset tilesetAsset,
                                         Function<TiledImageAsset, Float2> imageSizeSupplier) {
        Float2 imageSize = imageSizeSupplier.apply(tilesetAsset);
        return Float2.of(imageSize.x / tilesetAsset.columns, imageSize.y / tilesetAsset.rows);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public byte[] toBytes() {
        final AssetTable<TiledImageAsset> assetTable = AssetTable.emptyAssetTable(AssetLibrary::getTilesetAssetById);
        int totalLayerBytes = 0;
        final byte[][] layerBytes = new byte[this.layers.size()][];
        for (int i = 0; i < layerBytes.length; i++) {
            layerBytes[i] = this.layers.get(i).toBytes(assetTable);
            totalLayerBytes += layerBytes[i].length;
        }
        final int constantSize = UuidUtils.UUID_NUMBER_OF_BYTES
                + Integer.BYTES + this.name.length()
                + this.mapSize.numberOfBytes()
                + Integer.BYTES;
        final ByteBuffer bytes = ByteBuffer.wrap(new byte[constantSize + assetTable.numberOfBytes() + totalLayerBytes]);
        bytes.put(UuidUtils.toBytes(this.id));
        BinaryDocument.putUTF8String(bytes, this.name);
        bytes.put(this.mapSize.toBytes());
        bytes.put(assetTable.toBytes());
        bytes.putInt(this.layers.size());
        Stream.of(layerBytes).forEach(bytes::put);
        return bytes.array();
    }

    @Override
    public int numberOfBytes() {
        // Initialize to the amount of bytes to store id and name, the map size, and the number of layers
        int totalBytes = UuidUtils.UUID_NUMBER_OF_BYTES
                + Integer.BYTES + this.name.length()
                + this.mapSize.numberOfBytes()
                + Integer.BYTES;
        // Used to count how many tilesets are actually used, since that is all we will store
        final Set<UUID> uniqueTilesetsUsed = new HashSet<>();
        for (MapLayer layer : this.layers) {
            totalBytes += layer.numberOfBytes();
            layer.getCells().stream().map(MapLayerCell::getTilesetId).forEach(uniqueTilesetsUsed::add);
        }
        // Add on the amount of bytes the asset table would take
        totalBytes += Integer.BYTES + (uniqueTilesetsUsed.size() * UuidUtils.UUID_NUMBER_OF_BYTES);
        return totalBytes;
    }

    @Override
    protected void addAdditionalJSONData(JSONObject jsonObject) {
        jsonObject.put(JSON_NAME_FIELD, this.name);
        jsonObject.put(JSON_SIZE_FIELD, this.mapSize.toJSON());
        final JSONArray tilesetIdsArray = new JSONArray();
        this.getAllTilesetIds()
                .stream()
                .map(UUID::toString)
                .forEach(tilesetIdsArray::put);
        jsonObject.put(JSON_TILESETS_FIELD, tilesetIdsArray);
        final JSONArray layersArray = new JSONArray();
        this.layers
                .stream()
                .map(MapLayer::toJSON)
                .forEach(layersArray::put);
        jsonObject.put(JSON_LAYERS_FIELD, layersArray);
    }
}
