package com.courter.parachutegunner.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.courter.parachutegunner.Assets;
import com.courter.parachutegunner.OverlapTester;
import com.courter.parachutegunner.Settings;

public class OptionsScreen implements Screen{
	Game game;
	public final static String optionTitle = "OPTIONS";

	OrthographicCamera guiCam;
	SpriteBatch batcher;
	Rectangle backBounds;
	Rectangle difficultyBounds;
	Rectangle soundBounds;
	Rectangle tutorialBounds;
	Rectangle creditsBounds;
	Vector3 touchPoint;
	float xOffset = 0;

	public OptionsScreen (Game game) {
		this.game = game;

		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		backBounds = new Rectangle(0, 0, 64, 64);
		difficultyBounds = new Rectangle(160, 350, 128, 32);
		soundBounds = new Rectangle(110, 330, 96, 32);
		tutorialBounds = new Rectangle(160, 310, 128, 32);
		creditsBounds = new Rectangle(100, 200, 128, 32);
		touchPoint = new Vector3();
		batcher = new SpriteBatch();
		
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (OverlapTester.pointInRectangle(backBounds, touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Settings.save();
				game.setScreen(new MainMenuScreen(game));
				return;
			}
			else if (OverlapTester.pointInRectangle(difficultyBounds, touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Settings.incrementDifficulty();
				draw();
			}
			else if(OverlapTester.pointInRectangle(soundBounds, touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Settings.soundEnabled = !Settings.soundEnabled;
				if (Settings.soundEnabled)
					Assets.music.play();
				else
					Assets.music.pause();
				draw();
			}
			else if (OverlapTester.pointInRectangle(tutorialBounds, touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Settings.resetShowTutorial();
				draw();
			}
			else if (OverlapTester.pointInRectangle(creditsBounds, touchPoint.x, touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new CreditsScreen(game));
				return;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			Settings.save();
			game.setScreen(new MainMenuScreen(game));
			return;
		}
	}

	public void draw () {
		GLCommon gl = Gdx.gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.update();

		batcher.setProjectionMatrix(guiCam.combined);
		batcher.disableBlending();
		batcher.begin();
		batcher.draw(Assets.backgroundRegion, 0, 0, 320, 480);
		batcher.end();

		batcher.enableBlending();
		batcher.begin();
		Assets.font.draw(batcher, optionTitle, 160 - (Assets.font.getBounds(optionTitle).width / 2), 390);
		
		String difficultyOption = "Difficulty: " + Settings.difficulties[Settings.currentDifficulty];
		String soundOption = "Sound: ";
		if(Settings.soundEnabled)
			soundOption += "On";
		else
			soundOption += "Off";
		String tutorialOption = "Show Tutorial: ";
		if(Settings.showTutorial)
			tutorialOption += "YES";
		else
			tutorialOption += "NO"; 
		Assets.font.draw(batcher, difficultyOption, 10, 370);
		Assets.font.draw(batcher, soundOption, 10, 350);
		Assets.font.draw(batcher, tutorialOption, 10, 330);
		Assets.font.draw(batcher, "Credits", 110, 230);

		batcher.draw(Assets.arrow, 0, 0, 64, 64);
		batcher.end();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
