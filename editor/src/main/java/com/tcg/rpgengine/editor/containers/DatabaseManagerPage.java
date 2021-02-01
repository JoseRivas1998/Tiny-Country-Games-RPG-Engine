package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.TitleEditorPane;
import com.tcg.rpgengine.editor.components.SystemSoundEditorPane;
import com.tcg.rpgengine.editor.components.UISkinEditorPane;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.stage.Window;

public class DatabaseManagerPage extends FlowPane {

    public DatabaseManagerPage(Window owner) {
        super();
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.getChildren().addAll(
                new TitleEditorPane(owner),
                new SystemSoundEditorPane(owner),
                new UISkinEditorPane(owner)
        );
    }

}
