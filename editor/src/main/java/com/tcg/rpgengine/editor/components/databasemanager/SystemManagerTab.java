package com.tcg.rpgengine.editor.components.databasemanager;

import com.tcg.rpgengine.editor.components.databasemanager.systemmanagerpages.SystemManagerLayout;
import javafx.scene.control.Tab;
import javafx.stage.Window;

public class SystemManagerTab extends Tab {

    public SystemManagerTab(Window owner) {
        super("System");
        this.setClosable(false);
        this.setContent(new SystemManagerLayout(owner));
    }

}
