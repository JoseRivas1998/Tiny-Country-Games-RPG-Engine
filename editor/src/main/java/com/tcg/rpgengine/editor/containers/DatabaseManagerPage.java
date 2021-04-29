package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.databasemanager.ActorsTab;
import com.tcg.rpgengine.editor.components.databasemanager.ElementsTab;
import com.tcg.rpgengine.editor.components.databasemanager.SystemManagerTab;
import com.tcg.rpgengine.editor.components.databasemanager.systemmanagerpages.*;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class DatabaseManagerPage extends TabPane {

    public DatabaseManagerPage(Window owner) {
        super();
        this.getTabs().setAll(
                new SystemManagerTab(owner),
                new ElementsTab(owner),
                new ActorsTab(owner)
        );
    }

}
