package com.dodgeball.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.czyzby.websocket.*;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSockets;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.czyzby.websocket.serialization.Serializer;

import java.net.Socket;
import java.net.SocketAddress;
public class DodgeballGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont introfont;
	public String myroom;

	public void create() {
		batch = new SpriteBatch();
		introfont = new BitmapFont(Gdx.files.internal("Minecraft.fnt"),Gdx.files.internal("Minecraft.png"), false);
		introfont.setColor(1,1,1,1);
		introfont.getData().setScale(.4f,.4f);
		font = new BitmapFont(Gdx.files.internal("GameFont.fnt"),Gdx.files.internal("GameFont.png"), false);
		font.setColor(1,1,1,1);
		font.getData().setScale(.4f,.4f);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}