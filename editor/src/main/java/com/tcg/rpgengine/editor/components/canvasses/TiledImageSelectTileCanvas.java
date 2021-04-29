package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TiledImageSelectTileCanvas extends ImagePreviewCanvas {

    private final SimpleIntegerProperty rowsProperty;
    private final SimpleIntegerProperty columnsProperty;

    private final SimpleIntegerProperty selectedRowProperty;
    private final SimpleIntegerProperty selectedColumnProperty;

    public TiledImageSelectTileCanvas(FileHandle image, int rows, int columns) {
        super(image);
        this.rowsProperty = new SimpleIntegerProperty(Math.max(rows, 1));
        this.columnsProperty = new SimpleIntegerProperty(Math.max(columns, 1));

        this.selectedRowProperty = new SimpleIntegerProperty(0);
        this.selectedColumnProperty = new SimpleIntegerProperty(0);

        this.rowsProperty.addListener((observable, oldValue, newValue) -> this.draw());
        this.columnsProperty.addListener((observable, oldValue, newValue) -> this.draw());

        this.selectedRowProperty.addListener((observable, oldValue, newValue) -> this.draw());
        this.selectedColumnProperty.addListener((observable, oldValue, newValue) -> this.draw());
        this.setOnMouseClicked(event -> {
            if (this.pointIsInRect(event.getX(), event.getY(), this.imageRect)) {
                final double relativeX = event.getX() - this.imageRect.getX();
                final double relativeY = event.getY() - this.imageRect.getY();

                final double cellWidth = this.imageRect.getWidth() / this.columnsProperty.getValue();
                final double cellHeight = this.imageRect.getHeight() / this.rowsProperty.getValue();

                final int column = (int) (relativeX / cellWidth);
                final int row = (int) (relativeY / cellHeight);

                this.selectedRowProperty.set(row);
                this.selectedColumnProperty.set(column);
            }
        });
    }

    private boolean pointIsInRect(double x, double y, Rectangle rectangle) {
        return x >= rectangle.getX() && x <= rectangle.getX() + rectangle.getWidth() &&
                y >= rectangle.getY() && y <= rectangle.getY() + rectangle.getHeight();
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

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.0);

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

        gc.setStroke(Color.web("#00ffff"));
        gc.setLineWidth(5.0);

        final double selectedCellX = this.imageRect.getX() + cellWidth * this.selectedColumnProperty.getValue();
        final double selectedCellY = this.imageRect.getY() + cellHeight * this.selectedRowProperty.getValue();
        gc.strokeRect(selectedCellX, selectedCellY, cellWidth, cellHeight);
        
        
    }

    public void setRows(int rows) {
        this.rowsProperty.setValue(Math.max(rows, 1));
    }

    public void setColumns(int columns) {
        this.columnsProperty.setValue(Math.max(columns, 1));
    }

    public int getSelectedRow() {
        return this.selectedRowProperty.getValue();
    }

    public void setSelectedRow(int row) {
        this.selectedRowProperty.setValue(Math.max(Math.min(this.rowsProperty.getValue() - 1, row), 0));
    }

    public int getSelectedColumn() {
        return this.selectedColumnProperty.getValue();
    }

    public void setSelectedColumn(int column) {
        this.selectedColumnProperty.setValue(Math.max(Math.min(this.columnsProperty.getValue() - 1, column), 0));
    }

    public void setImage(FileHandle image, int rows, int cols) {
        this.rowsProperty.set(rows);
        this.columnsProperty.set(cols);
        this.setImage(image);
    }

}
