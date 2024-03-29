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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.swarmconnect.Swarm;

public class MainMenuScreen implements Screen {
	Game game;

	OrthographicCamera guiCam;
	SpriteBatch batcher;
	Rectangle playBounds;
	Rectangle highscoresBounds;
	Rectangle optionsBounds;
	Rectangle swarmBounds;
	Vector3 touchPoint;

	public MainMenuScreen(Game game) {
		this.game = game;

		guiCam = new OrthographicCamera(320, 480);
		guiCam.position.set(320 / 2, 480 / 2, 0);
		batcher = new SpriteBatch();
		playBounds = new Rectangle(160 - 150, 170 + 18, 300, 36);
		highscoresBounds = new Rectangle(160 - 150, 170 - 18, 300, 36);
		optionsBounds = new Rectangle(160 - 150, 170 - 18 - 36, 300, 36);
		swarmBounds = new Rectangle(0, 0, 64, 64);
		touchPoint = new Vector3();

		ParachuteGunner.ExternalHandler.showAd(false);
	}

	public void update(float deltaTime) {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(),
					0));

			if (OverlapTester.pointInRectangle(playBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				if (Swarm.isLoggedIn()) {
					Settings.updateCurrentUser();

					if (Settings.showTutorial)
						game.setScreen(new TutorialScreen(game));
					else
						game.setScreen(new GameScreen(game));
				} else
					Swarm.showLogin();
				return;
			}
			if (OverlapTester.pointInRectangle(highscoresBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				// game.setScreen(new HighscoresScreen(game));

				if (Swarm.isLoggedIn())
					Swarm.showLeaderboards();
				else
					Swarm.showLogin();
				return;
			}
			if (OverlapTester.pointInRectangle(optionsBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new OptionsScreen(game));
				return;
			}
			if (OverlapTester.pointInRectangle(swarmBounds, touchPoint.x,
					touchPoint.y)) {
				Assets.playSound(Assets.clickSound);
				Swarm.showDashboard();
				return;
			}
		}
	}

	public void draw(float deltaTime) {
		GLCommon gl = Gdx.gl;
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);

		batcher.disableBlending();
		batcher.begin();
		batcher.draw(Assets.backgroundRegion, 0, 0, 320, 480);
		batcher.end();

		batcher.enableBlending();
		batcher.begin();
		batcher.draw(Assets.logo, 160 - 274 / 2, 480 - 50 - 142, 274, 142);
		batcher.draw(Assets.mainMenu, 10, 170 - 110 / 2, 300, 110);
		batcher.draw(Assets.swarmLogoRegion, 0, 0, 64, 64);
		batcher.end();
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
		Settings.save();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}