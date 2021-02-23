package com.tcg.rpgengine.editor.components.canvasses;

import javafx.scene.canvas.Canvas;

public abstract class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        super();
        this.widthProperty().addListener((observable, oldValue, newValue) -> this.draw());
        this.heightProperty().addListener((observable, oldValue, newValue) -> this.draw());
    }

    protected abstract void draw();

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return this.getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return this.getHeight();
    }
}
