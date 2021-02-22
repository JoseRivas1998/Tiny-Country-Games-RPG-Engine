package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.system.GlobalFlag;
import com.tcg.rpgengine.editor.components.tableviews.GlobalFlagTableView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.GlobalFlagDialog;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.Optional;
import java.util.UUID;

public class GlobalFlagEditor extends VBox {

    private final GlobalFlagTableView globalFlagTableView;

    public GlobalFlagEditor(Window owner) {
        super(ApplicationContext.Constants.SPACING);
        this.globalFlagTableView = this.buildGlobalFlagTableView();
        this.getChildren().addAll(this.globalFlagTableView, this.buildButtonBox(owner));
    }

    private HBox buildButtonBox(Window owner) {
        final HBox buttonBox = new HBox(ApplicationContext.Constants.SPACING);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(
                this.buildAddButton(owner),
                this.buildEditButton(owner),
                this.buildRemoveButton(owner)
        );
        return buttonBox;
    }

    private Button buildRemoveButton(Window owner) {
        final Button remove = new Button("Remove");
        remove.disableProperty().bind(this.globalFlagTableView.getSelectionModel().selectedItemProperty().isNull());
        remove.setOnAction(event -> this.removeSelectedGlobalFlag(owner));
        return remove;
    }

    private void removeSelectedGlobalFlag(Window owner) {
        this.getSelectedFlag().ifPresent(selectedGlobalFlag -> this.removeGlobalFlag(owner, selectedGlobalFlag));
    }

    private void removeGlobalFlag(Window owner, GlobalFlag globalFlag) {
        try {
            ApplicationContext.context().currentProject.systemData.removeGlobalFlag(globalFlag.id);
            ApplicationContext.context().currentProject.saveSystemData();
            final int selectedIndex = this.globalFlagTableView.getSelectionModel().getSelectedIndex();
            this.globalFlagTableView.getItems().remove(selectedIndex);
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private Button buildEditButton(Window owner) {
        final Button edit = new Button("Edit");
        edit.disableProperty().bind(this.globalFlagTableView.getSelectionModel().selectedItemProperty().isNull());
        edit.setOnAction(event -> this.updateSelectedGlobalFlag(owner));
        return edit;
    }

    private void updateSelectedGlobalFlag(Window owner) {
        this.getSelectedFlag().ifPresent(selectedGlobalFlag -> this.inputGlobalFlagUpdate(owner, selectedGlobalFlag));
    }

    private void inputGlobalFlagUpdate(Window owner, GlobalFlag selectedGlobalFlag) {
        try {
            final GlobalFlagDialog globalFlagDialog = new GlobalFlagDialog(selectedGlobalFlag);
            globalFlagDialog.initOwner(owner);
            final Optional<Pair<String, Boolean>> optionalGlobalFlagValues = globalFlagDialog.showAndWait();
            if (optionalGlobalFlagValues.isPresent()) {
                final Pair<String, Boolean> globalFlagValues = optionalGlobalFlagValues.get();
                final UUID id = selectedGlobalFlag.id;
                final String name = globalFlagValues.getKey();
                final boolean initialValue = globalFlagValues.getValue();
                this.updateGlobalFlag(id, name, initialValue);
            }
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void updateGlobalFlag(UUID id, String name, boolean initialValue) {
        this.validateGlobalFlagName(name);
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        final GlobalFlag globalFlag = currentProject.systemData.getGlobalFlag(id);
        globalFlag.setName(name);
        globalFlag.initialValue = initialValue;
        currentProject.saveSystemData();
        final int selectedIndex = this.globalFlagTableView.getSelectionModel().getSelectedIndex();
        this.globalFlagTableView.getItems().set(selectedIndex, globalFlag);
    }

    private Optional<GlobalFlag> getSelectedFlag() {
        return Optional.ofNullable(this.globalFlagTableView.getSelectionModel().getSelectedItem());
    }

    private Button buildAddButton(Window owner) {
        final Button add = new Button("Add");
        add.setOnAction(event -> this.inputNewGlobalFlag(owner));
        return add;
    }

    private GlobalFlagTableView buildGlobalFlagTableView() {
        final GlobalFlagTableView globalFlagTableView = new GlobalFlagTableView();
        globalFlagTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        globalFlagTableView.getItems().setAll(ApplicationContext.context().currentProject.systemData.getAllGlobalFlags());
        VBox.setVgrow(globalFlagTableView, Priority.ALWAYS);
        return globalFlagTableView;
    }

    private void inputNewGlobalFlag(Window owner) {
        try {
            final GlobalFlagDialog globalFlagDialog = new GlobalFlagDialog();
            globalFlagDialog.initOwner(owner);
            final Optional<Pair<String, Boolean>> optionalGlobalFlagValues = globalFlagDialog.showAndWait();
            if (optionalGlobalFlagValues.isPresent()) {
                final Pair<String, Boolean> globalFlagValues = optionalGlobalFlagValues.get();
                final String name = globalFlagValues.getKey();
                final boolean initialValue = globalFlagValues.getValue();
                this.addGlobalFlag(name, initialValue);
            }
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void addGlobalFlag(String name, boolean initialValue) {
        final CurrentProject currentProject = ApplicationContext.context().currentProject;
        this.validateGlobalFlagName(name);
        final GlobalFlag globalFlag = GlobalFlag.createNewGlobalFlag(name, initialValue);
        currentProject.systemData.addGlobalFlag(globalFlag);
        currentProject.saveSystemData();
        this.globalFlagTableView.getItems().add(globalFlag);
    }

    private void validateGlobalFlagName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Global flag name cannot be blank.");
        }
    }

}
