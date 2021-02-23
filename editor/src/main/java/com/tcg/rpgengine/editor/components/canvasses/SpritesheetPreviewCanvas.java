package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SpritesheetPreviewCanvas extends ResizableCanvas {

    private final SimpleIntegerProperty rowsProperty;
    private final SimpleIntegerProperty columnsProperty;
    private final double imageAspectRatio;
    private Image image;

    public SpritesheetPreviewCanvas(FileHandle image, int rows, int columns) {
        super();
        this.image = new Image(image.read());
        this.imageAspectRatio = this.image.getWidth() / this.image.getHeight();
        this.rowsProperty = new SimpleIntegerProperty(Math.max(rows, 1));
        this.columnsProperty = new SimpleIntegerProperty(Math.max(columns, 1));
        this.rowsProperty.addListener((observable, oldValue, newValue) -> this.draw());
        this.columnsProperty.addListener((observable, oldValue, newValue) -> this.draw());
    }

    @Override
    protected void draw() {
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double paddedWidth = width - ApplicationContext.Constants.SPACING * 2;
        final double paddedHeight = height - ApplicationContext.Constants.SPACING * 2;

        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.web("#000080"));
        gc.fillRect(0, 0, width, height);

        final Rectangle imageRect = new Rectangle();

        if (Double.compare(this.image.getWidth(), this.image.getHeight()) > 0) {
            imageRect.setWidth(paddedWidth);
            imageRect.setHeight(paddedWidth * (1f / this.imageAspectRatio));
        } else {
            imageRect.setHeight(paddedHeight);
            imageRect.setWidth(paddedHeight * this.imageAspectRatio);
        }

        imageRect.setX(ApplicationContext.Constants.SPACING + paddedWidth * 0.5f - imageRect.getWidth() * 0.5f);
        imageRect.setY(ApplicationContext.Constants.SPACING + paddedHeight * 0.5f - imageRect.getHeight() * 0.5f);

        gc.drawImage(this.image, imageRect.getX(), imageRect.getY(), imageRect.getWidth(), imageRect.getHeight());

        final int columns = this.columnsProperty.getValue();
        final int rows = this.rowsProperty.getValue();

        double lineX = 0;
        double lineY = 0;

        final Color cellBorder = Color.WHITE;
        final Color spritesheetBorder = Color.web("#00ffff");

        for (int i = 0; i <= columns; i++) {
            final double rectWidth = imageRect.getWidth() / columns;
            gc.setStroke(cellBorder);
            gc.setLineWidth(1.0);
            for (int j = 0; j <= 3 && i < columns; j++) {
                lineX = imageRect.getX() + (rectWidth * i) + ((rectWidth / 3) * j);
                gc.strokeLine(lineX, imageRect.getY(), lineX, imageRect.getY() + imageRect.getHeight());
            }
            lineX = imageRect.getX() + (rectWidth * i);
            gc.setStroke(spritesheetBorder);
            gc.setLineWidth(2.0);
            gc.strokeLine(lineX, imageRect.getY(), lineX, imageRect.getY() + imageRect.getHeight());
        }
        for (int i = 0; i <= rows; i++) {
            final double rectHeight = imageRect.getHeight() / rows;
            gc.setStroke(cellBorder);
            gc.setLineWidth(1.0);
            for (int j = 0; j <= 4 && i < rows; j++) {
                lineY = imageRect.getY() + (rectHeight * i) + ((rectHeight / 4) * j);
                gc.strokeLine(imageRect.getX(), lineY, imageRect.getX() + imageRect.getWidth(), lineY);
            }
            lineY = imageRect.getY() + (rectHeight * i);
            gc.setStroke(spritesheetBorder);
            gc.setLineWidth(2.0);
            gc.strokeLine(imageRect.getX(), lineY, imageRect.getX() + imageRect.getWidth(), lineY);
        }

    }

    public void setRows(int rows) {
        this.rowsProperty.setValue(Math.max(rows, 1));
    }

    public void setColumns(int columns) {
        this.columnsProperty.setValue(Math.max(columns, 1));
    }

}
