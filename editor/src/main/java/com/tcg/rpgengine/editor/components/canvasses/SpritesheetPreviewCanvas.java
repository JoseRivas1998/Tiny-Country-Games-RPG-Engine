package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SpritesheetPreviewCanvas extends ImagePreviewCanvas {

    private final SimpleIntegerProperty rowsProperty;
    private final SimpleIntegerProperty columnsProperty;

    public SpritesheetPreviewCanvas(FileHandle image, int rows, int columns) {
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

        double lineX = 0;
        double lineY = 0;

        final Color cellBorder = Color.WHITE;
        final Color spritesheetBorder = Color.web("#00ffff");

        for (int i = 0; i <= columns; i++) {
            final double rectWidth = this.imageRect.getWidth() / columns;
            gc.setStroke(cellBorder);
            gc.setLineWidth(1.0);
            for (int j = 0; j <= 3 && i < columns; j++) {
                lineX = this.imageRect.getX() + (rectWidth * i) + ((rectWidth / 3) * j);
                gc.strokeLine(
                        lineX, this.imageRect.getY(),
                        lineX, this.imageRect.getY() + this.imageRect.getHeight()
                );
            }
            lineX = this.imageRect.getX() + (rectWidth * i);
            gc.setStroke(spritesheetBorder);
            gc.setLineWidth(2.0);
            gc.strokeLine(
                    lineX, this.imageRect.getY(),
                    lineX, this.imageRect.getY() + this.imageRect.getHeight());
        }
        for (int i = 0; i <= rows; i++) {
            final double rectHeight = this.imageRect.getHeight() / rows;
            gc.setStroke(cellBorder);
            gc.setLineWidth(1.0);
            for (int j = 0; j <= 4 && i < rows; j++) {
                lineY = this.imageRect.getY() + (rectHeight * i) + ((rectHeight / 4) * j);
                gc.strokeLine(
                        this.imageRect.getX(), lineY,
                        this.imageRect.getX() + this.imageRect.getWidth(), lineY
                );
            }
            lineY = this.imageRect.getY() + (rectHeight * i);
            gc.setStroke(spritesheetBorder);
            gc.setLineWidth(2.0);
            gc.strokeLine(this.imageRect.getX(), lineY, this.imageRect.getX() + this.imageRect.getWidth(), lineY);
        }

    }

    public void setRows(int rows) {
        this.rowsProperty.setValue(Math.max(rows, 1));
    }

    public void setColumns(int columns) {
        this.columnsProperty.setValue(Math.max(columns, 1));
    }

}
