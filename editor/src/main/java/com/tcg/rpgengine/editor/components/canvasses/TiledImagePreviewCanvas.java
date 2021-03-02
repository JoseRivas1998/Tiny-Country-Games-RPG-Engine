package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TiledImagePreviewCanvas extends ImagePreviewCanvas {

    private final SimpleIntegerProperty rowsProperty;
    private final SimpleIntegerProperty columnsProperty;

    public TiledImagePreviewCanvas(FileHandle image, int rows, int columns) {
        super(image);
        this.rowsProperty = new SimpleIntegerProperty(Math.max(rows, 1));
        this.columnsProperty = new SimpleIntegerProperty(Math.max(columns, 1));
        this.rowsProperty.addListener((observable, oldValue, newValue) -> this.draw());
        this.columnsProperty.addListener((observable, oldValue, newValue) -> this.draw());
    }

    @Override
    protected void draw() {
        super.draw();
        final GraphicsContext gc = this.getGraphicsContext2D();

        final int columns = this.columnsProperty.getValue();
        final int rows = this.rowsProperty.getValue();
        final double cellWidth = this.imageRect.getWidth() / columns;
        final double cellHeight = this.imageRect.getHeight() / rows;

        double lineX = 0;
        double lineY = 0;

        gc.setStroke(Color.web("#00ffff"));
        gc.setLineWidth(2.0);

        for (int i = 0; i <= columns; i++) {
            lineX = this.imageRect.getX() + (cellWidth * i);
            gc.strokeLine(
                    lineX, this.imageRect.getY(),
                    lineX, this.imageRect.getY() + this.imageRect.getHeight()
            );
        }

        for (int i = 0; i <= rows; i++) {
            lineY = this.imageRect.getY() + (cellHeight * i);
            gc.strokeLine(
                    this.imageRect.getX(), lineY,
                    this.imageRect.getX() + this.imageRect.getWidth(), lineY
            );
        }
        
    }

    public void setRows(int rows) {
        this.rowsProperty.setValue(Math.max(rows, 1));
    }

    public void setColumns(int columns) {
        this.columnsProperty.setValue(Math.max(columns, 1));
    }
}
