package com.javagame.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class JavaGame implements ApplicationListener {
	static final int WORLD_WIDTH = 200;
	static final int WORLD_HEIGHT = 200;
	static final float SPEED = 1f;

	private OrthographicCamera cam;
	private SpriteBatch batch;

	private Sprite playerSprite;
	public Vector3 playerpos = new Vector3(0f, 0f,0);

	@Override
	public void create() {
		playerSprite = new Sprite(new Texture(Gdx.files.internal("player.png")));
		playerSprite.setScale(.2f, .2f);
		playerSprite.setPosition(playerpos.x, playerpos.y);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		cam = new OrthographicCamera(30, 30 * (h / w));
		cam.position.set(playerpos.x,playerpos.y, 0);
		cam.update();

		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		handleInput();
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		playerSprite.draw(batch);
		batch.end();
	}

	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			playerpos.x += SPEED;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			playerpos.x += -SPEED;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			playerpos.y += SPEED;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			playerpos.y += -SPEED;
		}

		playerSprite.setPosition(playerpos.x, playerpos.y);
		//cam.position.set(playerpos.x,playerpos.y, 0);
		//cam.position.x = MathUtils.clamp(playerpos.x, cam.viewportWidth / 2f, WORLD_WIDTH - cam.viewportWidth / 2f);
		//cam.position.y = MathUtils.clamp(playerpos.y, cam.viewportHeight / 2f, WORLD_HEIGHT - cam.viewportHeight / 2f);
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = 30f;
		cam.viewportHeight = 30f * height/width;
		cam.update();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		playerSprite.getTexture().dispose();
		batch.dispose();
	}

	@Override
	public void pause() {
	}
}