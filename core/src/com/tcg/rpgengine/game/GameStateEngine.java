package com.tcg.rpgengine.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.tcg.rpgengine.gamestates.GameState;

import java.util.Objects;
import java.util.Stack;

public class GameStateEngine implements Disposable {

    private final Stack<GameState> stateStack;
    private GameState currentState;

    public GameStateEngine() {
        this.stateStack = new Stack<>();
    }

    public void setState(GameState newState) {
        Objects.requireNonNull(newState);
        if (this.currentState != null) this.currentState.dispose();
        this.currentState = newState;
        newState.create();
        newState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void pushState(GameState newState) {
        Objects.requireNonNull(newState);
        if (this.currentState != null) {
            this.currentState.pause();
            this.stateStack.push(this.currentState);
            this.currentState = null;
            this.setState(newState);
        }
    }

    public void popState() {
        if (!this.stateStack.empty()) {
            if (this.currentState != null) this.currentState.dispose();
            this.currentState = this.stateStack.pop();
            this.currentState.resume();
            this.currentState.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void step(float delta) {
        if (this.currentState != null) {
            this.currentState.handleInput(delta);
            this.currentState.update(delta);
            this.currentState.draw(delta);
        }
    }

    public void resize(int width, int height) {
        if (this.currentState != null) this.currentState.resize(width, height);
    }

    public void resume() {
        if (this.currentState != null) this.currentState.resume();
    }

    public void pause() {
        if (this.currentState != null) this.currentState.pause();
    }

    @Override
    public void dispose() {
        if (this.currentState != null) this.currentState.dispose();
        while (!this.stateStack.empty()) {
            this.stateStack.pop().dispose();
        }
        this.stateStack.clear();
    }


}
