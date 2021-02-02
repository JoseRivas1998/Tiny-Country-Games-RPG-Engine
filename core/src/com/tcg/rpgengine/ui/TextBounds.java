package com.tcg.rpgengine.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.Objects;

public class TextBounds {

    private final BitmapFont font;
    private final GlyphLayout gl;
    private final Rectangle bounds;

    private String text;
    private float targetWidth;
    private int halign;
    private boolean wrap;

    public TextBounds(BitmapFont font, String text, float targetWidth, int halign, boolean wrap) {
        this.font = Objects.requireNonNull(font);
        this.gl = new GlyphLayout();
        this.targetWidth = targetWidth;
        this.halign = halign;
        this.wrap = wrap;
        this.bounds = new Rectangle();
        this.setText(text);
    }

    public TextBounds(BitmapFont font, String text) {
        this(font, text, 0, Align.left, false);
    }

    public void setText(String text) {
        this.text = Objects.requireNonNull(text);
        this.updateGlyphLayout();
    }

    public void setTargetWidth(float targetWidth) {
        this.targetWidth = targetWidth;
        this.updateGlyphLayout();
    }

    public void setHalign(int halign) {
        this.halign = halign;
        this.updateGlyphLayout();
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
        this.updateGlyphLayout();
    }

    private void updateGlyphLayout() {
        this.gl.setText(this.font, this.text, this.font.getColor(), this.targetWidth, this.halign, this.wrap);
        this.bounds.setSize(this.gl.width, this.gl.height - this.font.getDescent());
    }

    public float getX() {
        return this.bounds.getX();
    }

    public void setX(float x) {
        this.bounds.setX(x);
    }

    public float getY() {
        return this.bounds.getY();
    }

    public void setY(float y) {
        this.bounds.setY(y);
    }

    public Vector2 getPosition(Vector2 position) {
        return this.bounds.getPosition(position);
    }

    public Rectangle setPosition(Vector2 position) {
        return this.bounds.setPosition(position);
    }

    public Rectangle setPosition(float x, float y) {
        return this.bounds.setPosition(x, y);
    }

    public float getWidth() {
        return this.bounds.getWidth();
    }

    public float getTargetWidth() {
        return this.targetWidth;
    }

    public float getHeight() {
        return this.bounds.getHeight();
    }

    public Rectangle getBounds() {
        return new Rectangle(this.bounds);
    }

    public void draw(Batch batch) {
        if (Float.compare(this.targetWidth, 0) == 0) {
            this.font.draw(batch, this.text, this.bounds.x, this.bounds.y);
        } else {
            this.font.draw(batch, this.text, this.bounds.x, this.bounds.y, this.targetWidth, this.halign, this.wrap);
        }
    }

}
