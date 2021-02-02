package com.tcg.rpgengine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.common.data.assets.ImageAsset;
import org.graalvm.compiler.loop.MathUtil;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class Menu<I> extends Window {

    private static final float INITIAL_PADDING = 5f;
    private static final float INITIAL_MARGIN = 25f;
    private final TextureRegion highlightTexture;
    private Function<I, String> menuItemToString = Object::toString;
    private float textPadding;
    private float margin;
    private BitmapFont font;
    private final Array<I> menuItems;
    private final Array<TextBounds> textBounds;
    private final Array<Boolean> disabled;
    private int selectedIndex;
    private boolean needsUpdate;
    private float stateTime;

    private Menu(TCGRPGGame game, BitmapFont font,  Iterable<? extends I> initialItems) {
        super(game);
        this.selectedIndex = -1;
        this.font = font;
        this.menuItems = new Array<>();
        this.textBounds = new Array<>();
        this.disabled = new Array<>();
        initialItems.forEach(this::addItem);
        this.textPadding = INITIAL_PADDING;
        this.margin = INITIAL_MARGIN;
        this.update();

        this.highlightTexture = this.buildHighlightTexture(game);
        this.stateTime = 0;

    }

    private TextureRegion buildHighlightTexture(TCGRPGGame game) {
        final UUID windowSkinId = game.systemData.windowSkin.getWindowSkinId();
        final ImageAsset windowSkinAsset = game.assetLibrary.getImageAssetById(windowSkinId);
        final Texture windowTexture = game.localAssetManager.get(windowSkinAsset.path, Texture.class);
        return new TextureRegion(windowTexture, 0.5f, 0.5f, 0.75f, 0.75f);
    }

    public static <I> Menu<I> newMenu(TCGRPGGame game, BitmapFont font, Iterable<I> initialItems) {
        return new Menu<>(game, font, initialItems);
    }

    @SafeVarargs
    public static <I> Menu<I> newMenu(TCGRPGGame game, BitmapFont font, I... initialItems) {
        return new Menu<>(game, font, new Array<>(initialItems));
    }

    public void addItem(I menuItem) {
        this.menuItems.add(menuItem);
        this.textBounds.add(this.textBoundsFromMenuItem(menuItem));
        this.disabled.add(false);
        if (this.selectedIndex == -1) {
            this.selectedIndex = 0;
        }
        this.needsUpdate = true;
    }

    public void insertItem(I menuItem, int index) {
        this.menuItems.insert(index, menuItem);
        this.textBounds.insert(index, this.textBoundsFromMenuItem(menuItem));
        this.disabled.insert(index, false);
        this.selectedIndex = this.selectedIndex >= index ? this.selectedIndex + 1 : this.selectedIndex;
        this.needsUpdate = true;
    }

    private TextBounds textBoundsFromMenuItem(I menuItem) {
        return new TextBounds(this.font, this.menuItemToString.apply(menuItem), 0f, Align.left, true);
    }

    public void setDisabled(I menuItem, boolean disabled) {
        final int itemIndex = this.menuItems.indexOf(menuItem, false);
        if (itemIndex >= 0) {
            this.disabled.set(itemIndex, disabled);
        }
    }

    public void setFont(BitmapFont font) {
        this.font = font;
        this.needsUpdate = true;
    }

    public void setMenuItemToString(Function<I, String> menuItemToString) {
        this.menuItemToString = menuItemToString;
        this.needsUpdate = true;
    }

    public void setTextPadding(float textPadding) {
        this.textPadding = textPadding;
        this.needsUpdate = true;
    }

    public void setMargin(float margin) {
        this.margin = margin;
        this.needsUpdate = true;
    }

    public void previous() {
        if (this.menuItems.notEmpty()) {
            this.selectedIndex = (this.selectedIndex + this.menuItems.size - 1) % this.menuItems.size;
        }
    }

    public void next() {
        if (this.menuItems.notEmpty()) {
            this.selectedIndex = (this.selectedIndex + 1) % this.menuItems.size;
        }
    }

    public Optional<I> getSelectedItem() {
        if (this.selectedIndex == -1 || this.disabled.get(this.selectedIndex)) {
            return Optional.empty();
        }
        return Optional.of(this.menuItems.get(this.selectedIndex));
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        this.needsUpdate = true;
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        if (this.needsUpdate) this.update();
        super.draw(spriteBatch, delta);
        this.stateTime += delta;
        if (this.selectedIndex >= 0) {
            float alpha = 0.25f * MathUtils.cos(2f * MathUtils.PI2 * this.stateTime) + 0.75f;
            float selectedY = this.getY() + this.margin;
            for (int i = this.menuItems.size - 1; i > this.selectedIndex; i--) {
                selectedY += this.textBounds.get(i).getHeight() + (this.textPadding * 2);
            }
            final Color originalColor = new Color(spriteBatch.getColor());
            spriteBatch.setColor(1f, 1f, 1f, alpha);
            spriteBatch.draw(this.highlightTexture, this.getX() + this.margin, selectedY, this.getWidth() - (this.margin * 2), this.textBounds.get(this.selectedIndex).getHeight() + (this.textPadding * 2));
            spriteBatch.setColor(originalColor);
        }
        float y = this.getY() + this.margin;
        for (int i = this.menuItems.size - 1; i >= 0; i--) {
            final TextBounds textBounds = this.textBounds.get(i);
            textBounds.setX(this.getX() + this.margin + this.textPadding);
            textBounds.setY(y + this.textPadding + textBounds.getHeight());
            y += this.textPadding * 2 + textBounds.getHeight();
            if (this.disabled.get(i)) {
                final Color originalColor = new Color(this.font.getColor());
                this.font.setColor(Color.GRAY);
                textBounds.draw(spriteBatch);
                this.font.setColor(originalColor);
            } else {
                textBounds.draw(spriteBatch);
            }
        }
    }

    public void update() {
        final float targetWidth = this.getWidth() - (this.margin * 2);
        float height = this.margin;
        for (int i = this.menuItems.size - 1; i >= 0; i--) {
            final I menuItem = this.menuItems.get(i);
            final TextBounds menuText = this.textBounds.get(i);
            menuText.setText(this.menuItemToString.apply(menuItem));
            menuText.setTargetWidth(targetWidth);
            height += menuText.getHeight() + (this.textPadding * 2);
        }
        this.setHeight(height + this.margin);
        this.needsUpdate = false;
    }
}
