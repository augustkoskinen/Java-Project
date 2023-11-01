package com.dodgeball.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.dodgeball.game.DodgeballGame;
import com.github.czyzby.websocket.GwtWebSockets;
import com.badlogic.gdx.Gdx;
import com.google.gwt.user.client.ui.TextArea;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {

                //Gdx.app.setLogLevel(LOG_INFO);
                // Resizable application, uses available space in browser

                //GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                //return new GwtApplicationConfiguration(800, 480);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                GwtWebSockets.initiate();
                return new DodgeballGame("html");
        }
}