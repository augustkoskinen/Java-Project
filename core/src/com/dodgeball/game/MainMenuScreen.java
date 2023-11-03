package com.dodgeball.game;

//import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;

//import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
	//vars
	final DodgeballGame game;
	private Texture introtext;
	private Texture button1v1;
	private Texture buttononline;
	private Texture controllerpic;
	private Texture rulebutton;
	private boolean touchingbutton;

	OrthographicCamera camera;
	String launcher = "html";

	public MainMenuScreen(final DodgeballGame game,String gamelauncher) {
		//creates camera, textures for the title screen, and sets the screen
		this.game = game;
		//launcher = gamelauncher;
		introtext = new Texture("title.png");
		button1v1 = new Texture("1v1button.png");
		buttononline = new Texture("onlinebutton.png");
		controllerpic = new Texture("controllers.png");
		rulebutton = new Texture("rules.png");
		game.setScreen(new GameScreen(game));

		camera = new OrthographicCamera();
		//int aspectratio = 2500/Gdx.graphics.getWidth();
		camera.setToOrtho(false, 800/*aspectratio*/, 480/*aspectratio*/);
	}

	@Override
	//renders images
	public void render(float delta) {
		//clears screen
		ScreenUtils.clear(1, 1, 1f, 1);

		if (MovementMath.pointDis(new Vector3(748f*(Gdx.graphics.getWidth()/800f),432*(Gdx.graphics.getHeight()/480f),0f),new Vector3(Gdx.input.getX(),Gdx.input.getY(),0f))<48f*(Gdx.graphics.getWidth()/800f)){
			touchingbutton = true;
		} else {
			touchingbutton = false;
		}

		//draws things
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(introtext, 0, 0, 800, 480);
		game.batch.draw(button1v1, 152, 128, 96, 96);
		game.batch.draw(buttononline, 552, 128, 96, 96);
		game.batch.draw(rulebutton, 700, 4f, 96, 96);
		if(touchingbutton) {
			game.batch.draw(controllerpic, 556, 4, 240, 192);
		}
		game.batch.end();

		//senses if starting a 1v1 game or online game
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

	// necessary overrides
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