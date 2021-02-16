package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.GlobalVariableEditor;
import com.tcg.rpgengine.editor.components.TitleEditorPane;
import com.tcg.rpgengine.editor.components.SystemSoundEditorPane;
import com.tcg.rpgengine.editor.components.UISkinEditorPane;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class DatabaseManagerPage extends GridPane {

    public DatabaseManagerPage(Window owner) {
        super();
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setVgap(ApplicationContext.Constants.SPACING);
        final GlobalVariableEditor globalVariableEditor = new GlobalVariableEditor(owner);
        GridPane.setVgrow(globalVariableEditor, Priority.ALWAYS);
        this.add(globalVariableEditor, 0, 0);
        this.add(this.buildMainPane(owner), 1, 0);
    }

    private FlowPane buildMainPane(Window owner) {
        final FlowPane mainPane = new FlowPane();
        mainPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        mainPane.setHgap(ApplicationContext.Constants.SPACING);
        mainPane.setVgap(ApplicationContext.Constants.SPACING);
        mainPane.getChildren().addAll(
                new TitleEditorPane(owner),
                new SystemSoundEditorPane(owner),
                new UISkinEditorPane(owner)
        );
        GridPane.setVgrow(mainPane, Priority.ALWAYS);
        GridPane.setHgrow(mainPane, Priority.ALWAYS);
        return mainPane;
    }

}
