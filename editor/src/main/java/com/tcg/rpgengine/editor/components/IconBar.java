package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.editor.components.fontawesome.Icon;
import com.tcg.rpgengine.editor.components.fontawesome.Icons;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tooltip;

public class IconBar extends MenuBar {

    public IconBar() {
        super();
        this.getMenus().addAll(
                this.buildDatabaseManager(),
                this.buildAssetManager()
        );

    }

    private Menu buildDatabaseManager() {
        final Menu databaseManager = new Menu();
        final Button databaseManagerButton = this.iconMenuButton(Icons.FA_COGS);
        databaseManagerButton.setOnAction(event -> ApplicationContext.context().openDatabaseManager());
        databaseManagerButton.setTooltip(new Tooltip("Database Manager"));
        databaseManager.setGraphic(databaseManagerButton);
        databaseManager.setStyle("-fx-background-color: transparent;");
        return databaseManager;
    }

    private Menu buildAssetManager() {
        final Menu assetManager = new Menu();
        final Button assetManagerButton = this.iconMenuButton(Icons.FA_FOLDER);
        assetManagerButton.setOnAction(event -> ApplicationContext.context().openAssetManager());
        assetManagerButton.setTooltip(new Tooltip("Asset Manager"));
        assetManager.setGraphic(assetManagerButton);
        assetManager.setStyle("-fx-background-color: transparent;");
        return assetManager;
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
