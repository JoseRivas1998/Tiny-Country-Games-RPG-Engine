package com.tcg.rpgengine.editor.containers;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.editor.components.MapEditorMenuBar;
import com.tcg.rpgengine.editor.components.MapLayerListView;
import com.tcg.rpgengine.editor.components.MapNameListView;
import com.tcg.rpgengine.editor.components.TilesetTabPane;
import com.tcg.rpgengine.editor.components.canvasses.MapRenderCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.util.Optional;
import java.util.UUID;

public class EditorPane extends BorderPane {

    private final MapNameListView mapListView;
    private final ScrollPane mapRenderScrollPane;
    private final TilesetTabPane tilesetTabPane;
    private final MapLayerListView mapLayerListView;
    private final MapEditor mapEditor;


    public EditorPane() {
        super();
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.mapEditor = new MapEditor();

        final MapNameListView mapListView = new MapNameListView(this::getMapName);
        mapListView.getItems().setAll(ApplicationContext.context().currentProject.systemData.maps.getAllMaps());
        mapListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> this.getSelectedItem().ifPresent(this::onMapSelected));
        BorderPane.setMargin(mapListView, new Insets(ApplicationContext.Constants.SPACING));

        this.mapListView = mapListView;

        this.setLeft(this.mapListView);

        this.mapRenderScrollPane = new ScrollPane();
        BorderPane.setMargin(this.mapRenderScrollPane, new Insets(ApplicationContext.Constants.SPACING));
        this.setCenter(this.mapRenderScrollPane);

        this.tilesetTabPane = new TilesetTabPane(this.mapEditor);
        VBox.setVgrow(this.tilesetTabPane, Priority.ALWAYS);

        this.mapLayerListView = new MapLayerListView();
        this.mapLayerListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> Optional.ofNullable(newValue)
                        .ifPresent(this.mapEditor::selectLayer));
        VBox.setVgrow(this.mapLayerListView, Priority.ALWAYS);

        final Button addLayer = new Button("Add Layer");
        addLayer.setOnAction(event -> {
            if(this.mapEditor.addLayer()) {
                final int newLayerIndex = this.mapLayerListView.getItems().size();
                this.mapLayerListView.getItems().add(0, newLayerIndex);
                this.mapLayerListView.getSelectionModel().select(0);
            }
        });

        final HBox layerButtons = new HBox(ApplicationContext.Constants.SPACING, addLayer);

        final VBox layerBox = new VBox(ApplicationContext.Constants.SPACING, this.mapLayerListView, layerButtons);
        VBox.setVgrow(layerBox, Priority.ALWAYS);

        this.setRight(new VBox(ApplicationContext.Constants.SPACING, this.tilesetTabPane, layerBox));

        this.mapListView.getSelectionModel().select(0);

        this.setTop(new MapEditorMenuBar(this.mapEditor));

    }

    private void onMapSelected(UUID mapId) {
        try {
            final CurrentProject currentProject = ApplicationContext.context().currentProject;
            final AssetLibrary assetLibrary = currentProject.assetLibrary;
            final FileHandle projectFile = currentProject.getProjectFileHandle();
            final FileHandle mapFile = projectFile.sibling(String.format("maps/%s.json", mapId));
            final MapEntity map = MapEntity.ofJSON(assetLibrary, new AssetUtils.TiledImageSizeSupplier(),
                    mapFile.readString());
            this.mapEditor.setMap(map);
            MapRenderCanvas mapRenderCanvas = new MapRenderCanvas(assetLibrary, map, this.mapEditor);
            this.mapRenderScrollPane.setContent(mapRenderCanvas);
            this.tilesetTabPane.setMap(map);
            this.mapLayerListView.setMap(map);
        } catch (Exception exception) {

            ErrorDialog.showErrorDialog(exception, ApplicationContext.context().primaryStage);
        }
    }

    private Optional<UUID> getSelectedItem() {
        return Optional.ofNullable(this.mapListView.getSelectionModel().getSelectedItem());
    }

    private String getMapName(UUID mapId) {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle projectFileHandle = context.currentProject.getProjectFileHandle();
        final FileHandle mapFile = projectFileHandle.sibling(String.format("maps/%s.json", mapId));
        if (!mapFile.exists()) {
            throw new IllegalStateException("Map file not found for map: " + mapId + ".");
        }
        final JSONObject json = new JSONObject(mapFile.readString());
        return json.getString("name");
    }



}
