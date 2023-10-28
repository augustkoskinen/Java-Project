package com.dodgeball.game;

import com.github.czyzby.websocket.CommonWebSockets;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.dodgeball.game.DodgeballGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(120);
		config.setWindowedMode(800, 480);
		config.useVsync(true);
		config.setTitle("Dodgeball");
		CommonWebSockets.initiate();
		new Lwjgl3Application(new DodgeballGame(), config);
	}
}
