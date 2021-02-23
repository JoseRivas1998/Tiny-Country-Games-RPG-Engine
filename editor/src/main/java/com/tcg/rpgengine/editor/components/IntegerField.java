package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.utils.NumberUtils;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

public class IntegerField extends TextField {

    private final SimpleIntegerProperty intValueProperty;

    public IntegerField() {
        this(0);
    }

    public IntegerField(int initialValue) {
        super(NumberUtils.toString(initialValue));
        this.intValueProperty = new SimpleIntegerProperty(initialValue);
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || (!newValue.isEmpty() && !this.isInt(newValue))) {
                this.setText(oldValue);
            } else {
                this.intValueProperty.setValue(this.intValueFromText(newValue));
            }
        });

    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int intValueFromText(String text) {
        return text.isBlank() ? 0 : Integer.parseInt(text);
    }

    public void setIntValue(int x) {
        this.setText(Integer.toString(x));
    }

    public int getIntValue() {
        return this.intValueProperty.intValue();
    }

    public ReadOnlyIntegerProperty integerProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(this.intValueProperty);
    }

}
