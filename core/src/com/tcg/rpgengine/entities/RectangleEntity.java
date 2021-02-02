package com.tcg.rpgengine.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class RectangleEntity {

    private final Rectangle bounds;

    public RectangleEntity() {
        this.bounds = new Rectangle();
    }

    public RectangleEntity(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public float getX() {
        return this.bounds.x;
    }

    public void setX(float x) {
        this.bounds.x = x;
    }

    public void setCenterX(float x) {
        this.bounds.x = x - (this.bounds.width * 0.5f);
    }

    public float getY() {
        return this.bounds.y;
    }

    public void setY(float y) {
        this.bounds.y = y;
    }

    public void setCenterY(float y) {
        this.bounds.y = y - (this.bounds.y * 0.5f);
    }

    public Vector2 getPosition() {
        return new Vector2(this.bounds.x, this.bounds.y);
    }

    public void setPosition(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public void setPosition(Vector2 position) {
        this.setPosition(position.x, position.y);
    }

    public void setCenter(float x, float y) {
        this.setCenterX(x);
        this.setCenterY(y);
    }

    public void setCenter(Vector2 center) {
        this.setCenter(center.x, center.y);
    }

    public float getWidth() {
        return this.bounds.width;
    }

    public void setWidth(float width) {
        this.bounds.width = width;
    }

    public float getHeight() {
        return this.bounds.height;
    }

    public void setHeight(float height) {
        this.bounds.height = height;
    }

    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setSize(Vector2 size) {
        this.setSize(size.x, size.y);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = this == obj;
        if (!equals) {
            if (obj == null || this.getClass() != obj.getClass()) {
                equals = false;
            } else {
                final RectangleEntity other = (RectangleEntity) obj;
                equals = this.bounds.equals(other.bounds);
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bounds);
    }
}
