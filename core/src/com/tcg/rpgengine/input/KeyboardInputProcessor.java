package com.tcg.rpgengine.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntMap;

public class KeyboardInputProcessor extends InputAdapter {

    private final IntMap<Controls> keyMap = new IntMap<>();

    public KeyboardInputProcessor() {
        super();
        // hard code key bindings for now
        this.keyMap.put(Input.Keys.UP, Controls.MOVE_UP);
        this.keyMap.put(Input.Keys.W, Controls.MOVE_UP);

        this.keyMap.put(Input.Keys.DOWN, Controls.MOVE_DOWN);
        this.keyMap.put(Input.Keys.S, Controls.MOVE_DOWN);

        this.keyMap.put(Input.Keys.LEFT, Controls.MOVE_LEFT);
        this.keyMap.put(Input.Keys.A, Controls.MOVE_LEFT);

        this.keyMap.put(Input.Keys.RIGHT, Controls.MOVE_RIGHT);
        this.keyMap.put(Input.Keys.D, Controls.MOVE_RIGHT);

        this.keyMap.put(Input.Keys.Z, Controls.ACTION);
        this.keyMap.put(Input.Keys.ENTER, Controls.ACTION);
        this.keyMap.put(Input.Keys.SPACE, Controls.ACTION);

        this.keyMap.put(Input.Keys.X, Controls.CANCEL);
        this.keyMap.put(Input.Keys.ESCAPE, Controls.CANCEL);

        this.keyMap.put(Input.Keys.SHIFT_LEFT, Controls.SPRINT);
        this.keyMap.put(Input.Keys.SHIFT_RIGHT, Controls.SPRINT);
    }

    @Override
    public boolean keyDown(int keycode) {
        return this.setControlValue(keycode, true);
    }

    @Override
    public boolean keyUp(int keycode) {
        return this.setControlValue(keycode, false);
    }

    private boolean setControlValue(int keycode, boolean value) {
        if (this.keyMap.containsKey(keycode)) {
            ControlInput.setControl(this.keyMap.get(keycode), value);
            return true;
        }
        return false;
    }
}
