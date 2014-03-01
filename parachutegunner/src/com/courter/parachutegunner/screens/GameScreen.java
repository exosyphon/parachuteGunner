/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.courter.parachutegunner.screens;

import sun.security.x509.DeltaCRLIndicatorExtension;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.courter.parachutegunner.Assets;
import com.courter.parachutegunner.OverlapTester;
import com.courter.parachutegunner.ParachuteGunner;
import com.courter.parachutegunner.Settings;
import com.courter.parachutegunner.World;
import com.courter.parachutegunner.World.WorldListener;
import com.courter.parachutegunner.WorldRenderer;
import com.swarmconnect.SwarmLeaderboard;

public class GameScreen implements Screen {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_LEVEL_END = 3;
	static final int GAME_OVER = 4;
	private static final int LEADERBOARD_ID = 7053;
	private static final float fullAdShowPeriod = 5;

	Game game;

	int state;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	SpriteBatch batcher;
	World world;
	WorldListener worldListener;
	WorldRenderer renderer;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	Rectangle flameWallBounds;
	Rectangle exitGameBounds;
	Rectangle retryGameBounds;
	int lastScore;
	float lastFallSpeed;
	String scoreString;
	String congratsString;
	float fullAdTimeBegin;
	float fullAdTimeShown;

	public GameScreen(Game game) {
		this.game = game;

		state = GAME_READY;
		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		touchPoint = new Vector3();
		batcher = new SpriteBatch();
		worldListener = new WorldListener() {
			@Override
			public void hit() {
				Assets.playSound(Assets.hitSound);
			}

			@Override
			public void coin() {
				Assets.playSound(Assets.coinSound);
			}

			@Override
			public void kill() {
				Assets.playSound(Assets.explosionSound);
			}

			@Override
			public void powerup() {
				Assets.playSound(Assets.powerupSound);
			}
		};
		world = new World(worldListener);
		renderer = new WorldRenderer(batcher, world);
		flameWallBounds = new Rectangle(0, 0 + 16, 48, 48);
		pauseBounds = new Rectangle(320 - 80, 480 - 96, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 40);
		exitGameBounds = new Rectangle(40, 70, 96, 64);
		retryGameBounds = new Rectangle(160, 70, 128, 64);
		lastScore = 0;
		lastFallSpeed = 0;
		scoreString = "SCORE: 0";
		congratsString = "";
		fullAdTimeBegin = 0;
		fullAdTimeShown = 0;

		ParachuteGunner.ExternalHandler.showAd(true);
	}

	public void update(float deltaTime) {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;

		switch (state) {
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_LEVEL_END:
			updateLevelEnd();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateReady() {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateRunning(float deltaTime) {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					0));

			if (OverlapTester.pointInRectangle(pauseBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				state = GAME_PAUSED;
				return;
			} else if ((world.flameWallActive)
					&& (OverlapTester.pointInRectangle(flameWallBounds,
							touchPoint.x, touchPoint.y))) {
				world.addFlameWall();
				Assets.playSound(Assets.fireSound);
			} else if (state == GAME_RUNNING) {
				world.addBullet();
				Assets.playSound(Assets.laserSound);
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			game.setScreen(new MainMenuScreen(game));
			return;
		}

		ApplicationType appType = Gdx.app.getType();

		// should work also with
		// Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)
		if (appType == ApplicationType.Android
				|| appType == ApplicationType.iOS) {
			world.update(deltaTime, Gdx.input.getAccelerometerX());
		} else {
			float accel = 0;
			if (Gdx.input.isKeyPressed(Keys.DPAD_LEFT))
				accel = 2f;
			if (Gdx.input.isKeyPressed(Keys.DPAD_RIGHT))
				accel = -2f;
			world.update(deltaTime, accel);
		}
		if (world.score != lastScore) {
			lastScore = world.score;
			scoreString = "SCORE: " + lastScore;
		}
		if (world.state == World.WORLD_STATE_NEXT_LEVEL) {
			ParachuteGunner.ExternalHandler.showFullAd(true);
			fullAdTimeBegin = deltaTime;
			if (world.fallSpeed != lastFallSpeed) {
				lastFallSpeed = world.fallSpeed;
			}
			state = GAME_LEVEL_END;
		}
		if (world.state == World.WORLD_STATE_GAME_OVER) {
			state = GAME_OVER;
			if (lastScore >= Settings.highscores[0]) {
				congratsString = "Congrats! NEW";
				scoreString = "HISCORE:" + lastScore;
				SwarmLeaderboard.submitScore(LEADERBOARD_ID, (float) lastScore);
			} else {
				congratsString = "";
				scoreString = "SCORE: " + lastScore;
			}
			Settings.addScore(lastScore);
			Settings.save();
			world.checkPointsAchievements(lastScore);
		}
	}

	private void updatePaused() {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					0));

			if (OverlapTester.pointInRectangle(resumeBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				state = GAME_RUNNING;
				return;
			}

			if (OverlapTester.pointInRectangle(quitBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			game.setScreen(new MainMenuScreen(game));
			return;
		}
	}

	private void updateLevelEnd() {
		if (fullAdTimeBegin == 0) {
			if (Gdx.input.justTouched()) {
				world = new World(worldListener);
				renderer = new WorldRenderer(batcher, world);
				world.score = lastScore;
				world.fallSpeed = lastFallSpeed;
				state = GAME_READY;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
				Settings.addScore(lastScore);
				Settings.save();
				if (lastScore >= Settings.highscores[0])
					SwarmLeaderboard.submitScore(LEADERBOARD_ID,
							(float) lastScore);
				world.checkPointsAchievements(lastScore);
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		} else {
			fullAdTimeShown += .01f;
			if (fullAdTimeShown - fullAdTimeBegin > fullAdShowPeriod) {
				ParachuteGunner.ExternalHandler.showFullAd(false);
				fullAdTimeBegin = 0;
				fullAdTimeShown = 0;
			}
		}
	}

	private void updateGameOver() {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					0));
			if (OverlapTester.pointInRectangle(exitGameBounds, touchPoint.x,
					touchPoint.y)) {
				game.setScreen(new MainMenuScreen(game));
			} else if (OverlapTester.pointInRectangle(retryGameBounds,
					touchPoint.x, touchPoint.y)) {
				world = new World(worldListener);
				renderer = new WorldRenderer(batcher, world);
				world.score = 0;
				state = GAME_READY;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			game.setScreen(new MainMenuScreen(game));
			return;
		}
	}

	public void draw(float deltaTime) {
		GLCommon gl = Gdx.gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		renderer.render();

		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);
		batcher.enableBlending();
		batcher.begin();
		switch (state) {
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_LEVEL_END:
			presentLevelEnd(deltaTime);
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
		batcher.end();
	}

	private void presentReady() {
		batcher.draw(Assets.ready, 160 - 192 / 2, 240 - 32 / 2, 192, 32);
	}

	private void presentRunning() {
		batcher.draw(Assets.pause, 320 - 64, 480 - 96, 64, 64);
		if (world.flameWallActive)
			batcher.draw(Assets.flameWallActive, 0, 0 + 16, 48, 48);
		else
			batcher.draw(Assets.flameWallDeactive, 0, 0 + 16, 48, 48);

		Assets.font.draw(batcher, "x" + world.powerupsHolding, 48, 36);
		Assets.font.draw(batcher, scoreString, 16, 480 - 50);
	}

	private void presentPaused() {
		batcher.draw(Assets.pauseMenu, 160 - 192 / 2, 240 - 96 / 2, 192, 96);
		Assets.font.draw(batcher, scoreString, 16, 480 - 20);
	}

	private void presentLevelEnd(float deltaTime) {
		String topText = "Keep going for";
		String bottomText = "the Highscore!";
		float topWidth = Assets.font.getBounds(topText).width;
		float bottomWidth = Assets.font.getBounds(bottomText).width;
		Assets.font.draw(batcher, topText, 190 - topWidth / 2, 480 - 100);
		Assets.font.draw(batcher, bottomText, 190 - bottomWidth / 2, 100);
	}

	private void presentGameOver() {
		batcher.draw(Assets.gameOver, 160 - 160 / 2, 240 - 96 / 2, 160, 96);
		// float scoreWidth = Assets.font.getBounds(scoreString).width;
		Assets.font.draw(batcher, congratsString, 10, 480 - 80);
		Assets.font.draw(batcher, scoreString, 10, 480 - 100);
		Assets.font.draw(batcher, "Exit", 40, 100);
		Assets.font.draw(batcher, "Continue?", 160, 100);
	}

	@Override
	public void render(float delta) {
		update(delta);
		draw(delta);
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
		if (state == GAME_RUNNING) {
			state = GAME_PAUSED;
		}
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}