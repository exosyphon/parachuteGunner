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

package com.courter.parachutegunner;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {
	static final float FRUSTUM_WIDTH = 10;
	static final float FRUSTUM_HEIGHT = 15;
	World world;
	OrthographicCamera cam;
	SpriteBatch batch;

	public WorldRenderer (SpriteBatch batch, World world) {
		this.world = world;
		this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.cam.position.set(FRUSTUM_WIDTH / 2, World.WORLD_HEIGHT , 0);
		this.batch = batch;
	}

	public void render () {
		if ((world.bob.position.y < cam.position.y) && (world.bob.position.y > 8)) cam.position.y = world.bob.position.y;
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		renderObjects();
	}


	public void renderObjects () {
		batch.enableBlending();
		batch.begin();
		renderBackground();
		renderGrass();
		renderBob();
		renderItems();
		renderPowerups();
		renderExplosions();
		renderSquirrels();
		renderBullets();
		renderFlameWall();
		renderClouds();
		batch.end();
	}
	
	public void renderBackground () {
		int len = world.backgrounds.size();
		for (int i = 0; i < len; i++) {
			Background background = world.backgrounds.get(i);
			batch.draw(Assets.backgroundRegion, background.position.x, background.position.y , FRUSTUM_WIDTH,
					FRUSTUM_HEIGHT);
		}
	}

	private void renderBob () {
		TextureRegion keyFrame;
		switch (world.bob.state) {
		case Bob.BOB_STATE_FALL:
			keyFrame = Assets.bobFall.getKeyFrame(world.bob.stateTime, Animation.ANIMATION_LOOPING);
			break;
		case Bob.BOB_STATE_HIT:
		default:
			keyFrame = Assets.bobHit;
		}

		float side = world.bob.velocity.x < 0 ? 1 : -1;
		if (side < 0)
			batch.draw(keyFrame, world.bob.position.x + 0.5f, world.bob.position.y - 0.5f, side * 1, 1);
		else
			batch.draw(keyFrame, world.bob.position.x - 0.5f, world.bob.position.y - 0.5f, side * 1, 1);
		
	}

	private void renderItems () {
		int len = world.coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = world.coins.get(i);
			TextureRegion keyFrame = Assets.coinAnim.getKeyFrame(coin.stateTime, Animation.ANIMATION_LOOPING);
			batch.draw(keyFrame, coin.position.x - 0.5f, coin.position.y - 0.5f, 1, 1);
		}
	}
	
	private void renderPowerups () {
		int len = world.powerups.size();
		for (int i = 0; i < len; i++) {
			Powerup powerup = world.powerups.get(i);
			TextureRegion keyFrame = Assets.powerup;
			batch.draw(keyFrame, powerup.position.x - 0.5f, powerup.position.y - 0.5f, 1, 1);
		}
	}
	
	private void renderClouds () {
		int len = world.clouds.size();
		for (int i = 0; i < len; i++) {
			Cloud cloud = world.clouds.get(i);
			TextureRegion keyFrame = Assets.cloud;
			batch.draw(keyFrame, cloud.position.x - 0.5f, cloud.position.y - 0.5f, 1, 1);
		}
	}

	private void renderSquirrels () {
		int len = world.squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = world.squirrels.get(i);
			TextureRegion keyFrame = Assets.squirrelFly.getKeyFrame(squirrel.stateTime, Animation.ANIMATION_LOOPING);
			float side = squirrel.velocity.x < 0 ? -1 : 1;
			if (side < 0)
				batch.draw(keyFrame, squirrel.position.x + 0.5f, squirrel.position.y - 0.5f, side * 1, 1);
			else
				batch.draw(keyFrame, squirrel.position.x - 0.5f, squirrel.position.y - 0.5f, side * 1, 1);
		}
	}
	
	private void renderBullets() {
		TextureRegion keyFrame = Assets.bullet;
		int len = world.bullets.size();
		for (int i = 0; i < len; i++) {
			Bullet bullet = world.bullets.get(i);

			batch.draw(keyFrame, bullet.position.x - .5f, bullet.position.y - .6f, 1, 0.5f);
		}
	}
	
	private void renderGrass()
	{
		TextureRegion keyFrame = Assets.grass;
		int len = world.grasses.size();
		for (int i = 0; i < len; i++) {
			Grass grass = world.grasses.get(i);

			batch.draw(keyFrame, grass.position.x, grass.position.y, 1, 1);
		}
	}
	

	private void renderExplosions() {
		int len = world.explosions.size();
		for (int i = 0; i < len; i++) {
			Explosion explosion = world.explosions.get(i);
			TextureRegion keyFrame = Assets.explosionAnim.getKeyFrame(explosion.stateTime, Animation.ANIMATION_NONLOOPING);

			batch.draw(keyFrame, explosion.position.x - .5f, explosion.position.y -.5f, 1, 1);
		}
	}
	
	private void renderFlameWall() {
		TextureRegion keyFrame = Assets.flameWall;
		int len = world.flamewalls.size();
		for (int i = 0; i < len; i++) {
			FlameWall flamewall = world.flamewalls.get(i);

			batch.draw(keyFrame, flamewall.position.x - .5f, flamewall.position.y -.5f, 1, 1);
		}
	}
}