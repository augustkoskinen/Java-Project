package com.javagame.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

/*
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import java.lang.Object;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Iterator;
import com.badlogic.gdx.Input.Keys;
*/

public class GameScreen implements Screen{
    static final int WORLD_WIDTH = 1108;
	static final int WORLD_HEIGHT = 1108;
	static final float SPEED = 140f;
	static final float SPEED2 = 140f;
	Random random = new Random();
	int ran;

	private Rectangle tilerects[] = new Rectangle[25];
	private boolean tilecol[] = new boolean[25];
	private boolean tilecol2[] = new boolean[25];
	private OrthographicCamera cam;
	private SpriteBatch batch;
    final JavaGame game;
	private Texture tiletexture;
	private Texture debughitbox;

	//p1
	private Circle player;
	private Texture playerTexture;
	private Sprite playerSprite;
	private float playerrot = 0;
	
	//p1
	private Circle player2;
	private Texture playerTexture2;
	private Sprite playerSprite2;
	private float playerrot2 = 0;

	private Vector3 spawn1 = new Vector3(162, WORLD_HEIGHT/2,0);
	private Vector3 spawn2 = new Vector3(WORLD_WIDTH / 2, 162,0);
	private Vector3 spawn3 = new Vector3(WORLD_WIDTH-162, WORLD_HEIGHT/2,0);
	private Vector3 spawn4 = new Vector3(WORLD_WIDTH / 2, WORLD_HEIGHT-162,0);

	public GameScreen(final JavaGame game) {
        this.game = game;
		for(int i = 0; i < tilecol.length; i++){
			tilerects[i]=new Rectangle();
			tilerects[i].setWidth(196);
			tilerects[i].setHeight(196);
			tilerects[i].setPosition((((i%5)*196)+64),(float)(Math.floor(i/5f)*196+64));
			tilecol[i] = false;
			tilecol2[i] = false;
		}
		System.out.print(tilerects[0].getWidth());
		batch = new SpriteBatch();

		tiletexture = new Texture("Tiles.png");
		debughitbox = new Texture("debughitbox.png");

		//p1 setup
		playerTexture = new Texture(Gdx.files.internal("redplayer.png"));
		playerSprite = new Sprite(playerTexture, 0, 0, 64, 64);
		playerSprite.setRotation(playerrot);
		//playerSprite.setOrigin(playerSprite.getWidth()/2,playerSprite.getHeight()/2);
		
		player = new Circle();
		player.radius = playerSprite.getWidth()/2;
		player.setPosition(spawn1.x-32,spawn1.y-32);

		//p2 setup
		playerTexture2 = new Texture(Gdx.files.internal("blueplayer.png"));
		playerSprite2 = new Sprite(playerTexture2, 0, 0, 64, 64);
		playerSprite2.setRotation(playerrot2);
		//playerSprite2.setOrigin(playerSprite2.getWidth()/2,playerSprite2.getHeight()/2);
		
		player2 = new Circle();
		player2.radius = playerSprite2.getWidth()/2;
		player2.setPosition(spawn3.x-32,spawn3.y-32);
		
		//cam setup
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 704, 448);
	}

	private void handleInput() {
		//p1
		Vector3 moveVect = MovementMath.InputDir(0);
		playerrot = MovementMath.pointDir(new Vector3(0,0,0),moveVect);
		float playermag = MovementMath.pointDis(new Vector3(0,0,0),moveVect);
		Vector3 moveMag = MovementMath.lengthDir(playerrot,playermag);
		
		float xadd = moveMag.x*SPEED* Gdx.graphics.getDeltaTime();
		float yadd = moveMag.y*SPEED* Gdx.graphics.getDeltaTime();
		
		player.x += xadd;
		player.y += yadd;
		if(playermag!=0) {
			playerSprite.setRotation((float)Math.toDegrees((float)playerrot));
		}
		playerSprite.setPosition(player.x,player.y);

		//p2
		Vector3 moveVect2 = MovementMath.InputDir(1);
		playerrot2 = MovementMath.pointDir(new Vector3(0,0,0),moveVect2);
		float playermag2 = MovementMath.pointDis(new Vector3(0,0,0),moveVect2);
		Vector3 moveMag2 = MovementMath.lengthDir(playerrot2,playermag2);
		
		float xadd2 = moveMag2.x*SPEED2* Gdx.graphics.getDeltaTime();
		float yadd2 = moveMag2.y*SPEED2* Gdx.graphics.getDeltaTime();
		
		player2.x += xadd2;
		player2.y += yadd2;
		if(playermag2!=0) {
			playerSprite2.setRotation((float)Math.toDegrees((float)playerrot2));
		}
		playerSprite2.setPosition(player2.x,player2.y);

		//cam
		Vector3 cammp = MovementMath.midpoint(new Vector3(player.x+32,player.y+32,0),new Vector3(player2.x+32,player2.y+32,0));
		float camdis = MovementMath.pointDis(cam.position,cammp);
		float camdir = MovementMath.pointDir(cam.position,cammp);
		Vector3 campos = MovementMath.lengthDir(camdir,camdis);
		cam.position.set(cam.position.x+campos.x*.05f,cam.position.y+campos.y*.05f, 0);
		cam.zoom = Math.max(.9f,MovementMath.pointDis(new Vector3(player.x, player.y, 0),new Vector3(player2.x, player2.y, 0))/392);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0,1);
		
		handleInput();

		//System.out.println(searchBoolArray(tilecol,true));
		cam.update();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		for (int i = 0; i < tilerects.length; i++) {
			batch.draw(tiletexture,tilerects[i].x,tilerects[i].y);
			tilecol[i] =false;
			tilecol2[i] =false;
			if(MovementMath.overlaps(player,tilerects[i])) {
				tilecol[i] = true;
			}
			if(MovementMath.overlaps(player2,tilerects[i])) {
				tilecol2[i] = true;
			}
		}
		if(!searchBoolArray(tilecol, true)){
			ran = random.nextInt(4);
			switch (ran){
				case 0:{
					player.setPosition(spawn1.x-32,spawn1.y-32);
					break;
				}
				case 1:{
					player.setPosition(spawn2.x-32,spawn2.y-32);
					break;
				}
				case 2:{
					player.setPosition(spawn3.x-32,spawn3.y-32);
					break;
				}
				case 3:{
					player.setPosition(spawn4.x-32,spawn4.y-32);
					break;
				}
			}
		}
		if(!searchBoolArray(tilecol2, true)){
			ran = random.nextInt(4);
			switch (ran){
				case 0:{
					player2.setPosition(spawn1.x-32,spawn1.y-32);
					break;
				}
				case 1:{
					player2.setPosition(spawn2.x-32,spawn2.y-32);
					break;
				}
				case 2:{
					player2.setPosition(spawn3.x-32,spawn3.y-32);
					break;
				}
				case 3:{
					player2.setPosition(spawn4.x-32,spawn4.y-32);
					break;
				}
			}
		}
		playerSprite.draw(batch);
		playerSprite2.draw(batch);
		drawHitbox(debughitbox, player2, batch);
		batch.end();
        
		game.batch.begin();
        game.font.draw(game.batch, "Player X: "+player.x+"\nPlayer Y: "+player.y, 10, 470);
		game.font.draw(game.batch, "Player2 X: "+player2.x+"\nPlayer2 Y: "+player2.y, 370, 470);
        game.batch.end();
	}

	//nessasary overrides
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
		playerTexture.dispose();
		batch.dispose();
		tiletexture.dispose();
	}
	public void drawHitbox(Texture hitbox, Circle circle, SpriteBatch batch){
		Sprite hitboxsprite = new Sprite(hitbox);
		hitboxsprite.setScale(circle.radius/32);
		batch.draw(hitboxsprite,circle.x,circle.y);
	}
	private boolean searchBoolArray(boolean[] array, boolean search){
		for (int i = 0; i < array.length; i++)
			if (array[i]==search)
			return true;
		return false;
	}
}
