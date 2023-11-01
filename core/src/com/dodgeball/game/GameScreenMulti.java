package com.dodgeball.game;

import java.util.*;
import java.io.*;
import java.net.*;
//import com.google.gwt.json.client.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONArray;
import java.lang.String.*;
import java.lang.Integer.*;
import java.lang.Float.*;
import com.google.gwt.json.client.JSONValue;

import com.badlogic.gdx.utils.*;

import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.Input;

import static java.lang.Float.parseFloat;

/*
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.lang.Object;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Iterator;
import com.badlogic.gdx.Input.Keys;
*/

public class GameScreenMulti implements Screen {
	private WebSocket socket;
	private int array;
	private String mystring = "";
	private float mynum = 0;
	private boolean start = false;
	private String mycolor = "red";
	private String myid = "";
	private String otherid = "";
	private boolean createdplayers = false;

	static final int WORLD_WIDTH = 1108;
	static final int WORLD_HEIGHT = 1108;
	static final float SPEED = 200f;
	static final float DASHSPEED = 1500f;
	static final float SPEED2 = 200f;
	static final float DASHSPEED2 = 1500f;
	Random random;
	int ran;
	int rantile;
	int rantilerespawn = -1;
	Texture power0 = new Texture(Gdx.files.internal("power0.png"));

	private Rectangle tilerects[] = new Rectangle[25];
	private boolean tilecol[] = new boolean[25];
	private boolean tilecol2[] = new boolean[25];
	private OrthographicCamera cam;
	private SpriteBatch batch;
	final DodgeballGame game;
	private Texture tiletexture;
	private Array<Power> poweruparray = new Array<Power>();

	// p1
	private Circle player;
	private Texture playerTexture;
	private Sprite playerSprite;
	private float playerrot = 0;
	private Array<Ball> myballs = new Array<Ball>();
	private boolean canreleaseball = false;
	private Texture balltext;
	private float ballsize = 8;
	private Vector3 kb = new Vector3(0, 0, 0);
	private Vector3 dashvel = new Vector3(0, 0, 0);
	private int points = 0;
	private float dashcooldown = 0;
	private int playerpower = -1;
	private float playerpowercooldown = -1;
	private float spawnprot = 20.0f;

	// p2
	private Circle player2;
	private Texture playerTexture2;
	private Sprite playerSprite2;
	private float playerrot2 = 180;
	private Array<Ball> blueballs = new Array<Ball>();
	private Texture balltext2;
	private float ballsize2 = 8;
	private float dashvel2x = 0;
	private float dashvel2y = 0;
	private float kbaddx = 0;
	private float kbaddy = 0;
	private int points2 = 0;
	private float spawnprot2 = 40.0f;
	float xadd2 = 0;
	float yadd2 = 0;
	float moveVectx = 0;
	float moveVecty = 0;

	private Vector3 spawn1 = new Vector3(162, WORLD_HEIGHT / 2, 0);
	private Vector3 spawn2 = new Vector3(WORLD_WIDTH / 2, 162, 0);
	private Vector3 spawn3 = new Vector3(WORLD_WIDTH - 162, WORLD_HEIGHT / 2, 0);
	private Vector3 spawn4 = new Vector3(WORLD_WIDTH / 2, WORLD_HEIGHT - 162, 0);

	public WebSocket configSocket() {
		WebSocket holdsocket = WebSockets.newSocket(WebSockets.toWebSocketUrl("game2.ejenda.org", 80));//game.ejenda.org:80 127.0.0.1:8080
		holdsocket.setSendGracefully(true);
		holdsocket.addListener(new WebSocketListener() {
			@Override
			public boolean onOpen(WebSocket webSocket) {
				Gdx.app.log("Log", "Open");
				return false;
			}

			@Override
			public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
				Gdx.app.log("Log", "Close");
				return false;
			}

			@Override
			public boolean onMessage(WebSocket webSocket, String packet) {
				//Gdx.app.log("Log","hiiii");//good luck charm; dont touch
				JSONObject data = JSONParser.parse(packet).isObject();
				String event = data.get("event").isString().stringValue();
				//mystring = event;
				//Gdx.app.debug("MyTag", "my debug message");
				Gdx.app.log("Log", "Packet Message: " + data);
				if (event.equals("socketID")) {
					myid = data.get("id").isString().stringValue();
					game.myroom = data.get("room").isString().stringValue();
					Gdx.app.log("Websocket", "My ID: " + myid);
				}
				if (event.equals("getPlayers")) {
					otherid = data.get("otherid").isString().stringValue();
					mycolor = data.get("color").isString().stringValue();
					Gdx.app.log("Websocket", "Other ID: " + otherid);
				}
				if (event.equals("disconnecting")) {
					start = false;
					socket.close();
				}
				if (event.equals("startGame")) {
					//other vars
					start = true;
					int seed = (int) data.get("seed").isNumber().doubleValue();
					random = new Random(seed);
					rantile = random.nextInt(25);
				}

				if (event.equals("movement")) {
					if (data.get("id").isString().stringValue().equals(myid)) {
						parseFloat(data.get("x").isString().stringValue());

						float x = parseFloat(data.get("x").isString().stringValue());
						float y = parseFloat(data.get("y").isString().stringValue());
						player2.setPosition(x, y);

						spawnprot2 = parseFloat(data.get("spawnprot2").isString().stringValue());
						moveVectx = parseFloat(data.get("moveVectx").isString().stringValue());
						moveVecty = parseFloat(data.get("moveVecty").isString().stringValue());
						dashvel2x = parseFloat(data.get("dashvelx").isString().stringValue());
						dashvel2y = parseFloat(data.get("dashvely").isString().stringValue());
						ballsize2 = parseFloat(data.get("ballsize").isString().stringValue());
						playerrot2 = parseFloat(data.get("rotation").isString().stringValue());
						kb.x += parseFloat(data.get("kbaddx").isString().stringValue());
						kb.y += parseFloat(data.get("kbaddy").isString().stringValue());
						xadd2 = parseFloat(data.get("xadd2").isString().stringValue());
						yadd2 = parseFloat(data.get("yadd2").isString().stringValue());
					}
					for (int i = 0; i < 25; i++) {
						JSONObject curRect = data.get("jstilerects").isArray().get(i).isObject();
						tilerects[i].width = (float) curRect.get("width").isNumber().doubleValue();
						tilerects[i].height = (float) curRect.get("height").isNumber().doubleValue();
						tilerects[i].setPosition((float)(curRect.get("x").isNumber().doubleValue()), (float)(curRect.get("y").isNumber().doubleValue()));
					}
				}
				if (event.equals("shootBullet")) {
					parseFloat(data.get("ballsize").isString().stringValue());
					if (data.get("id").isString().stringValue().equals(otherid)) {
						int ballamount = (int) parseFloat(data.get("ballcount").isString().stringValue());
						float ballsize = parseFloat(data.get("ballsize").isString().stringValue());
						int mult = 0;
						for (int i = 0; i < ballamount; i++) {
							if (i == 1) {
								mult = 1;
							} else if (i == 2) {
								mult = -1;
							}
							Ball ball = new Ball();
							ball.rotation = playerrot2;
							ball.changeColor(data.get("color").isString().stringValue());
							Vector3 ballpos = MovementMath.lengthDir(playerrot2 + 0.349066f * mult, 40f);
							Vector3 ballvel = MovementMath.lengthDir(playerrot2 + 0.349066f * mult, ballsize * 80 + 50f);
							ball.circ.setPosition(player2.x + ballpos.x + 28, player2.y + ballpos.y + 28);
							ball.velocity = ballvel;
							ball.ballsprite.setRotation(playerrot2 + 0.349066f * mult);
							ball.ballsprite.setScale(ballsize / 8);
							ball.circ.radius = ballsize / 2;
							blueballs.add(ball);
						}
					}
				}
				if (event.equals("updatePoints")) {
					parseFloat(data.get("ran").isString().stringValue());
					points++;
					ballsize = 8;
					ballsize2 = 8;
					ran = (int) parseFloat(data.get("ran").isString().stringValue());
					kb = new Vector3(0, 0, 0);
					spawnprot2 = 40.0f;
					for (int i = 0; i < tilecol.length; i++) {
						tilerects[i].setWidth(196);
						tilerects[i].setHeight(196);
						tilerects[i].setPosition((((i % 5) * 196) + 64), (float) (Math.floor(i / 5f) * 196 + 64));
						rantile = (int) parseFloat(data.get("rantile").isString().stringValue());
						rantilerespawn = -1;
					}
					tilecol[0] = true;
					tilecol2[0] = true;
					player2.setPosition(WORLD_WIDTH / 2 - 32, WORLD_HEIGHT / 2 - 32);
					//mystring = "here";
				}
				if (event.equals("makePower")) {
					Power power = new Power();
					//power.type = (int) (Math.random() * 5);
					switch ((int) data.get("randpos").isNumber().doubleValue()) {
						case 0: {
							power.assignCircleValues(24, new Vector3(spawn1.x - 32, spawn1.y - 32, 0));
							break;
						}
						case 1: {
							power.assignCircleValues(24, new Vector3(spawn2.x - 32, spawn2.y - 32, 0));
							break;
						}
						case 2: {
							power.assignCircleValues(24, new Vector3(spawn3.x - 32, spawn3.y - 32, 0));
							break;
						}
						case 3: {
							power.assignCircleValues(24, new Vector3(spawn4.x - 32, spawn4.y - 32, 0));
							break;
						}
					}
					power.asignType(0);
					poweruparray.add(power);
				}
				return false;
			}

			@Override
			public boolean onMessage(WebSocket webSocket, byte[] packet) {
				Gdx.app.log("Log", "Byte Message: ");
				return false;
			}

			@Override
			public boolean onError(WebSocket webSocket, Throwable error) {
				Gdx.app.log("Log", "Error");
				return false;
			}
		});

		holdsocket.connect();

		return holdsocket;
	}


	public GameScreenMulti(final DodgeballGame game) {
		//socket code
		this.game = game;
		socket = configSocket();
		for (int i = 0; i < tilecol.length; i++) {
			tilerects[i] = new Rectangle();
			tilerects[i].setWidth(196);
			tilerects[i].setHeight(196);
			tilerects[i].setPosition((((i % 5) * 196) + 64), (float) (Math.floor(i / 5f) * 196 + 64));
			tilecol[i] = false;
			tilecol2[i] = false;
		}

		batch = new SpriteBatch();
		player = new Circle();
		player2 = new Circle();
		balltext = new Texture(Gdx.files.internal("redball.png"));
		balltext2 = new Texture(Gdx.files.internal("blueball.png"));
		tiletexture = new Texture("Tiles.png");
	}

	private void handleInput() {
		// p1
		Vector3 moveVect = MovementMath.InputDir(0);
		Vector3 moveMag = new Vector3(0, 0, 0);
		float xadd = 0;
		float yadd = 0;
		if (moveVect.x != 0 || moveVect.y != 0) {
			playerrot = MovementMath.pointDir(new Vector3(0, 0, 0), moveVect);
			moveMag = MovementMath.lengthDir(playerrot, 1);
			xadd = moveMag.x * SPEED * Gdx.graphics.getDeltaTime();
			yadd = moveMag.y * SPEED * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.G) && dashcooldown <= 0 && (moveVect.x != 0 || moveVect.y != 0)) {
			dashvel = new Vector3(moveMag.x * DASHSPEED, moveMag.y * DASHSPEED, 0);
			dashcooldown = 20;
		}

		kb.x *= 0.8f;
		kb.y *= 0.8f;
		kbaddx *= 0.5f;
		kbaddy *= 0.5f;
		dashvel.x *= 0.8f;
		dashvel.y *= 0.8f;

		player.x += xadd + kb.x * Gdx.graphics.getDeltaTime() + dashvel.x * Gdx.graphics.getDeltaTime();
		player.y += yadd + kb.y * Gdx.graphics.getDeltaTime() + dashvel.y * Gdx.graphics.getDeltaTime();

		playerSprite.setPosition(player.x, player.y);
		if (moveVect.x != 0 || moveVect.y != 0) {
			playerSprite.setRotation((float) Math.toDegrees((float) playerrot));
		}
		playerSprite2.setPosition(player2.x, player2.y);
		playerSprite2.setRotation((float) Math.toDegrees((float) playerrot2));
		if (!Gdx.input.isKeyPressed(Input.Keys.F) && canreleaseball) {
			int ballamount = 0;
			if (playerpower == 0)
				ballamount = 3;
			else
				ballamount = 1;
			int mult = 0;
			for (int i = 0; i < ballamount; i++) {
				if (i == 1) {
					mult = 1;
				} else if (i == 2) {
					mult = -1;
				}
				Ball ball = new Ball();
				ball.rotation = playerrot;
				ball.changeColor(mycolor);
				Vector3 ballpos = MovementMath.lengthDir(playerrot + 0.349066f * mult, 40f);
				Vector3 ballvel = MovementMath.lengthDir(playerrot + 0.349066f * mult, ballsize * 80 + 50f);
				ball.circ.setPosition(player.x + ballpos.x + 28, player.y + ballpos.y + 28);
				ball.velocity = ballvel;
				ball.ballsprite.setRotation(playerrot + 0.349066f * mult);
				ball.ballsprite.setScale(ballsize / 8);
				ball.circ.radius = ballsize / 2;
				myballs.add(ball);
			}

			JSONObject data = new JSONObject();
			data.put("event", new JSONString("shootmyball"));
			data.put("id", new JSONString(myid));
			data.put("otherid", new JSONString(otherid));
			data.put("ballcount", new JSONString(ballamount + ""));
			data.put("ballsize", new JSONString(ballsize + ""));
			data.put("color", new JSONString(mycolor));
			data.put("room", new JSONString(game.myroom));
			socket.send(JsonUtils.stringify(data.getJavaScriptObject()));

			canreleaseball = false;
			spawnprot = -1;
			ballsize = 8;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.F) && !canreleaseball) {
			canreleaseball = true;
		}
		// bumping
		if (MovementMath.overlaps(player, player2) && (Math.abs(dashvel.x) + Math.abs(dashvel.y) + Math.abs(dashvel2x) + Math.abs(dashvel2y) < 50)) {
			Vector3 bumpvel1 = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(xadd, yadd, 0)), 200f);
			Vector3 bumpvel2 = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(xadd2, yadd2, 0)), 200f);
			Vector3 bumpvel1flip = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(-xadd, -yadd, 0)), 200f);
			Vector3 bumpvel2flip = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(-xadd2, -yadd2, 0)), 200f);
			int p1canmove = 1;
			if (moveVect.x == 0 && moveVect.y == 0) {
				p1canmove = 0;
			}
			int p2canmove = 1;
			if (moveVectx == 0 && moveVecty == 0) {
				p2canmove = 0;
			}
			if (spawnprot <= 0) {
				kb.x += p1canmove * bumpvel1flip.x + p2canmove * bumpvel2.x;
				kb.y += p1canmove * bumpvel1flip.y + p2canmove * bumpvel2.y;
			}
			if (spawnprot2 <= 0) {
				kbaddx = p1canmove * bumpvel1.x + p2canmove * bumpvel2flip.x;
				kbaddy = p1canmove * bumpvel1.y + p2canmove * bumpvel2flip.y;
			}
		}


		//socket movement
		//new Timer().scheduleAtFixedRate(new TimerTask(){
		//@Override
		//public void run(){
		JSONObject data = new JSONObject();
		data.put("event", new JSONString("playermove"));
		data.put("x", new JSONString(player.x + ""));
		data.put("y", new JSONString(player.y + ""));
		data.put("rotation", new JSONString(playerrot + ""));
		data.put("xadd2", new JSONString(xadd2 + ""));
		data.put("yadd2", new JSONString(yadd2 + ""));
		data.put("moveVectx", new JSONString(moveVectx + ""));
		data.put("moveVecty", new JSONString(moveVecty + ""));
		data.put("kbaddx", new JSONString(kbaddx + ""));
		data.put("kbaddy", new JSONString(kbaddy + ""));
		data.put("dashvelx", new JSONString(dashvel.x + ""));
		data.put("dashvely", new JSONString(dashvel.y + ""));
		data.put("spawnprot", new JSONString(spawnprot + ""));
		data.put("id", new JSONString(myid));
		data.put("otherid", new JSONString(otherid));
		if (canreleaseball)
			data.put("ballsize", new JSONString(ballsize + ""));
		else
			data.put("ballsize", new JSONString((-1f) + ""));
		//data.add("mytime", Gdx.graphics.getDeltaTime());
		data.put("room", new JSONString(game.myroom));
		//mystring = myid;
		//data.put("event",new JSONString("updateTiles"));
		socket.send(JsonUtils.stringify(data.getJavaScriptObject()));
		//}
		//},(long)100,(long)1000);
		//new Timer().scheduleAtFixedRate(new TimerTask(){
		//@Override
		//public void run(){

		//Array<String> data = new Array<>();
		/*
		JSONObject data2 = new JSONObject();
		data2.put("event",new JSONString("updateTiles"));
		data2.put("room",new JSONString(game.myroom));
		data2.put("id", new JSONString(myid));
		data2.put("otherid", new JSONString(otherid));
		socket.send(JsonUtils.stringify(data2.getJavaScriptObject()));
		*/
		//}
		//},(long)100,(long)1000);


		// cam
		float camdis = MovementMath.pointDis(cam.position, new Vector3(player.x + player.radius, player.y + player.radius, 0));
		float camdir = MovementMath.pointDir(cam.position, new Vector3(player.x + player.radius, player.y + player.radius, 0));
		Vector3 campos = MovementMath.lengthDir(camdir, camdis);
		cam.position.set(cam.position.x + campos.x * .05f, cam.position.y + campos.y * .05f, 0);
		cam.zoom = 1.5f;
	}

	@Override
	public void render(float delta) {

		ScreenUtils.clear(0, 0, 0, 1);
		if (start) {
			if (!createdplayers) {
				createdplayers = true;
				if (mycolor.equals("red")) {
					player.setPosition(spawn1.x - 32, spawn1.y - 32);
					playerTexture = new Texture(Gdx.files.internal("redplayer.png"));
					playerTexture2 = new Texture(Gdx.files.internal("blueplayer.png"));
				} else if (mycolor.equals("blue")) {
					player.setPosition(spawn3.x - 32, spawn3.y - 32);
					playerTexture = new Texture(Gdx.files.internal("blueplayer.png"));
					playerTexture2 = new Texture(Gdx.files.internal("redplayer.png"));
				}
				// p1 setup
				playerSprite = new Sprite(playerTexture, 0, 0, 64, 64);
				player.radius = playerSprite.getWidth() / 2;
				playerSprite.setRotation(playerrot);

				// p2 setup
				playerSprite2 = new Sprite(playerTexture2, 0, 0, 64, 64);
				player2.radius = playerSprite2.getWidth() / 2;
				playerSprite2.setRotation(playerrot2);

				// cam setup
				cam = new OrthographicCamera();
				cam.setToOrtho(false, 704, 448);
				cam.position.set(player.x + player.radius, player.y + player.radius, 0);
			}
			handleInput();

			cam.update();
			if (playerpowercooldown != -1) {
				playerpowercooldown -= Gdx.graphics.getDeltaTime() * 10;
				if (playerpowercooldown <= -1) {
					playerpower = -1;
					playerpowercooldown = -1;
				}
			}

			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			for (int i = 0; i < tilerects.length; i++) {
				batch.draw(tiletexture, tilerects[i].x, tilerects[i].y, tilerects[i].width, tilerects[i].height);
				tilecol[i] = false;
				tilecol2[i] = false;
				if (MovementMath.overlaps(player, tilerects[i])) {
					tilecol[i] = true;
				}
				if (MovementMath.overlaps(player2, tilerects[i])) {
					tilecol2[i] = true;
				}
			}
			for (Iterator<Power> iter = poweruparray.iterator(); iter.hasNext(); ) {
				Power powerhit = iter.next();
				powerhit.powersprite.setPosition(powerhit.hitbox.x, powerhit.hitbox.y);
				if (MovementMath.overlaps(player, powerhit.hitbox)) {
					playerpower = powerhit.type;
					playerpowercooldown = powerhit.getCooldown(playerpower);
					iter.remove();
				} else if (MovementMath.overlaps(player2, powerhit.hitbox)) {
					iter.remove();
				} else {
					powerhit.powersprite.draw(batch);
				}
			}
			for (Iterator<Ball> iter = blueballs.iterator(); iter.hasNext(); ) {
				Ball curball = iter.next();
				Vector3 pastcirc = new Vector3(curball.circ.x, curball.circ.y, 0);
				curball.circ.x += curball.velocity.x * Gdx.graphics.getDeltaTime();
				curball.circ.y += curball.velocity.y * Gdx.graphics.getDeltaTime();
				curball.ballsprite.setPosition(curball.circ.x, curball.circ.y);
				if (curball.circ.x < -10 && curball.circ.y < -10 && curball.circ.x > 1120 && curball.circ.x > 1120) {
					iter.remove();
				} else if (MovementMath.lineCol(pastcirc, new Vector3(curball.circ.x, curball.circ.y, 0), player) || MovementMath.overlaps(player, curball.circ)) {
					if (spawnprot <= 0) {
						kb.x += curball.velocity.x * 1.25f;
						kb.y += curball.velocity.y * 1.25f;
					}
					iter.remove();
				} else {
					curball.ballsprite.draw(batch);
				}
			}
			for (Iterator<Ball> iter = myballs.iterator(); iter.hasNext(); ) {
				Ball curball = iter.next();
				Vector3 pastcirc = new Vector3(curball.circ.x, curball.circ.y, 0);
				curball.circ.x += curball.velocity.x * Gdx.graphics.getDeltaTime();
				curball.circ.y += curball.velocity.y * Gdx.graphics.getDeltaTime();
				curball.ballsprite.setPosition(curball.circ.x, curball.circ.y);
				if (curball.circ.x < -10 && curball.circ.y < -10 && curball.circ.x > 1120 && curball.circ.x > 1120) {
					iter.remove();
				} else if (MovementMath.lineCol(pastcirc, new Vector3(curball.circ.x, curball.circ.y, 0), player2) || MovementMath.overlaps(player2, curball.circ)) {
					iter.remove();
				} else {
					curball.ballsprite.draw(batch);
				}
			}
			if (!searchBoolArray(tilecol, true)) {
				points2++;
				ballsize = 8;
				ballsize2 = 8;
				kb = new Vector3(0, 0, 0);
				playerpower = 0;
				playerpowercooldown = 0;
				spawnprot = 40.0f;
				for (int i = 0; i < tilecol.length; i++) {
					tilerects[i].setWidth(196);
					tilerects[i].setHeight(196);
					tilerects[i].setPosition((((i % 5) * 196) + 64), (float) (Math.floor(i / 5f) * 196 + 64));
					rantilerespawn = -1;
				}
				tilecol[0] = true;
				tilecol2[0] = true;
				player.setPosition(WORLD_WIDTH / 2f - 32, WORLD_HEIGHT / 2f - 32);
				JSONObject data = new JSONObject();
				data.put("event", new JSONString("updateserverpoints"));
				data.put("winnerid", new JSONString(otherid));
				data.put("ran", new JSONString(random.nextInt(4) + ""));
				data.put("rantile", new JSONString(random.nextInt(25) + ""));
				data.put("id", new JSONString(myid));
				data.put("otherid", new JSONString(otherid));
				socket.send(JsonUtils.stringify(data.getJavaScriptObject()));
			}
			if (spawnprot <= 0)
				playerSprite.draw(batch, 1);
			else
				playerSprite.draw(batch, .75f);
			if (spawnprot2 <= 0)
				playerSprite2.draw(batch, 1);
			else
				playerSprite2.draw(batch, 0.75f);
			if (dashcooldown > 0) {
				dashcooldown -= Gdx.graphics.getDeltaTime() * 10;
			}
			if (spawnprot > 0) {
				spawnprot -= Gdx.graphics.getDeltaTime() * 10;
			}
			if (playerpowercooldown > 0) {
				playerpowercooldown -= Gdx.graphics.getDeltaTime();
			} else {
				playerpower = -1;
				playerpowercooldown = -1;
			}

			if (canreleaseball) {
				if (ballsize < 80)
					ballsize += Gdx.graphics.getDeltaTime() * 10;
				Vector3 ballpos = MovementMath.lengthDir(playerrot, 40f);
				if (mycolor.equals("red"))
					batch.draw(balltext, player.x + ballpos.x + playerSprite.getWidth() / 2 - ballsize / 2, player.y + ballpos.y + playerSprite.getHeight() / 2 - ballsize / 2, ballsize, ballsize);
				else
					batch.draw(balltext2, player.x + ballpos.x + playerSprite.getWidth() / 2 - ballsize / 2, player.y + ballpos.y + playerSprite.getHeight() / 2 - ballsize / 2, ballsize, ballsize);
			}
			if (ballsize2 != -1) {
				Vector3 ballpos = MovementMath.lengthDir(playerrot2, 40f);
				if (mycolor.equals("red"))
					batch.draw(balltext2, player2.x + ballpos.x + playerSprite2.getWidth() / 2 - ballsize2 / 2, player2.y + ballpos.y + playerSprite2.getHeight() / 2 - ballsize2 / 2, ballsize2, ballsize2);
				else
					batch.draw(balltext, player2.x + ballpos.x + playerSprite2.getWidth() / 2 - ballsize2 / 2, player2.y + ballpos.y + playerSprite2.getHeight() / 2 - ballsize2 / 2, ballsize2, ballsize2);
			}
			if (playerpowercooldown != -1)
				game.font.draw(batch, (1 + (int) playerpowercooldown / 4) + "", player.x + 16, player.y + player.radius * 2 + 24);
			if (dashcooldown > 0)
				game.font.draw(batch, "" + (1 + (int) dashcooldown / 4), player.x + 24, player.y);

			batch.end();

			game.batch.begin();
			game.font.draw(game.batch, "Points: " + points, 10, 470);
			game.font.draw(game.batch, "Opponent Points: " + points2, 440, 470);
			//debugger for html
			//game.font.draw(game.batch, mystring + " " + mynum+" "+array, 0, 200);
			game.batch.end();
		}
	}

	// nessasary overrides
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
		System.out.print("disposing");
		socket.close();
		playerTexture.dispose();
		batch.dispose();
		tiletexture.dispose();
		balltext.dispose();
		balltext2.dispose();

		//socket.disconnect();
	}

	public void drawHitbox(Texture hitbox, Circle circle, SpriteBatch batch) {
		Sprite hitboxsprite = new Sprite(hitbox);
		hitboxsprite.setScale(circle.radius / 32);
		batch.draw(hitboxsprite, circle.x, circle.y);
	}

	private boolean searchBoolArray(boolean[] array, boolean search) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == search)
				return true;
		return false;
	}

	public class Ball {
		Circle circ = new Circle();
		float rotation = 0;
		String color = "";
		Vector3 velocity = new Vector3(0, 0, 0);
		Sprite ballsprite;

		public void changeColor(String col) {
			color = col;
			if (col.equals("red")) {
				ballsprite = new Sprite(balltext);
			} else if (col.equals("blue")) {
				ballsprite = new Sprite(balltext2);
			}
		}
	}

	public class Power {
		public Sprite powersprite;
		public int type = -1;
		public Circle hitbox = new Circle();

		public void asignType(int argtype) {
			type = argtype;
			switch (argtype) {
				case 0: {
					powersprite = new Sprite(power0);
					powersprite.setPosition(hitbox.x, hitbox.y);
					break;
				}
			}
		}

		public void assignCircleValues(float argradius, Vector3 pos) {
			hitbox.radius = argradius;
			hitbox.x = pos.x;
			hitbox.y = pos.y;
		}

		public float getCooldown(int argtype) {
			switch (argtype) {
				case 0: {
					return 40.0f;
				}
			}
			return 0f;
		}
	}
}