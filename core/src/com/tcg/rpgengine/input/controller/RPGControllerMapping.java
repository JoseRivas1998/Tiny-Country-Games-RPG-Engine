package com.tcg.rpgengine.input.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerMapping;
import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;

public class RPGControllerMapping extends ControllerMappings {

    private static final VirtualController[] buttons = {
            VirtualController.A,
            VirtualController.B,
            VirtualController.X,
            VirtualController.Y,
            VirtualController.DPAD_UP,
            VirtualController.DPAD_DOWN,
            VirtualController.DPAD_RIGHT,
            VirtualController.DPAD_LEFT,
    };

    public RPGControllerMapping() {
        super();

        for (VirtualController button : buttons) {
            this.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, button.ordinal()));
        }
        this.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital,
                VirtualController.LEFT_AXIS_X.ordinal()));
        this.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital,
                VirtualController.LEFT_AXIS_Y.ordinal()));


        this.commitConfig();
    }


    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping, Controller controller) {
        final ControllerMapping mapping = controller.getMapping();

        defaultMapping.putMapping(this.createMappedButton(VirtualController.A, mapping.buttonA));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.B, mapping.buttonB));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.X, mapping.buttonX));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.Y, mapping.buttonY));

        defaultMapping.putMapping(this.createMappedButton(VirtualController.DPAD_UP, mapping.buttonDpadUp));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.DPAD_DOWN, mapping.buttonDpadDown));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.DPAD_LEFT, mapping.buttonDpadLeft));
        defaultMapping.putMapping(this.createMappedButton(VirtualController.DPAD_RIGHT, mapping.buttonDpadRight));

        defaultMapping.putMapping(this.createMappedAxis(VirtualController.LEFT_AXIS_X, mapping.axisLeftX));
        defaultMapping.putMapping(this.createMappedAxis(VirtualController.LEFT_AXIS_Y, mapping.axisLeftY));

        return true;
    }

    private MappedInput createMappedAxis(VirtualController virtualController, int axisIndex) {
        return new MappedInput(virtualController.ordinal(), new ControllerAxis(axisIndex));
    }

    private MappedInput createMappedButton(VirtualController virtualController, int buttonIndex) {
        return new MappedInput(virtualController.ordinal(), new ControllerButton(buttonIndex));
    }


}
