package com.tcg.rpgengine.input;

import com.badlogic.gdx.controllers.Controller;
import com.tcg.rpgengine.input.controller.RPGControllerMapping;
import com.tcg.rpgengine.input.controller.VirtualController;
import de.golfgl.gdx.controllers.mapping.MappedControllerAdapter;

import java.util.EnumMap;

public class ControllerInputProcessor extends MappedControllerAdapter {

    private final EnumMap<VirtualController, Controls> buttonMap = new EnumMap<>(VirtualController.class);
    private final EnumMap<VirtualController, AxisMapping> axisMap = new EnumMap<>(VirtualController.class);

    public ControllerInputProcessor() {
        super(new RPGControllerMapping());

        this.buttonMap.put(VirtualController.A, Controls.ACTION);
        this.buttonMap.put(VirtualController.B, Controls.CANCEL);
        this.buttonMap.put(VirtualController.Y, Controls.CANCEL);
        this.buttonMap.put(VirtualController.X, Controls.SPRINT);
        this.buttonMap.put(VirtualController.DPAD_UP, Controls.MOVE_UP);
        this.buttonMap.put(VirtualController.DPAD_DOWN, Controls.MOVE_DOWN);
        this.buttonMap.put(VirtualController.DPAD_LEFT, Controls.MOVE_LEFT);
        this.buttonMap.put(VirtualController.DPAD_RIGHT, Controls.MOVE_RIGHT);

        this.axisMap.put(VirtualController.LEFT_AXIS_Y, new AxisMapping(Controls.MOVE_UP, Controls.MOVE_DOWN));
    }

    @Override
    public boolean configuredButtonDown(Controller controller, int buttonId) {
        return this.setControlValue(buttonId, true);
    }

    @Override
    public boolean configuredButtonUp(Controller controller, int buttonId) {
        return this.setControlValue(buttonId, false);
    }

    @Override
    public boolean configuredAxisMoved(Controller controller, int axisId, float value) {
        final VirtualController virtualController = VirtualController.values()[axisId];
        if (this.axisMap.containsKey(virtualController)) {
            final AxisMapping axisMapping = this.axisMap.get(virtualController);
            if (Float.compare(value, 0f) == 0) {
                ControlInput.setControl(axisMapping.negativeControl, false);
                ControlInput.setControl(axisMapping.positiveControl, false);
            } else {
                final Controls controlActivated;
                final Controls controlDeactivated;
                if (Float.compare(value, 0f) > 0) {
                    controlActivated = axisMapping.positiveControl;
                    controlDeactivated = axisMapping.negativeControl;
                } else {
                    controlActivated = axisMapping.negativeControl;
                    controlDeactivated = axisMapping.positiveControl;
                }
                ControlInput.setControl(controlActivated, true);
                ControlInput.setControl(controlDeactivated, false);
            }
            return true;
        }
        return false;
    }

    private boolean setControlValue(int buttonId, boolean value) {
        final VirtualController virtualController = VirtualController.values()[buttonId];
        if (this.buttonMap.containsKey(virtualController)) {
            ControlInput.setControl(this.buttonMap.get(virtualController), value);
            return true;
        }
        return false;
    }

    private class AxisMapping {
        final Controls negativeControl;
        final Controls positiveControl;

        private AxisMapping(Controls negativeControl, Controls positiveControl) {
            this.negativeControl = negativeControl;
            this.positiveControl = positiveControl;
        }
    }


}
