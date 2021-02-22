package com.tcg.rpgengine.editor.dialogs;

import com.tcg.rpgengine.common.data.system.GlobalFlag;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class GlobalFlagDialog extends Dialog<Pair<String, Boolean>> {

    private final TextField nameField;
    private final CheckBox initialValueBox;

    public GlobalFlagDialog() {
        super();
        this.setHeaderText("Global Flag");
        this.setTitle("Global Flag");
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final GridPane formPane = new GridPane();
        formPane.setVgap(ApplicationContext.Constants.SPACING);
        formPane.setHgap(ApplicationContext.Constants.SPACING);
        formPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.nameField = new TextField();
        formPane.add(new Label("Name:"), 0, 0);
        formPane.add(this.nameField, 1, 0);

        this.initialValueBox = new CheckBox();
        formPane.add(new Label("Initial Value"), 0, 1);
        formPane.add(this.initialValueBox, 1, 1);

        this.getDialogPane().setContent(formPane);

        this.setResultConverter(param -> {
            if (param != ButtonType.OK) return null;
            return new Pair<>(this.nameField.getText(), this.initialValueBox.isSelected());
        });
    }

    public GlobalFlagDialog(GlobalFlag globalFlag) {
        this();
        this.nameField.setText(globalFlag.getName());
        this.initialValueBox.setSelected(globalFlag.initialValue);
    }

}
