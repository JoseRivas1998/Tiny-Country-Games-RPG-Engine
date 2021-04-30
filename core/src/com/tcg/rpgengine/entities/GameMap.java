package com.tcg.rpgengine.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.common.data.maps.MapLayer;
import com.tcg.rpgengine.common.data.maps.MapLayerCell;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameMap implements Disposable {

    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Vector2 mapPixelSize;


    public GameMap(TCGRPGGame game, MapEntity mapEntity) {

        final RowColumnPair tileSize = RowColumnPair.of(0, 0);
        final List<UUID> allTilesetIds = mapEntity.getAllTilesetIds();
        final Map<UUID, TextureRegion[][]> tilesetMap = new HashMap<>();
        for (int i = 0; i < allTilesetIds.size(); i++) {
            final TiledImageAsset tilesetAsset = game.assetLibrary.getTilesetAssetById(allTilesetIds.get(i));
            final Texture tilesetTexture = game.localAssetManager.get(tilesetAsset.getPath());
            if (i == 0) {
                tileSize.column = tilesetTexture.getWidth() / tilesetAsset.columns;
                tileSize.row = tilesetTexture.getHeight() / tilesetAsset.rows;
            }

            tilesetMap.put(tilesetAsset.id, TextureRegion.split(tilesetTexture, tileSize.column, tileSize.row));
        }
        final RowColumnPair mapSize = mapEntity.getMapSize();

        this.mapPixelSize = new Vector2(mapSize.column * tileSize.column, mapSize.row * tileSize.row);

        this.tiledMap = new TiledMap();

        final List<MapLayer> mapLayers = mapEntity.getLayers();

        for (MapLayer mapLayer : mapLayers) {
            this.tiledMap.getLayers().add(this.buildTileLayer(tileSize, tilesetMap, mapSize, mapLayer));
        }

        this.mapRenderer = new OrthogonalTiledMapRenderer(this.tiledMap);

    }

    private TiledMapTileLayer buildTileLayer(RowColumnPair tileSize, Map<UUID, TextureRegion[][]> tilesetMap,
                                             RowColumnPair mapSize, MapLayer mapLayer) {
        final TiledMapTileLayer layer = new TiledMapTileLayer(mapSize.column, mapSize.row,
                tileSize.column, tileSize.row);
        final List<MapLayerCell> mapLayerCells = mapLayer.getCells();
        for (MapLayerCell mapLayerCell : mapLayerCells) {
            this.buildLayerCell(tilesetMap, mapSize, layer, mapLayerCell);
        }
        return layer;
    }

    private void buildLayerCell(Map<UUID, TextureRegion[][]> tilesetMap, RowColumnPair mapSize,
                                TiledMapTileLayer layer, MapLayerCell mapLayerCell) {
        final TextureRegion[][] tileset = tilesetMap.get(mapLayerCell.getTilesetId());
        final RowColumnPair tilesetCoordinate = mapLayerCell.getTilesetCoordinate();
        final TextureRegion cellRegion = tileset[tilesetCoordinate.row][tilesetCoordinate.column];
        final RowColumnPair mapCoordinate = mapLayerCell.getMapCoordinate();
        final int x = mapCoordinate.column;
        final int y = mapSize.row - 1 - mapCoordinate.row;
        final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(cellRegion));
        layer.setCell(x, y, cell);
    }

    public void draw(Viewport viewport) {
        this.mapRenderer.setView((OrthographicCamera) viewport.getCamera());
        this.mapRenderer.render();
    }

    public Vector2 getMapPixelSize() {
        return this.mapPixelSize.cpy();
    }

    @Override
    public void dispose() {
        this.mapRenderer.dispose();
        this.tiledMap.dispose();
    }
}
