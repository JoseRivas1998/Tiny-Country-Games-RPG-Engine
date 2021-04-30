package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tcg.rpgengine.TCGRPGGame;
import com.tcg.rpgengine.entities.GameMap;
import com.tcg.rpgengine.input.ControlInput;
import com.tcg.rpgengine.input.Controls;
import com.tcg.rpgengine.utils.GameConstants;

public class MapDemoState implements GameState{

    private static final float FADE_TIME = 1.0f;
    private static final Interpolation FADE_FUNCTION = Interpolation.slowFast;
    private static final float CAM_SPEED = 320f;

    private final TCGRPGGame game;
    private final GameMap gameMap;
    private FitViewport viewport;

    private boolean fadingIn;
    private float fadingInTime;

    public MapDemoState(TCGRPGGame game, GameMap gameMap) {
        this.game = game;
        this.gameMap = gameMap;
    }

    @Override
    public void create() {
        this.viewport = new FitViewport(GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);
        this.fadingIn = true;
        this.fadingInTime = 0;
    }

    @Override
    public void handleInput(float deltaTime) {
        if (!this.fadingIn) {
            if (ControlInput.controlCheck(Controls.MOVE_UP)) {
                this.viewport.getCamera().position.y += CAM_SPEED * deltaTime;
            }
            if (ControlInput.controlCheck(Controls.MOVE_DOWN)) {
                this.viewport.getCamera().position.y -= CAM_SPEED * deltaTime;
            }
            if (ControlInput.controlCheck(Controls.MOVE_RIGHT)) {
                this.viewport.getCamera().position.x += CAM_SPEED * deltaTime;
            }
            if (ControlInput.controlCheck(Controls.MOVE_LEFT)) {
                this.viewport.getCamera().position.x -= CAM_SPEED * deltaTime;
            }
        }

    }

    @Override
    public void update(float deltaTime) {

        if (this.fadingIn) {
            this.fadingInTime += deltaTime;
            if (Float.compare(this.fadingInTime, FADE_TIME) > 0) {
                this.fadingIn = false;
            }
        }

        final float minX = GameConstants.VIEW_WIDTH * 0.5f;
        final float minY = GameConstants.VIEW_HEIGHT * 0.5f;
        final Vector2 mapPixelSize = this.gameMap.getMapPixelSize();
        this.viewport.getCamera().position.x = MathUtils.clamp(this.viewport.getCamera().position.x,
                minX, mapPixelSize.x - minX);
        this.viewport.getCamera().position.y = MathUtils.clamp(this.viewport.getCamera().position.y,
                minY, mapPixelSize.y - minY);
        this.viewport.getCamera().update();

    }

    @Override
    public void draw(float deltaTime) {
        this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
        this.gameMap.draw(this.viewport);
        if (this.fadingIn) {
            final float alpha = Math.min(1f, FADE_FUNCTION.apply(1f, 0f, this.fadingInTime / FADE_TIME));
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            this.game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            this.game.shapeRenderer.setProjectionMatrix(this.viewport.getCamera().combined);
            this.game.shapeRenderer.setColor(new Color(0f, 0f, 0f, alpha));
            this.game.shapeRenderer.rect(0, 0, GameConstants.VIEW_WIDTH, GameConstants.VIEW_HEIGHT);
            this.game.shapeRenderer.end();
            this.game.shapeRenderer.setColor(Color.WHITE);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        this.gameMap.dispose();
    }
}
