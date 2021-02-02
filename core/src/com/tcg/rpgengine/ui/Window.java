package com.tcg.rpgengine.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import com.tcg.rpgengine.entities.RectangleEntity;

import java.util.UUID;

public class Window extends RectangleEntity {

    private static final int BORDER_SIZE = 8;
    private final TextureRegion background;
    private final TextureRegion texture;
    private final NinePatch border;

    protected Window(TCGRPGGame game) {
        super();
        final UUID windowSkinId = game.systemData.windowSkin.getWindowSkinId();
        final ImageAsset windowImageAsset = game.assetLibrary.getImageAssetById(windowSkinId);
        final Texture windowSkinTexture = game.localAssetManager.get(windowImageAsset.path, Texture.class);

        this.background = new TextureRegion(windowSkinTexture, 0f, 0f, 0.5f, 0.5f);
        this.texture = new TextureRegion(windowSkinTexture, 0f, 0.5f, 0.5f, 1f);
        this.border = Window.buildBorder(windowSkinTexture);
    }

    private static NinePatch buildBorder(Texture windowSkinTexture) {
        final TextureRegion borderRegion = new TextureRegion(windowSkinTexture, 0.5f, 0f, 1f, 0.5f);
        final int centerWidth = borderRegion.getRegionWidth() - (BORDER_SIZE * 2);
        final int centerHeight = borderRegion.getRegionHeight() - BORDER_SIZE * 2;
        final int rightX = borderRegion.getRegionWidth() - BORDER_SIZE;
        final int bottomY = borderRegion.getRegionHeight() - BORDER_SIZE;

        final TextureRegion[] borderPatches = new TextureRegion[9];

        borderPatches[NinePatch.TOP_LEFT] = new TextureRegion(borderRegion,
                0, 0,
                BORDER_SIZE, BORDER_SIZE);

        borderPatches[NinePatch.TOP_CENTER] = new TextureRegion(borderRegion,
                BORDER_SIZE, 0,
                centerWidth, BORDER_SIZE);

        borderPatches[NinePatch.TOP_RIGHT] = new TextureRegion(borderRegion,
                rightX, 0,
                BORDER_SIZE, BORDER_SIZE);

        borderPatches[NinePatch.MIDDLE_LEFT] = new TextureRegion(borderRegion,
                0, BORDER_SIZE,
                BORDER_SIZE, centerHeight);

        // Since this is just a border, the center is used by a different part of the ui skin and should be excluded
        // from the nine patch
        borderPatches[NinePatch.MIDDLE_CENTER] = null;

        borderPatches[NinePatch.MIDDLE_RIGHT] = new TextureRegion(borderRegion,
                rightX, BORDER_SIZE,
                BORDER_SIZE, centerHeight);

        borderPatches[NinePatch.BOTTOM_LEFT] = new TextureRegion(borderRegion,
                0, bottomY,
                BORDER_SIZE, BORDER_SIZE);

        borderPatches[NinePatch.BOTTOM_CENTER] = new TextureRegion(borderRegion,
                BORDER_SIZE, bottomY,
                centerWidth, BORDER_SIZE);

        borderPatches[NinePatch.BOTTOM_RIGHT] = new TextureRegion(borderRegion,
                rightX, bottomY,
                BORDER_SIZE, BORDER_SIZE);

        return new NinePatch(borderPatches);
    }

    public static Window createWindow(TCGRPGGame game) {
        return new Window(game);
    }

    public void draw(SpriteBatch spriteBatch, float delta) {
        final float bgX = this.getX() + BORDER_SIZE * 0.5f;
        final float bgY = this.getY() + BORDER_SIZE * 0.5f;
        final float bgWidth = this.getWidth() - BORDER_SIZE;
        final float bgHeight = this.getHeight() - BORDER_SIZE;
        spriteBatch.draw(this.background, bgX, bgY, bgWidth, bgHeight);
        spriteBatch.draw(this.texture, bgX, bgY, bgWidth, bgHeight);
        this.border.draw(spriteBatch, this.getX(), this.getY(), this.getWidth(), this.getHeight());

    }

}
