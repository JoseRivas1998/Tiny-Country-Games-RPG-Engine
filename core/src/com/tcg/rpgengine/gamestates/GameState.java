package com.tcg.rpgengine.gamestates;

import com.badlogic.gdx.utils.Disposable;

public interface GameState extends Disposable {

    void create();
    void handleInput(float deltaTime);
    void update(float deltaTime);
    void draw(float deltaTime);
    void resize(int width, int height);
    void pause();
    void resume();

}
