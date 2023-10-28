package com.dodgeball.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dodgeball.game.DodgeballGame;
import com.github.czyzby.websocket.GwtWebSockets;
import com.badlogic.gdx.Gdx;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {

                //Gdx.app.setLogLevel(LOG_INFO);
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                //return new GwtApplicationConfiguration(800, 480);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                GwtWebSockets.initiate();
                return new DodgeballGame();
        }
}