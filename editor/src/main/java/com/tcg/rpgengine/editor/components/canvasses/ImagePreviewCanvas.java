package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ImagePreviewCanvas extends ResizableCanvas{
    protected double imageAspectRatio;
    protected Image image;
    protected final Rectangle imageRect;

    public ImagePreviewCanvas(FileHandle image) {
        super();
        this.setImage(image);
        this.imageRect = new Rectangle();
    }

    @Override
    protected void draw() {
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double paddedWidth = width - ApplicationContext.Constants.SPACING * 2;
        final double paddedHeight = height - ApplicationContext.Constants.SPACING * 2;

        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.web(ApplicationContext.Constants.IMAGE_PREVIEW_BACKGROUND));
        gc.fillRect(0, 0, width, height);

        this.imageRect.setWidth(paddedWidth);
        this.imageRect.setHeight(paddedWidth * (1f / this.imageAspectRatio));

        if (Double.compare(this.imageRect.getHeight(), paddedHeight) >= 0) {
            this.imageRect.setHeight(paddedHeight);
            this.imageRect.setWidth(paddedHeight * this.imageAspectRatio);
        }

        this.imageRect.setX(this.imageRectX(paddedWidth));
        this.imageRect.setY(this.imageRectY(paddedHeight));

        gc.drawImage(
                this.image,
                this.imageRect.getX(), this.imageRect.getY(),
                this.imageRect.getWidth(), this.imageRect.getHeight()
        );

    }
    
    public void setImage(FileHandle image) {
        final boolean shouldDraw = this.image != null;
        this.image = new Image(image.read());
        this.imageAspectRatio = this.image.getWidth() / this.image.getHeight();
        if (shouldDraw) this.draw();
    }

    private double imageRectY(double paddedHeight) {
        return ApplicationContext.Constants.SPACING + paddedHeight * 0.5f - this.imageRect.getHeight() * 0.5f;
    }

    private double imageRectX(double paddedWidth) {
        return ApplicationContext.Constants.SPACING + paddedWidth * 0.5f - this.imageRect.getWidth() * 0.5f;
    }

}
