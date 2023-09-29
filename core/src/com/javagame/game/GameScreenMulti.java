package com.javagame.game;

import java.util.Iterator;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Input;

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
	private Socket socket;
	private boolean start = false;
	private String mycolor = "red";
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
	final JavaGame game;
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
	private float spawnprot2 = 20.0f;
	float xadd2 = 0;
	float yadd2 = 0;
	float moveVectx = 0;
	float moveVecty = 0;

	private Vector3 spawn1 = new Vector3(162, WORLD_HEIGHT / 2, 0);
	private Vector3 spawn2 = new Vector3(WORLD_WIDTH / 2, 162, 0);
	private Vector3 spawn3 = new Vector3(WORLD_WIDTH - 162, WORLD_HEIGHT / 2, 0);
	private Vector3 spawn4 = new Vector3(WORLD_WIDTH / 2, WORLD_HEIGHT - 162, 0);

	public void connectSocket() {
		try {
			socket = IO.socket("https://game.ejenda.org");
			socket.connect();
		} catch (Exception e) {
			socket.disconnect();
		}
	}
	private String id;
	private String myid = "";
	private String otherid = "";

	public void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				// player = new Starship(playerShip);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					if(myid==""){
						id = data.getString("id");
						Gdx.app.log("SocketIO", "My ID: " + id);
						myid = id;
					}
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray data = (JSONArray) args[0];
				try {
					String id1 = data.getJSONObject(data.length()-2).getString("id");
					String id2 = data.getJSONObject(data.length()-1).getString("id");
					if(myid.equals(id1)){
						otherid = id2;
						mycolor = "red";
					}
					else if(myid.equals(id2)){
						otherid = id1;
						mycolor = "blue";
					}
				} catch (JSONException e) {

				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					if(myid==id){
						socket.disconnect();
					}
					// friendlyPlayers.remove(id);
					System.out.println("Player Disconnected: " + id);
					//disconnectSocket();
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("startGame", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					//other vars
					start = true;
					int seed = data.getInt("seed");
        			random = new Random(seed);
					rantile = random.nextInt(25);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("movement", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					if (objects.getString("id").equals(otherid)){
						float x = objects.getFloat("x");
						float y = objects.getFloat("y");
						player2.setPosition(x,y);
						playerrot2 = objects.getFloat("rotation");
						kb.x+= objects.getFloat("kbaddx");
						kb.y+= objects.getFloat("kbaddy");
						xadd2 = objects.getFloat("xadd2");
						yadd2 =  objects.getFloat("yadd2");
						moveVectx = objects.getFloat("moveVectx");
						moveVecty = objects.getFloat("moveVecty");
						dashvel2x = objects.getFloat("dashvelx");
						dashvel2y = objects.getFloat("dashvely");
						spawnprot2 = objects.getFloat("spawnprot");
						ballsize2 = objects.getFloat("ballsize");
					}
				} catch (JSONException e) {

				}
			}
		}).on("shootBullet", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					if (objects.getString("id").equals(otherid)){
						int ballamount = objects.getInt("ballcount");
						float ballsize = (float)objects.getDouble("ballsize");
						int mult = 0;
						for (int i = 0; i<ballamount;i++){
							if(i==1){
								mult = 1;
							} else if(i==2){
								mult = -1;
							}
							Ball ball = new Ball();
							ball.rotation = playerrot2;
							ball.changeColor(objects.getString("color"));
							Vector3 ballpos = MovementMath.lengthDir(playerrot2+0.349066f*mult, 40f);
							Vector3 ballvel = MovementMath.lengthDir(playerrot2+0.349066f*mult, ballsize * 80 + 50f);
							ball.circ.setPosition(player2.x + ballpos.x + 28, player2.y + ballpos.y + 28);
							ball.velocity = ballvel;
							ball.ballsprite.setRotation(playerrot2+0.349066f*mult);
							ball.ballsprite.setScale(ballsize / 8);
							ball.circ.radius = ballsize / 2;
							blueballs.add(ball);
						}
					}
				} catch (JSONException e) {

				}
			}
		}).on("updatePoints", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					if(objects.getString("id").equals(myid)){
						points++;
					} else if (objects.getString("id").equals(otherid)){
						points2++;
					}
					ran = objects.getInt("ran");
					rantile = objects.getInt("rantile");
				} catch (JSONException e) {

				}
			}
		}).on("setTiles", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					for(int i = 0; i < 25; i++){
						tilerects[i].width =objects.getJSONArray("jstilerects").getJSONObject(i).getFloat("width");
						tilerects[i].height =objects.getJSONArray("jstilerects").getJSONObject(i).getFloat("height");
						tilerects[i].setPosition(objects.getJSONArray("jstilerects").getJSONObject(i).getFloat("x"),objects.getJSONArray("jstilerects").getJSONObject(i).getFloat("y"));
					}
				} catch (JSONException e) {

				}
			}
		}).on("makePower", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					Power power = new Power();
					power.type = (int) Math.random() % 5;
					switch (objects.getInt("randpos")) {
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
				} catch (JSONException e) {

				}
			}
		});
	}

	public GameScreenMulti(final JavaGame game) {
		connectSocket();
		configSocketEvents();
		this.game = game;
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
		kbaddx*= 0.5f;
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
			if(playerpower==0)
				ballamount = 3;
			else
				ballamount = 1;
			int mult = 0;
			for (int i = 0; i<ballamount;i++){
				if(i==1){
					mult = 1;
				} else if(i==2){
					mult = -1;
				}
				Ball ball = new Ball();
				ball.rotation = playerrot;
				ball.changeColor(mycolor);
				Vector3 ballpos = MovementMath.lengthDir(playerrot+0.349066f*mult, 40f);
				Vector3 ballvel = MovementMath.lengthDir(playerrot+0.349066f*mult, ballsize * 80 + 50f);
				ball.circ.setPosition(player.x + ballpos.x + 28, player.y + ballpos.y + 28);
				ball.velocity = ballvel;
				ball.ballsprite.setRotation(playerrot+0.349066f*mult);
				ball.ballsprite.setScale(ballsize / 8);
				ball.circ.radius = ballsize / 2;
				myballs.add(ball);
			}

			JSONObject data = new JSONObject();
			data.put("ballcount", ballamount);
			data.put("ballsize", ballsize);
			data.put("color", mycolor);
			socket.emit("shootmyball", data);

			canreleaseball = false;
			spawnprot = -1;
			ballsize = 8;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.F) && !canreleaseball) {
			canreleaseball = true;
		}
		// bumping
		if (MovementMath.overlaps(player, player2)&& (Math.abs(dashvel.x) + Math.abs(dashvel.y) + Math.abs(dashvel2x) + Math.abs(dashvel2y) < 50)) {
			Vector3 bumpvel1 = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(xadd,yadd, 0)), 200f);
			Vector3 bumpvel2 = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(xadd2,yadd2, 0)), 200f);
			Vector3 bumpvel1flip = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(-xadd,-yadd, 0)), 200f);
			Vector3 bumpvel2flip = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0, 0, 0), new Vector3(-xadd2,-yadd2, 0)), 200f);
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
		JSONObject data = new JSONObject();
		data.put("x", player.x);
		data.put("y", player.y);
		data.put("rotation", playerrot);
		data.put("xadd2", xadd2);
		data.put("yadd2", yadd2);
		data.put("moveVectx", moveVectx);
		data.put("moveVecty", moveVecty);
		data.put("kbaddx", kbaddx);
		data.put("kbaddy", kbaddy);
		data.put("dashvelx", dashvel.x);
		data.put("dashvely", dashvel.y);
		data.put("spawnprot", spawnprot);
		if(canreleaseball)
			data.put("ballsize", ballsize);
		else
			data.put("ballsize", -1f);
		data.put("mytime", Gdx.graphics.getDeltaTime());
		socket.emit("playermove", data);

		// cam
		float camdis = MovementMath.pointDis(cam.position, new Vector3(player.x+player.radius,player.y+player.radius, 0));
		float camdir = MovementMath.pointDir(cam.position, new Vector3(player.x+player.radius,player.y+player.radius, 0));
		Vector3 campos = MovementMath.lengthDir(camdir, camdis);
		cam.position.set(cam.position.x + campos.x * .05f, cam.position.y + campos.y * .05f, 0);
		cam.zoom = 1.5f;
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		if (start){
			if(!createdplayers){
				createdplayers = true;
				if(mycolor == "red"){
					player.setPosition(spawn1.x-32, spawn1.y-32);
					playerTexture = new Texture(Gdx.files.internal("redplayer.png"));
					playerTexture2 = new Texture(Gdx.files.internal("blueplayer.png"));
				}
				else if(mycolor == "blue"){
					player.setPosition(spawn3.x-32, spawn3.y-32);
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
				cam.position.set(player.x+player.radius,player.y+player.radius,0);
				Gdx.app.log("SocketIO", "Other ID: " + otherid);
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
			for (Iterator<Power> iter = poweruparray.iterator(); iter.hasNext();) {
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
			for (Iterator<Ball> iter = blueballs.iterator(); iter.hasNext();) {
				Ball curball = iter.next();
				Vector3 pastcirc = new Vector3(curball.circ.x, curball.circ.y, 0);
				curball.circ.x += curball.velocity.x * Gdx.graphics.getDeltaTime();
				curball.circ.y += curball.velocity.y * Gdx.graphics.getDeltaTime();
				curball.ballsprite.setPosition(curball.circ.x, curball.circ.y);
				if (curball.circ.x < -10 && curball.circ.y < -10 && curball.circ.x > 1120 && curball.circ.x > 1120) {
					iter.remove();
				} else if (MovementMath.lineCol(pastcirc, new Vector3(curball.circ.x, curball.circ.y, 0), player)|| MovementMath.overlaps(player, curball.circ)) {
					if (spawnprot <= 0) {
						kb.x += curball.velocity.x * 1.25f;
						kb.y += curball.velocity.y * 1.25f;
					}
					iter.remove();
				} else {
					curball.ballsprite.draw(batch);
				}
			}
			for (Iterator<Ball> iter = myballs.iterator(); iter.hasNext();) {
				Ball curball = iter.next();
				Vector3 pastcirc = new Vector3(curball.circ.x, curball.circ.y, 0);
				curball.circ.x += curball.velocity.x * Gdx.graphics.getDeltaTime();
				curball.circ.y += curball.velocity.y * Gdx.graphics.getDeltaTime();
				curball.ballsprite.setPosition(curball.circ.x, curball.circ.y);
				if (curball.circ.x < -10 && curball.circ.y < -10 && curball.circ.x > 1120 && curball.circ.x > 1120) {
					iter.remove();
				} else if (MovementMath.lineCol(pastcirc, new Vector3(curball.circ.x, curball.circ.y, 0), player2)|| MovementMath.overlaps(player2, curball.circ)) {
					iter.remove();
				} else {
					curball.ballsprite.draw(batch);
				}
			}
			if (!searchBoolArray(tilecol, true)) {
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
				player.setPosition(WORLD_WIDTH / 2 - 32, WORLD_HEIGHT / 2 - 32);
				JSONObject data = new JSONObject();
				data.put("id",otherid);
				data.put("ran",random.nextInt(4));
				data.put("rantile",random.nextInt(25));
				socket.emit("updateserverpoints",(data));
			}
			if (!searchBoolArray(tilecol2, true)) {
				ballsize = 8;
				ballsize2 = 8;
				ran = random.nextInt(4);
				kb = new Vector3(0, 0, 0);
				spawnprot2 = 40.0f;
				for (int i = 0; i < tilecol.length; i++) {
					tilerects[i].setWidth(196);
					tilerects[i].setHeight(196);
					tilerects[i].setPosition((((i % 5) * 196) + 64), (float) (Math.floor(i / 5f) * 196 + 64));
					rantile = random.nextInt(25);
					rantilerespawn = -1;
				}
				tilecol[0] = true;
				tilecol2[0] = true;
				player2.setPosition(WORLD_WIDTH / 2 - 32, WORLD_HEIGHT / 2 - 32);
				JSONObject data = new JSONObject();
				data.put("id",myid);
				data.put("ran",random.nextInt(4));
				data.put("rantile",random.nextInt(25));
				socket.emit("updatePoints",(data));
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
				if(mycolor.equals("red"))
					batch.draw(balltext, player.x + ballpos.x + playerSprite.getWidth() / 2 - ballsize / 2,player.y + ballpos.y + playerSprite.getHeight() / 2 - ballsize / 2, ballsize, ballsize);
				else
					batch.draw(balltext2, player.x + ballpos.x + playerSprite.getWidth() / 2 - ballsize / 2,player.y + ballpos.y + playerSprite.getHeight() / 2 - ballsize / 2, ballsize, ballsize);
			}
			if (ballsize2!=-1){
				Vector3 ballpos = MovementMath.lengthDir(playerrot2, 40f);
				if(mycolor.equals("red"))
					batch.draw(balltext2, player2.x + ballpos.x + playerSprite2.getWidth() / 2 - ballsize2 / 2,player2.y + ballpos.y + playerSprite2.getHeight() / 2 - ballsize2 / 2, ballsize2, ballsize2);
				else
					batch.draw(balltext, player2.x + ballpos.x + playerSprite2.getWidth() / 2 - ballsize2 / 2,player2.y + ballpos.y + playerSprite2.getHeight() / 2 - ballsize2 / 2, ballsize2, ballsize2);
			}
			if (playerpowercooldown != -1)
				game.font.draw(batch, (1 + (int) playerpowercooldown / 4) + "", player.x + 16,player.y + player.radius * 2 + 24);
			if (dashcooldown > 0)
				game.font.draw(batch, "" + (1 + (int) dashcooldown / 4), player.x + 24, player.y);
			batch.end();

			game.batch.begin();
			game.font.draw(game.batch, "Points: " + points, 10, 470);
			game.font.draw(game.batch, "Opponent Points: " + points2, 440, 470);
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
		playerTexture.dispose();
		batch.dispose();
		tiletexture.dispose();
		balltext.dispose();
		balltext2.dispose();
		socket.disconnect();
		try{
			socket.disconnect();
		} catch (Exception e) {

		}
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
			} else if (col.equals("blue")){
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