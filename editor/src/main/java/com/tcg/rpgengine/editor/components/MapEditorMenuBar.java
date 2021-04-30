package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.editor.components.fontawesome.Icon;
import com.tcg.rpgengine.editor.components.fontawesome.Icons;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tooltip;

public class MapEditorMenuBar extends MenuBar {

    private final MapEditor editor;

    public MapEditorMenuBar(MapEditor editor) {
        super();
        this.editor = editor;

        this.getMenus().addAll(this.buildSaveMenu());
    }

    private Menu buildSaveMenu() {
        final Menu save = new Menu();
        final Button saveButton = this.iconMenuButton(Icons.FA_SAVE);
        saveButton.setOnAction(event -> this.editor.save());
        saveButton.setTooltip(new Tooltip("Save"));
        save.setGraphic(saveButton);
        save.setStyle("-fx-background-color: transparent;");
        saveButton.disableProperty().bind(this.editor.mapUpdatedProperty().not());
        return save;
    }

    private Button iconMenuButton(Icons icon) {
        final Button assetManagerButton = new Button();
        final Icon assetManagerIcon = new Icon(icon);
        assetManagerIcon.setStyle("-fx-text-fill: black;");
        assetManagerButton.setGraphic(assetManagerIcon);
        assetManagerButton.setStyle("-fx-focus-color: transparent;-fx-faint-focus-color:transparent;");
        return assetManagerButton;
    }

}
