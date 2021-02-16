package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.utils.NumberUtils;
import javafx.scene.control.TextField;

public class FloatField extends TextField {

    public FloatField() {
        this(0f);
    }

    public FloatField(float initialValue) {
        super(NumberUtils.toString(initialValue));
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || (!newValue.isEmpty() && !this.isFloat(newValue))) {
                this.setText(oldValue);
            }
        });
    }

    private boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public float getFloatValue() {
        return this.getText().isBlank() ? 0f : Float.parseFloat(this.getText());
    }

    public void setFloatValue(float x) {
        this.setText(NumberUtils.toString(x));
    }

}
