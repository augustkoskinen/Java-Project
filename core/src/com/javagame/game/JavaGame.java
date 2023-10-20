package com.javagame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.Gdx;

public class JavaGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	FreeTypeFontGenerator generator;
	FreeTypeFontParameter parameter;
	public String myroom;

	public void create() {
		batch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = 30;
		font = generator.generateFont(parameter);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
		generator.dispose();
	}
	//.overlaps() 
}