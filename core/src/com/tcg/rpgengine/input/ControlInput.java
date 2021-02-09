package com.tcg.rpgengine.input;

import java.util.EnumMap;

public class ControlInput {

    private final static EnumMap<Controls, Boolean> inputValues = new EnumMap<>(Controls.class);
    private final static EnumMap<Controls, Boolean> previousInputValues = new EnumMap<>(Controls.class);

    public static void update() {
        for (Controls control : Controls.values()) {
            previousInputValues.put(control, inputValues.getOrDefault(control, false));
        }
    }

    static void setControl(Controls control, boolean value) {
        inputValues.put(control, value);
    }

    public static boolean controlCheck(Controls control) {
        return inputValues.getOrDefault(control, false);
    }

    public static boolean controlCheckPressed(Controls control) {
        return inputValues.getOrDefault(control, false) &&
                !previousInputValues.getOrDefault(control, false);
    }



}
