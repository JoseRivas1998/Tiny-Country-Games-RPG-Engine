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
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.Optional;
import java.util.UUID;

public class GlobalVariableEditor extends VBox {

    private final GlobalVariableTableView globalVariableTableView;

    public GlobalVariableEditor(Window owner) {
        super(ApplicationContext.Constants.SPACING);
        this.globalVariableTableView = this.buildGlobalVariableTableView();
        this.getChildren().addAll(this.buildTitleLabel(), this.globalVariableTableView, this.buildButtonBox(owner));
    }

    private Label buildTitleLabel() {
        final Label title = new Label("Global Variables");
        title.setStyle("-fx-font-weight: bold;");
        return title;
    }

    private HBox buildButtonBox(Window owner) {

        final HBox actionBox = new HBox(ApplicationContext.Constants.SPACING);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(
                this.buildAddButton(owner),
                this.buildEditButton(owner),
                this.buildRemoveButton(owner));
        return actionBox;
    }

    private Button buildRemoveButton(Window owner) {
        final Button remove = new Button("Remove");
        remove.disableProperty().bind(this.globalVariableTableView.getSelectionModel().selectedItemProperty().isNull());
        remove.setOnAction(event -> this.removeSelectedGlobalVariable(owner));
        return remove;
    }

    private Button buildEditButton(Window owner) {
        final Button edit = new Button("Edit");
        edit.disableProperty().bind(this.globalVariableTableView.getSelectionModel().selectedItemProperty().isNull());
        edit.setOnAction(event -> this.editSelectedGlobalVariable(owner));
        return edit;
    }

    private Button buildAddButton(Window owner) {
        final Button add = new Button("Add");
        add.setOnAction(event -> this.inputNewGlobalVariable(owner));
        return add;
    }

    private GlobalVariableTableView buildGlobalVariableTableView() {
        final GlobalVariableTableView globalVariableTableView = new GlobalVariableTableView();
        globalVariableTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        globalVariableTableView.getItems().setAll(ApplicationContext.context().currentProject.systemData.getAllGlobalVariables());
        VBox.setVgrow(globalVariableTableView, Priority.ALWAYS);
        return globalVariableTableView;
    }

    private void removeSelectedGlobalVariable(Window owner) {
        this.getSelectedGlobalVariable().ifPresent(globalVariable -> this.removeGlobalVariable(owner, globalVariable));
    }

    private void removeGlobalVariable(Window owner, GlobalVariable globalVariable) {
        try {
            final CurrentProject currentProject = ApplicationContext.context().currentProject;
            currentProject.systemData.removeGlobalVariable(globalVariable.id);
            currentProject.saveSystemData();
            final int selectedIndex = this.globalVariableTableView.getSelectionModel().getSelectedIndex();
            this.globalVariableTableView.getItems().remove(selectedIndex);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void editSelectedGlobalVariable(Window owner) {
        this.getSelectedGlobalVariable().ifPresent(globalVariable -> this.editGlobalVariable(owner, globalVariable));
    }

    private Optional<GlobalVariable> getSelectedGlobalVariable() {
        return Optional.ofNullable(this.globalVariableTableView.getSelectionModel().getSelectedItem());
    }

    private void editGlobalVariable(Window owner, GlobalVariable globalVariable) {
        try {
            final GlobalVariableDialog globalVariableDialog = new GlobalVariableDialog(globalVariable);
            globalVariableDialog.initOwner(owner);
            final Optional<Pair<String, Float>> optionalGlobalVariable = globalVariableDialog.showAndWait();
            if (optionalGlobalVariable.isPresent()) {
                final Pair<String, Float> globalVariableValues = optionalGlobalVariable.get();
                final String name = globalVariableValues.getKey();
                final float initialValue = globalVariableValues.getValue();
                final UUID id = globalVariable.id;
                if (name.isBlank()) {
                    throw new IllegalArgumentException("Global variable name cannot be blank.");
                }
                this.updateGlobalVariable(id, name, initialValue);
            }
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void updateGlobalVariable(UUID id, String name, float initialValue) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final GlobalVariable globalVariable = currentProject.systemData.getGlobalVariable(id);
        globalVariable.setName(name);
        globalVariable.initialValue = initialValue;
        currentProject.saveSystemData();
        final int selectedIndex = this.globalVariableTableView.getSelectionModel().getSelectedIndex();
        this.globalVariableTableView.getItems().set(selectedIndex, globalVariable);
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
