package com.javagame.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import java.lang.Object;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Iterator;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;


public class GameScreen implements Screen{
    static final int WORLD_WIDTH = 1060;
	static final int WORLD_HEIGHT = 660;
	static final int TILE_WIDTH = Math.floorDiv(WORLD_WIDTH,16);;
	static final int TILE_HEIGHT = Math.floorDiv(WORLD_HEIGHT,16);;
	static final float SPEED = 35f;

	private OrthographicCamera cam;
	private SpriteBatch batch;
    final JavaGame game;

	private Rectangle player;
	private Texture playerSprite;
	private Texture grasstile;
	public int[][] Map;

	public GameScreen(final JavaGame game) {
        this.game = game;
		grasstile = new Texture("grasstile.png");
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 176, 112);
		playerSprite = new Texture(Gdx.files.internal("player.png"));
		batch = new SpriteBatch();
		player = new Rectangle();
		//player.setCenter(8,8);
		player.width = 16;
		player.height = 16;
		player.x = WORLD_WIDTH / 2;
		player.y = WORLD_HEIGHT/2;
		cam.position.set(player.x,player.y, 0);
		Map = WorldCreation.create2DMap(TILE_WIDTH,TILE_HEIGHT,1);
	}

	private void handleInput() {
		Vector3 moveVect = MovementMath.InputDir(0);
		Vector3 moveMag = MovementMath.lengthDir(MovementMath.pointDir(new Vector3(0,0,0),moveVect),MovementMath.pointDis(new Vector3(0,0,0),moveVect));
		float xadd = moveMag.x*SPEED* Gdx.graphics.getDeltaTime();
		float yadd = moveMag.y*SPEED* Gdx.graphics.getDeltaTime();
		if (player.x-xadd>96&&player.x+xadd<WORLD_WIDTH-96)
			player.x += xadd;
		if (player.y+yadd>64&&player.y-yadd<WORLD_WIDTH-64)
			player.y += yadd;

		cam.position.set(player.x,player.y, 0);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0,1);
		handleInput();
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		for(int x =  0; x<TILE_WIDTH;x++)
			for(int y =  0; y<TILE_HEIGHT;y++)
				batch.draw(grasstile,x*16,y*16);
		batch.draw(playerSprite,player.x,player.y);
		batch.end();
        game.batch.begin();
        game.font.draw(game.batch, "Player X: "+player.x+"\nPlayer Y: "+player.y, 10, 470);
        game.batch.end();
	}
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
		playerSprite.dispose();
		batch.dispose();
	}
}
