package com.dodgeball.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DodgeballGame extends Game {
	//vars
	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont introfont;
	public String myroom;

	public String launcher = "";

	//for desktop
	//public DodgeballGame(String gamelauncher){
	//	launcher = gamelauncher;
	//}
	public void create() {
		//creates essential variables; fonts, sprite canvas
		batch = new SpriteBatch();
		introfont = new BitmapFont(Gdx.files.internal("Minecraft.fnt"),Gdx.files.internal("Minecraft.png"), false);
		introfont.setColor(1,1,1,1);
		introfont.getData().setScale(.4f,.4f);
		font = new BitmapFont(Gdx.files.internal("GameFont.fnt"),Gdx.files.internal("GameFont.png"), false);
		font.setColor(1,1,1,1);
		font.getData().setScale(.4f,.4f);
		this.setScreen(new MainMenuScreen(this,"html"));
	}

	//main renderer
	public void render() {
		super.render();
	}

	//trashing objects
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}