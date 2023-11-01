package com.dodgeball.game;

//import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;

//import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
	final DodgeballGame game;
	private Texture introtext;
	private Texture button1v1;
	private Texture buttononline;

	OrthographicCamera camera;
	String launcher = "";

	public MainMenuScreen(final DodgeballGame game,String gamelauncher) {
		this.game = game;
		launcher = gamelauncher;
		introtext = new Texture("title.png");
		button1v1 = new Texture("1v1button.png");
		buttononline = new Texture("onlinebutton.png");
		game.setScreen(new GameScreen(game));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(1, 1, 1f, 1);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.batch.draw(introtext, 0, 0, 800, 480);
		game.batch.draw(button1v1, 152, 128, 96, 96);
		game.batch.draw(buttononline, 552, 128, 96, 96);
		game.batch.end();

		if (Gdx.input.isTouched()&&MovementMath.pointDis(new Vector3(200f*(Gdx.graphics.getWidth()/800f),304f*(Gdx.graphics.getHeight()/480f),0f),new Vector3(Gdx.input.getX(),Gdx.input.getY(),0f))<48f*(Gdx.graphics.getWidth()/800f)){
			game.setScreen(new GameScreen(game));
			dispose();
		} else if (Gdx.input.isTouched() && MovementMath.pointDis(new Vector3(600f*(Gdx.graphics.getWidth()/800f),304f*(Gdx.graphics.getHeight()/480f),0f),new Vector3(Gdx.input.getX(),Gdx.input.getY(),0f))<48f*(Gdx.graphics.getWidth()/800f)) {
			if(launcher.equals("html"))
				game.setScreen(new GameScreenMulti(game));
			else if(launcher.equals("desktop"))
				game.setScreen(new GameScreenMulti(game));
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
		introtext.dispose();
		button1v1.dispose();
		buttononline.dispose();
	}
}