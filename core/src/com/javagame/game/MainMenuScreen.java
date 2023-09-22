package com.javagame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

	final JavaGame game;

	OrthographicCamera camera;

	public MainMenuScreen(final JavaGame game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	}

    @Override
	public void render(float delta) {
		ScreenUtils.clear(1, 1, 1f, 1);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.setColor(0,0,0,1);
		game.font.draw(game.batch, "Open Dodgeball", 250, 300);
		game.font.draw(game.batch, "Tap anywhere to begin", 250, 200);
		
		game.batch.end();

		if (Gdx.input.isTouched()) {
			game.font.setColor(1,1,1,1);
			game.setScreen(new GameScreen(game));
			dispose();
		}
	}
	@Override
	public void resize(int width, int height) {
	}
	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}