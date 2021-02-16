package com.tcg.rpgengine.editor.dialogs;

import com.tcg.rpgengine.common.data.system.GlobalVariable;
import com.tcg.rpgengine.editor.components.FloatField;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.UUID;

public class GlobalVariableDialog extends Dialog<Pair<String, Float>> {

    private final TextField nameField;
    private final FloatField initialValueField;

    public GlobalVariableDialog(GlobalVariable globalVariable) {
        this();
        this.nameField.setText(globalVariable.getName());
        this.initialValueField.setFloatValue(globalVariable.initialValue);
    }

    public GlobalVariableDialog() {
        super();
        this.setHeaderText("Global Variable");
        this.setTitle("Global Variable");
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final GridPane formPane = new GridPane();
        formPane.setHgap(ApplicationContext.Constants.SPACING);
        formPane.setVgap(ApplicationContext.Constants.SPACING);
        formPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.nameField = new TextField();
        formPane.add(new Label("Name"), 0, 0);
        formPane.add(this.nameField, 1, 0);

        this.initialValueField = new FloatField();
        formPane.add(new Label("Initial Value"), 0, 1);
        formPane.add(this.initialValueField, 1, 1);

        this.getDialogPane().setContent(formPane);

        this.setResultConverter(param -> {
            if (param != ButtonType.OK) return null;
            return new Pair<>(this.nameField.getText(), this.initialValueField.getFloatValue());
        });

    }

}
