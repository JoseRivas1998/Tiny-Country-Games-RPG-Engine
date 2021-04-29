package com.tcg.rpgengine.editor.components.canvasses;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.UUID;

public class CharacterCellPreviewCanvas extends ResizableCanvas {

    private Image image;
    private UUID currentImageId;
    private double imageAspectRatio;
    private final Rectangle iconSrcRect;
    private final Rectangle iconDestRect;

    public CharacterCellPreviewCanvas(TiledImageAsset imageAsset, int charRow, int charColumn, int cellRow, int cellColumn) {
        super();
        this.iconSrcRect = new Rectangle();
        this.iconDestRect = new Rectangle();
        this.updateImageAsset(imageAsset, charRow, charColumn, cellRow, cellColumn);
    }

    public void updateImageAsset(TiledImageAsset imageAsset, int charRow, int charColumn, int cellRow, int cellColumn) {
        // dont load the same image twice lol
        if (this.currentImageId == null || !this.currentImageId.equals(imageAsset.id)) {
            this.currentImageId = imageAsset.id;
            final ApplicationContext context = ApplicationContext.context();
            final FileHandle projectFile = context.currentProject.getProjectFileHandle();
            final FileHandle imageAssetFile = projectFile.sibling(imageAsset.getPath());
            this.image = new Image(imageAssetFile.read());
        }
        final double charWidth = this.image.getWidth() / imageAsset.columns;
        final double charHeight = this.image.getHeight() / imageAsset.rows;
        final double charX = charColumn * charWidth;
        final double charY = charRow * charHeight;

        this.iconSrcRect.setWidth(charWidth / 3);
        this.iconSrcRect.setHeight(charHeight / 4);
        this.iconSrcRect.setX(charX + cellColumn * this.iconSrcRect.getWidth());
        this.iconSrcRect.setY(charY + cellRow * this.iconSrcRect.getHeight());
        this.imageAspectRatio = this.iconSrcRect.getWidth() / this.iconSrcRect.getHeight();
        this.draw();
    }

    @Override
    protected void draw() {
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double paddedWidth = width - ApplicationContext.Constants.SPACING * 2;
        final double paddedHeight = height - ApplicationContext.Constants.SPACING * 2;

        this.iconDestRect.setWidth(paddedWidth);
        this.iconDestRect.setHeight(paddedWidth * (1f / this.imageAspectRatio));

        if (Double.compare(this.iconDestRect.getHeight(), paddedHeight) >= 0) {
            this.iconDestRect.setHeight(paddedHeight);
            this.iconDestRect.setWidth(paddedHeight * this.imageAspectRatio);
        }

        this.iconDestRect.setX(this.imageRectX(paddedWidth));
        this.iconDestRect.setY(this.imageRectY(paddedHeight));

        final GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        gc.drawImage(
                this.image,
                this.iconSrcRect.getX(), this.iconSrcRect.getY(),
                this.iconSrcRect.getWidth(), this.iconSrcRect.getHeight(),

                this.iconDestRect.getX(), this.iconDestRect.getY(),
                this.iconDestRect.getWidth(), this.iconDestRect.getHeight()
        );

    }

    private double imageRectY(double paddedHeight) {
        return ApplicationContext.Constants.SPACING + paddedHeight * 0.5f - this.iconDestRect.getHeight() * 0.5f;
    }

    private double imageRectX(double paddedWidth) {
        return ApplicationContext.Constants.SPACING + paddedWidth * 0.5f - this.iconDestRect.getWidth() * 0.5f;
    }

    public Dimension2D getIconSize() {
        return new Dimension2D(this.iconSrcRect.getWidth(), this.iconSrcRect.getHeight());
    }

}
