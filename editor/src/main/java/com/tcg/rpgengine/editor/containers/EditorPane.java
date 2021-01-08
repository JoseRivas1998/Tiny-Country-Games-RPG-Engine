package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EditorPane extends GridPane {

    public EditorPane() {
        super();
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.setPadding(new Insets(ApplicationContext.Constants.PADDING));
        final Label label = new Label("One day you will be able to do stuff here!");
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        label.setAlignment(Pos.CENTER);
        this.add(label, 0, 0);
    }

}
