package com.dodgeball.game;

//import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;

//import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
	//vars
	final DodgeballGame game;
	public GlyphLayout textlayout;
	private Texture introtext;
	public String myname = "";
	private Texture button1v1;
	private Texture buttononline;
	private Texture controllerpic;
	private Texture rulebutton;
	public boolean choosingname = false;
	public boolean startinggame = false;
	private boolean touchingbutton;

	OrthographicCamera camera;
	String launcher = "html";

	public MainMenuScreen(final DodgeballGame game) {
		//creates camera, textures for the title screen, and sets the screen
		this.game = game;
		//launcher = gamelauncher;
		introtext = new Texture("title.png");
		button1v1 = new Texture("1v1button.png");
		buttononline = new Texture("onlinebutton.png");
		controllerpic = new Texture("controllers.png");
		rulebutton = new Texture("rules.png");
		game.setScreen(new GameScreen(game));

		camera = new OrthographicCamera();
		//int aspectratio = 2500/Gdx.graphics.getWidth();
		camera.setToOrtho(false, 800/*aspectratio*/, 480/*aspectratio*/);
	}

	@Override
	//renders images
	public void render(float delta) {
		//clears screen
		if (!startinggame) {
			ScreenUtils.clear(1, 1, 1f, 1);
		} else {
			ScreenUtils.clear(0, 0, 0f, 1);
		}

		if (MovementMath.pointDis(new Vector3(748f*(Gdx.graphics.getWidth()/800f),432*(Gdx.graphics.getHeight()/480f),0f),new Vector3(Gdx.input.getX(),Gdx.input.getY(),0f))<48f*(Gdx.graphics.getWidth()/800f)){
			touchingbutton = true;
		} else {
			touchingbutton = false;
		}

		//draws things
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		if (!startinggame) {
			game.batch.draw(introtext, 0, 0, 800, 480);
			game.batch.draw(button1v1, 152, 128, 96, 96);
			game.batch.draw(buttononline, 552, 128, 96, 96);
			game.batch.draw(rulebutton, 700, 4f, 96, 96);
		} else if (choosingname){
			game.font.draw(game.batch,"Enter 8 character name: \n"+myname,(300f)-(textlayout.width/2),240f);
		}
		if(touchingbutton) {
			game.batch.draw(controllerpic, 556, 4, 240, 192);
		}
		game.batch.end();

		//senses if starting a 1v1 game or online game
		if(!startinggame) {
			if (Gdx.input.isTouched() && MovementMath.pointDis(new Vector3(200f * (Gdx.graphics.getWidth() / 800f), 304f * (Gdx.graphics.getHeight() / 480f), 0f), new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f)) < 48f * (Gdx.graphics.getWidth() / 800f)) {
				game.setScreen(new GameScreen(game));
				startinggame = true;
				dispose();
			} else if (Gdx.input.isTouched() && MovementMath.pointDis(new Vector3(600f * (Gdx.graphics.getWidth() / 800f), 304f * (Gdx.graphics.getHeight() / 480f), 0f), new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f)) < 48f * (Gdx.graphics.getWidth() / 800f)) {
				choosingname = true;
				startinggame = true;
				textlayout = new GlyphLayout();
				dispose();
			}
		}
		if (choosingname&&startinggame){
			String justpressed = getKeyPressed();
			if(justpressed.equals("enter")){
				game.myname = myname;
				game.setScreen(new GameScreenMulti(game));
				choosingname = false;
			} else if(justpressed.equals("back")){
				myname+="back";
				//myname = myname.substring(0,myname.length()-3);
				//textlayout.setText(game.introfont,myname);
			} else if(myname.length()<9){
				myname+=justpressed;
				textlayout.setText(game.introfont,myname);
			}
		}
	}

	// necessary overrides
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
		introtext.dispose();
		button1v1.dispose();
		buttononline.dispose();
	}
	public static String getKeyPressed(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)||Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)||Gdx.input.isKeyJustPressed(Input.Keys.DEL)){
			return "back";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
			return "A";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
			return "B";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
			return "C";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
			return "D";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
			return "E";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)){
			return "F";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
			return "G";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
			return "H";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.I)){
			return "I";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
			return "J";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
			return "K";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
			return "L";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
			return "M";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.N)){
			return "N";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.O)){
			return "O";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
			return "P";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
			return "Q";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
			return "R";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
			return "S";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
			return "T";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.U)){
			return "U";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)){
			return "V";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
			return "W";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
			return "X";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
			return "Y";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){
			return "Z";
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
			return "enter";
		}

		return "";
	}
}