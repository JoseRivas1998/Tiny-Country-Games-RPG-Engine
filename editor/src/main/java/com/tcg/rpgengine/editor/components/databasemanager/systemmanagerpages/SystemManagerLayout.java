package com.tcg.rpgengine.editor.components.databasemanager.systemmanagerpages;

import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class SystemManagerLayout extends GridPane {

    public SystemManagerLayout(Window owner) {
        super();
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setVgap(ApplicationContext.Constants.SPACING);
        final GlobalVariableEditor globalVariableEditor = new GlobalVariableEditor(owner);
        GridPane.setVgrow(globalVariableEditor, Priority.ALWAYS);
        this.add(globalVariableEditor, 0, 0);
        final GlobalFlagEditor globalFlagEditor = new GlobalFlagEditor(owner);
        GridPane.setVgrow(globalFlagEditor, Priority.ALWAYS);
        this.add(globalFlagEditor, 0, 1);
        this.add(this.buildMainPane(owner), 1, 0, 1, 2);
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
