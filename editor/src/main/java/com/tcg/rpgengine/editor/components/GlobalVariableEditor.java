package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.system.GlobalVariable;
import com.tcg.rpgengine.editor.components.tableviews.GlobalVariableTableView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.GlobalVariableDialog;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.Optional;

public class GlobalVariableEditor extends VBox {

    private final GlobalVariableTableView globalVariableTableView;

    public GlobalVariableEditor(Window owner) {
        super(ApplicationContext.Constants.SPACING);
        final Label title = new Label("Global Variables");
        title.setStyle("-fx-font-weight: bold;");

        final GlobalVariableTableView globalVariableTableView = new GlobalVariableTableView();
        globalVariableTableView.getItems().setAll(ApplicationContext.context().currentProject.systemData.getAllGlobalVariables());
        VBox.setVgrow(globalVariableTableView, Priority.ALWAYS);
        this.globalVariableTableView = globalVariableTableView;

        final Button add = new Button("Add");
        add.setOnAction(event -> this.inputNewGlobalVariable(owner));

        final HBox actionBox = new HBox(ApplicationContext.Constants.SPACING);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(add);

        this.getChildren().addAll(title, globalVariableTableView, actionBox);
    }

    private void inputNewGlobalVariable(Window owner) {
        try {
            final GlobalVariableDialog globalVariableDialog = new GlobalVariableDialog();
            globalVariableDialog.initOwner(owner);
            final Optional<Pair<String, Float>> optionalGlobalVariable = globalVariableDialog.showAndWait();
            if (optionalGlobalVariable.isPresent()) {
                final Pair<String, Float> globalVariableValues = optionalGlobalVariable.get();
                final String name = globalVariableValues.getKey();
                final float initialValue = globalVariableValues.getValue();
                this.globalVariableTableView.getItems().add(this.addNewGlobalVariable(name, initialValue));
            }
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private GlobalVariable addNewGlobalVariable(String name, float initialValue) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Global variable name cannot be blank.");
        }
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final GlobalVariable globalVariable = GlobalVariable.createNewGlobalVariable(name, initialValue);
        currentProject.systemData.addGlobalVariable(globalVariable);
        currentProject.saveSystemData();
        return globalVariable;
    }

}
