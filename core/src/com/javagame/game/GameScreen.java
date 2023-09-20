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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.TimeUtils;

/*
import com.badlogic.gdx.ApplicationListener;
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
	int rantile = -1;
	int rantilerespawn = -1;

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
	private Array<Ball> redballs = new Array<Ball>();
	private boolean canreleaseball = false;
	private Texture balltext;
	private float ballsize = 8;
	private Vector3 kb = new Vector3(0, 0, 0);
	private int points = 0;
	
	//p2
	private Circle player2;
	private Texture playerTexture2;
	private Sprite playerSprite2;
	private float playerrot2 = 180;
	private Array<Ball> blueballs = new Array<Ball>();
	private boolean canreleaseball2 = false;
	private Texture balltext2;
	private float ballsize2 = 8;
	private Vector3 kb2 = new Vector3(0, 0, 0);
	private int points2 = 0;

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
		batch = new SpriteBatch();

		tiletexture = new Texture("Tiles.png");
		debughitbox = new Texture("debughitbox.png");

		//p1 setup
		playerTexture = new Texture(Gdx.files.internal("redplayer.png"));
		balltext = new Texture(Gdx.files.internal("redball.png"));
		playerSprite = new Sprite(playerTexture, 0, 0, 64, 64);
		playerSprite.setRotation(playerrot);
		
		player = new Circle();
		player.radius = playerSprite.getWidth()/2;
		player.setPosition(spawn1.x-32,spawn1.y-32);

		//p2 setup
		playerTexture2 = new Texture(Gdx.files.internal("blueplayer.png"));
		balltext2 = new Texture(Gdx.files.internal("blueball.png"));
		playerSprite2 = new Sprite(playerTexture2, 0, 0, 64, 64);
		playerSprite2.setRotation(playerrot2);
		
		player2 = new Circle();
		player2.radius = playerSprite2.getWidth()/2;
		player2.setPosition(spawn3.x-32,spawn3.y-32);
		
		//cam setup
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 704, 448);
		cam.position.set(MovementMath.midpoint(new Vector3(player.x+player.radius,player.y+player.radius,0),new Vector3(player2.x+player.radius,player2.y+player.radius,0)));
	}

	private void handleInput() {
		//p1
		Vector3 moveVect = MovementMath.InputDir(0);
		float playermag = MovementMath.pointDis(new Vector3(0,0,0),moveVect);
		Vector3 moveMag = new Vector3(0, 0, 0);
		if(playermag!=0) {
			playerrot = MovementMath.pointDir(new Vector3(0,0,0),moveVect);
			moveMag = MovementMath.lengthDir(playerrot,playermag);
		}
		
		float xadd = moveMag.x*SPEED* Gdx.graphics.getDeltaTime();
		float yadd = moveMag.y*SPEED* Gdx.graphics.getDeltaTime();
		
		kb.x*=0.8f;
		kb.y*=0.8f;

		player.x += xadd+kb.x*Gdx.graphics.getDeltaTime();
		player.y += yadd+kb.y*Gdx.graphics.getDeltaTime();
		if(playermag!=0) {
			playerSprite.setRotation((float)Math.toDegrees((float)playerrot));
		}
		playerSprite.setPosition(player.x,player.y);
		if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)&&canreleaseball) {
			canreleaseball =false;
			Ball ball = new Ball();
			ball.rotation = playerrot;
			ball.changeColor("red");
			Vector3 ballpos = MovementMath.lengthDir(playerrot,40f);
			Vector3 ballvel = MovementMath.lengthDir(playerrot,ballsize*50+100f);
			ball.circ.setPosition(player.x+ballpos.x+28, player.y+ballpos.y+28);
			ball.velocity = ballvel;
			ball.ballsprite.setRotation(playerrot);
			ball.ballsprite.setScale(ballsize/8);
			ball.circ.radius = ballsize/2;
			ballsize = 8;
			redballs.add(ball);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)&&!canreleaseball) {
			canreleaseball = true;
		}

		//p2
		Vector3 moveVect2 = MovementMath.InputDir(1);
		float playermag2 = MovementMath.pointDis(new Vector3(0,0,0),moveVect2);
		Vector3 moveMag2 = new Vector3(0, 0, 0);
		if(playermag2!=0) {
			playerrot2 = MovementMath.pointDir(new Vector3(0,0,0),moveVect2);
			moveMag2 = MovementMath.lengthDir(playerrot2,playermag2);
		}
		
		float xadd2 = moveMag2.x*SPEED2* Gdx.graphics.getDeltaTime();
		float yadd2 = moveMag2.y*SPEED2* Gdx.graphics.getDeltaTime();
		
		kb2.x*=0.8f;
		kb2.y*=0.8f;

		player2.x += xadd2+kb2.x* Gdx.graphics.getDeltaTime();
		player2.y += yadd2+kb2.y* Gdx.graphics.getDeltaTime();
		if(playermag2!=0) {
			playerSprite2.setRotation((float)Math.toDegrees((float)playerrot2));
		}
		playerSprite2.setPosition(player2.x,player2.y);

		if (!Gdx.input.isKeyPressed(Input.Keys.M)&&canreleaseball2) {
			canreleaseball2 = false;
			Ball ball2 = new Ball();
			ball2.rotation = playerrot2;
			ball2.changeColor("blue");
			Vector3 ballpos2 = MovementMath.lengthDir(playerrot2,40f);
			Vector3 ballvel = MovementMath.lengthDir(playerrot2,ballsize2*50+100f);
			ball2.circ.setPosition(player2.x+ballpos2.x+28, player2.y+ballpos2.y+28);
			ball2.velocity = ballvel;
			ball2.ballsprite.setRotation(playerrot2);
			ball2.ballsprite.setScale(ballsize2/8);
			ball2.circ.radius = ballsize2/2;
			ballsize2 = 8;
			blueballs.add(ball2);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.M)&&!canreleaseball2) {
			canreleaseball2 = true;
		}

		//cam
		Vector3 cammp = MovementMath.midpoint(new Vector3(player.x+player.radius,player.y+player.radius,0),new Vector3(player2.x+player.radius,player2.y+player.radius,0));
		float camdis = MovementMath.pointDis(cam.position,cammp);
		float camdir = MovementMath.pointDir(cam.position,cammp);
		Vector3 campos = MovementMath.lengthDir(camdir,camdis);
		cam.position.set(cam.position.x+campos.x*.05f,cam.position.y+campos.y*.05f, 0);
		cam.zoom = Math.max(1f,MovementMath.pointDis(new Vector3(player.x, player.y, 0),new Vector3(player2.x, player2.y, 0))/392);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0,1);
		
		handleInput();

		cam.update();
		if(random.nextInt(1000*(int)(1+Math.abs(Gdx.graphics.getDeltaTime())))==0){
			if(rantile==-1)
				rantile = random.nextInt(25);
			else if (tilerects[rantile].width<=0){
				rantile = random.nextInt(25);
			}
		} else if (rantile!=-1){
			if (tilerects[rantile].width>0){
				float changefactor = (float)Gdx.graphics.getDeltaTime()*10;
				tilerects[rantile].width-=(changefactor);
				tilerects[rantile].height-=(changefactor);
				tilerects[rantile].x+=changefactor/2;
				tilerects[rantile].y+=changefactor/2;
			}
		}

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		for (int i = 0; i < tilerects.length; i++) {
			batch.draw(tiletexture,tilerects[i].x,tilerects[i].y,tilerects[i].width,tilerects[i].height);
			tilecol[i] =false;
			tilecol2[i] =false;
			if(MovementMath.overlaps(player,tilerects[i])) {
				tilecol[i] = true;
			}
			if(MovementMath.overlaps(player2,tilerects[i])) {
				tilecol2[i] = true;
			}
		}
		for (Iterator<Ball> iter = redballs.iterator(); iter.hasNext(); ) {
			Ball curball = iter.next();
			curball.circ.x+=curball.velocity.x*Gdx.graphics.getDeltaTime();
			curball.circ.y+=curball.velocity.y*Gdx.graphics.getDeltaTime();
			curball.ballsprite.setPosition(curball.circ.x,curball.circ.y);
			if(curball.circ.x<-10&&curball.circ.y<-10&&curball.circ.x>1120&&curball.circ.x>1120){
				iter.remove();
			} else if(MovementMath.overlaps(player2,curball.circ)) {
				kb2.x=curball.velocity.x;
				kb2.y=curball.velocity.y; 
				iter.remove();
			}
			else {curball.ballsprite.draw(batch); }
		}
		for (Iterator<Ball> iter = blueballs.iterator(); iter.hasNext(); ) {
			Ball curball = iter.next();
			curball.circ.x+=curball.velocity.x*Gdx.graphics.getDeltaTime();
			curball.circ.y+=curball.velocity.y*Gdx.graphics.getDeltaTime();
			curball.ballsprite.setPosition(curball.circ.x,curball.circ.y);
			if(curball.circ.x<-10&&curball.circ.y<-10&&curball.circ.x>1120&&curball.circ.x>1120){
				iter.remove();
			} else if (MovementMath.overlaps(player,curball.circ)) {
				kb.x=curball.velocity.x*2;
				kb.y=curball.velocity.y*2;
				iter.remove();
			}
			else {curball.ballsprite.draw(batch); }
		}
		if(!searchBoolArray(tilecol, true)){
			points2++;
			ran = random.nextInt(4);
			for(int i = 0; i < tilecol.length; i++){
				tilerects[i].setWidth(196);
				tilerects[i].setHeight(196);
				tilerects[i].setPosition((((i%5)*196)+64),(float)(Math.floor(i/5f)*196+64));
				rantile = -1;
				rantilerespawn = -1;
			}
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
			points++;
			ran = random.nextInt(4);
			for(int i = 0; i < tilecol.length; i++){
				tilerects[i].setWidth(196);
				tilerects[i].setHeight(196);
				tilerects[i].setPosition((((i%5)*196)+64),(float)(Math.floor(i/5f)*196+64));
				rantile = -1;
				rantilerespawn = -1;
			}
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
		if(canreleaseball){
			ballsize+=Gdx.graphics.getDeltaTime()*10;
			Vector3 ballpos = MovementMath.lengthDir(playerrot,40f);
			batch.draw(balltext,player.x+ballpos.x+playerSprite.getWidth()/2-ballsize/2,player.y+ballpos.y+playerSprite.getHeight()/2-ballsize/2,ballsize,ballsize);
		}
		if(canreleaseball2){
			ballsize2+=Gdx.graphics.getDeltaTime()*10;
			Vector3 ballpos2 = MovementMath.lengthDir(playerrot2,40f);
			batch.draw(balltext2,player2.x+ballpos2.x+playerSprite2.getWidth()/2-ballsize2/2,player2.y+ballpos2.y+playerSprite2.getHeight()/2-ballsize2/2,ballsize2,ballsize2);
		}
		playerSprite.draw(batch);
		playerSprite2.draw(batch);
		batch.end();
        
		game.batch.begin();
        game.font.draw(game.batch, "Player Points: "+points, 10, 470);
		game.font.draw(game.batch, "Player2 Points: "+points2, 470, 470);
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
		balltext.dispose();
		balltext2.dispose();
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

	public class Ball {
		Circle circ = new Circle();
		float rotation = 0;
		String color = "";
		Vector3 velocity = new Vector3(0, 0, 0);
		Sprite ballsprite;
		public void changeColor(String col){
			color = col;
			if(col == "red"){
				ballsprite = new Sprite(balltext);
			} else{
				ballsprite = new Sprite(balltext2);
			}
		}
	}
}