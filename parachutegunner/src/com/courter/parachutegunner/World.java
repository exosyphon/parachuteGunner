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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.badlogic.gdx.math.Vector2;
import com.courter.parachutegunner.Bullet.BulletDirection;
import com.swarmconnect.SwarmAchievement;

public class World {
	public interface WorldListener {
		public void hit();

		public void coin();

		public void kill();

		public void powerup();
	}
	
	Executor executor = Executors.newFixedThreadPool(10);
	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = WorldRenderer.FRUSTUM_HEIGHT / 2 * 50;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);
	private static final int FlameAchievement = 11161;
	private static final int pointsAchievement10000 = 11185;
	private static final int pointsAchievement25000 = 11187;
	private static final int pointsAchievement50000 = 11189;
	private static final int pointsAchievement100000 = 11191;
	private static final int pointsAchievement250000 = 11193;
	private static final int pointsAchievement500000 = 11195;
	private static final int allCoinsAchievement = 11197;
	private static final int noPowerupsAchievement = 11199;
	private static final int allEnemiesAchievement = 11201;
	private static final int bulletEnemyKillAchievement = 11203;
	private static final int easyLevelCompleted = 11205;
	private static final int medLevelCompleted = 11207;
	private static final int hardLevelCompleted = 11209;
	private static final int insaneLevelCompleted = 11211;
	private static final int allPowerupsAchievement = 11213;

	public final Bob bob;
	public final List<Squirrel> squirrels;
	public final List<Coin> coins;
	public final List<Bullet> bullets;
	public final List<Grass> grasses;
	public final List<Explosion> explosions;
	public final List<FlameWall> flamewalls;
	public final List<Powerup> powerups;
	public final List<Cloud> clouds;
	public final List<Background> backgrounds;
	public final WorldListener listener;
	public final Random rand;

	public boolean pickedUpPowerup;
	public int score;
	public float fallSpeed;
	public int state;
	public int powerupsHolding;
	public boolean flameWallActive;
	public float flameWallMaxYDistance;
	public static final float[] difficultyScoreMultiplier = { .8f, 1, 1.5f,
			2.3f };
	public static final float[] difficultyEnemyMultiplier = { 1, .7f, .5f, .1f };
	public static final float[] difficultyPowerupMultiplier = { .9f, .93f,
			.97f, 1 };
	public static final float[] difficultyCoinMultiplier = { .8f, .85f, .9f, 1 };
	public static final int LEVEL_COMPLETE_BONUS = 8000;
	private static final float fallSpeedIncrement = .012f;

	public World(WorldListener listener) {
		this.bob = new Bob(5, WORLD_HEIGHT);
		this.squirrels = new ArrayList<Squirrel>();
		this.bullets = new ArrayList<Bullet>();
		this.coins = new ArrayList<Coin>();
		this.grasses = new ArrayList<Grass>();
		this.explosions = new ArrayList<Explosion>();
		this.flamewalls = new ArrayList<FlameWall>();
		this.powerups = new ArrayList<Powerup>();
		this.clouds = new ArrayList<Cloud>();
		this.backgrounds = new ArrayList<Background>();
		this.listener = listener;
		rand = new Random();
		generateLevel();

		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
		this.flameWallActive = false;
		this.flameWallMaxYDistance = 33;
		this.powerupsHolding = 0;
		this.fallSpeed = .08f;
		this.pickedUpPowerup = false;
	}

	private void generateLevel() {
		backgrounds.add(new Background(0, WORLD_HEIGHT
				- WorldRenderer.FRUSTUM_HEIGHT / 2));
		float distanceBetweenObjs = WORLD_WIDTH * WORLD_WIDTH
				/ (2 * -gravity.y);
		addGrass();
		float y = distanceBetweenObjs;
		float xDistanceBetween = 2;
		while (y < WORLD_HEIGHT - WORLD_WIDTH / 2) {
			float x = rand.nextFloat() * (WORLD_WIDTH - xDistanceBetween)
					+ xDistanceBetween / 2;

			if (rand.nextFloat() > 0.6f) {
				Cloud cloud = new Cloud(x + rand.nextFloat(), y
						+ Cloud.CLOUD_HEIGHT + rand.nextFloat() * 2);
				clouds.add(cloud);

			}

			if (y > distanceBetweenObjs - 1
					&& rand.nextFloat() > (0.8f * difficultyEnemyMultiplier[Settings.currentDifficulty])) {
				Squirrel squirrel = new Squirrel(x + rand.nextFloat(), y
						+ Squirrel.SQUIRREL_HEIGHT + rand.nextFloat() * 2);
				squirrels.add(squirrel);
			}

			if (rand.nextFloat() > (0.6f * difficultyCoinMultiplier[Settings.currentDifficulty])) {
				Coin coin = new Coin(x + rand.nextFloat(), y + Coin.COIN_HEIGHT
						+ rand.nextFloat() * 3);
				coins.add(coin);
			}

			if (rand.nextFloat() > (0.98f * difficultyPowerupMultiplier[Settings.currentDifficulty])) {
				Powerup powerup = new Powerup(x + rand.nextFloat(), y
						+ Powerup.POWERUP_HEIGHT + rand.nextFloat() * 3);
				powerups.add(powerup);
			}

			y += (distanceBetweenObjs - 0.5f);
			y -= rand.nextFloat() * (distanceBetweenObjs / 3);
		}
	}

	private void addGrass() {
		for (int q = 0; q < WORLD_WIDTH; q++) {
			Grass grass = new Grass(q, 0.5f);
			grasses.add(grass);
		}
	}

	public void update(float deltaTime, float accelX) {
		updateBackground(deltaTime);
		updateBob(deltaTime, accelX);
		updateExplosions(deltaTime);
		updateSquirrels(deltaTime);
		updateCoins(deltaTime);
		updateBullets(deltaTime);
		updateFlameWall(deltaTime);
		if (bob.state != Bob.BOB_STATE_HIT)
			checkCollisions();
		checkGameOver();
	}

	private void updateBackground(float deltaTime) {
		int len = backgrounds.size();
		for (int i = 0; i < len; i++) {
			Background background = backgrounds.get(i);
			if ((background.position.y + WorldRenderer.FRUSTUM_HEIGHT / 2 + 1 > bob.position.y)
					&& (!background.appended)) {
				background.setAppended(true);
				float x = background.position.x;
				float y = background.position.y;
				backgrounds.add(new Background(x, y
						- WorldRenderer.FRUSTUM_HEIGHT));
			}
			if (background.position.y - WorldRenderer.FRUSTUM_HEIGHT / 2 > bob.position.y) {
				backgrounds.remove(background);
				len = backgrounds.size();
			}
		}
	}

	private void updateFlameWall(float deltaTime) {
		int len = flamewalls.size();
		for (int i = 0; i < len; i++) {
			FlameWall flamewall = flamewalls.get(i);
			if (flamewall.speed != bob.fallSpeed + fallSpeedIncrement)
				flamewall.speed = fallSpeed + fallSpeedIncrement;
			flamewall.update(deltaTime);
		}
	}

	private void updateBullets(float deltaTime) {
		BulletDirection bobDir = BulletDirection.RIGHT;
		if (bob.velocity.x < 0)
			bobDir = BulletDirection.LEFT;
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			bullet.update(deltaTime, bobDir);
		}
	}

	private void updateBob(float deltaTime, float accelX) {
		if (bob.state != Bob.BOB_STATE_HIT)
			bob.velocity.x = -accelX / 10 * Bob.BOB_MOVE_VELOCITY;
		if (bob.fallSpeed != fallSpeed)
			bob.fallSpeed = fallSpeed;
		bob.update(deltaTime);
	}

	private void updateSquirrels(float deltaTime) {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			squirrel.update(deltaTime);
		}
	}

	private void updateCoins(float deltaTime) {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			coin.update(deltaTime);
		}
	}

	private void updateExplosions(float deltaTime) {
		int len = explosions.size();
		for (int i = 0; i < len; i++) {
			Explosion explosion = explosions.get(i);
			explosion.update(deltaTime);
			if (explosion.stateTime > .8f) {
				explosions.remove(explosion);
				len = explosions.size();
			}
		}
	}

	private void checkCollisions() {
		checkBulletCollisions();
		checkFlameWallCollisions();
		checkSquirrelCollisions();
		checkItemCollisions();
		checkPowerupCollisions();
	}

	private void checkSquirrelCollisions() {
		int len = squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = squirrels.get(i);
			if (OverlapTester.overlapRectangles(squirrel.bounds, bob.bounds)) {
				bob.hitSquirrel();
				listener.hit();
			}
		}
		if (len == 0) 
			executor.execute(new MyRunnable(allEnemiesAchievement));
	}

	private void checkBulletCollisions() {
		boolean killed = false;
		int len = bullets.size();
		int len2 = squirrels.size();
		for (int y = 0; y < len2; y++) {
			Squirrel squirrel = squirrels.get(y);
			for (int i = 0; i < len; i++) {
				Bullet bullet = bullets.get(i);
				if (OverlapTester.overlapRectangles(bullet.bounds,
						squirrel.bounds)) {
					explosions.add(new Explosion(squirrel.position.x,
							squirrel.position.y));
					squirrels.remove(squirrel);
					bullets.remove(bullet);
					len = bullets.size();
					len2 = squirrels.size();
					listener.kill();
					score += (Bullet.KILL_SCORE * difficultyScoreMultiplier[Settings.currentDifficulty]);
					killed = true;
				}
			}
		}
		if (killed)
			executor.execute(new MyRunnable(bulletEnemyKillAchievement));
	}

	private void checkPowerupCollisions() {
		int len = powerups.size();
		for (int x = 0; x < len; x++) {
			Powerup powerup = powerups.get(x);
			if (OverlapTester.overlapRectangles(bob.bounds, powerup.bounds)) {
				powerups.remove(powerup);
				len = powerups.size();
				flameWallActive = true;
				listener.powerup();
				powerupsHolding += 1;
				pickedUpPowerup = true;
			}
		}
		if (len == 0)
			executor.execute(new MyRunnable(allPowerupsAchievement));
	}

	private void checkFlameWallCollisions() {
		int len = flamewalls.size();
		int len2 = squirrels.size();
		for (int y = 0; y < len2; y++) {
			Squirrel squirrel = squirrels.get(y);
			for (int i = 0; i < len; i++) {
				FlameWall flamewall = flamewalls.get(i);
				if (OverlapTester.overlapRectangles(flamewall.bounds,
						squirrel.bounds)) {
					explosions.add(new Explosion(squirrel.position.x,
							squirrel.position.y));
					squirrels.remove(squirrel);
					len2 = squirrels.size();
					listener.kill();
					score += (Bullet.KILL_SCORE * difficultyScoreMultiplier[Settings.currentDifficulty]);
				}

				if ((flamewall.startYPosition - flamewall.position.y) > flameWallMaxYDistance) {
					flamewalls.remove(flamewall);
					len = flamewalls.size();
				}
			}
		}
	}

	private void checkItemCollisions() {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			if (OverlapTester.overlapRectangles(bob.bounds, coin.bounds)) {
				coins.remove(coin);
				len = coins.size();
				listener.coin();
				score += Coin.COIN_SCORE;
			}
		}
		if (len == 0)
			executor.execute(new MyRunnable(allCoinsAchievement));
		if (bob.velocity.y > 0)
			return;

	}

	private void checkGameOver() {
		if (bob.state == Bob.BOB_STATE_HIT)
			state = WORLD_STATE_GAME_OVER;
		else if (bob.position.y < 2) {
			bob.position.y = 1.5f;
			bob.velocity.y = 0;
			score += LEVEL_COMPLETE_BONUS;
			fallSpeed = bob.fallSpeed + fallSpeedIncrement;
			powerupsHolding = 0;
			checkDifficultyAchievement();
			if (!pickedUpPowerup)
				executor.execute(new MyRunnable(noPowerupsAchievement));
			state = WORLD_STATE_NEXT_LEVEL;
		}
	}

	public void addFlameWall() {
		executor.execute(new MyRunnable(FlameAchievement));
		for (int x = 0; x < WORLD_WIDTH + 1; x++) {
			FlameWall fw = new FlameWall(x, bob.position.y - 1);
			fw.speed = fallSpeed + fallSpeedIncrement;
			flamewalls.add(fw);
		}
		powerupsHolding -= 1;
		if (powerupsHolding == 0)
			flameWallActive = false;
	}

	public void addBullet() {
		bullets.add(new Bullet(bob.position.x, bob.position.y));
	}

	public void checkPointsAchievements(long scored) {
		if (score >= 10000)
			SwarmAchievement.unlock(pointsAchievement10000);
		if (score >= 25000)
			SwarmAchievement.unlock(pointsAchievement25000);
		if (score >= 50000)
			SwarmAchievement.unlock(pointsAchievement50000);
		if (score >= 100000)
			SwarmAchievement.unlock(pointsAchievement100000);
		if (score >= 250000)
			SwarmAchievement.unlock(pointsAchievement250000);
		if (score >= 500000)
			SwarmAchievement.unlock(pointsAchievement500000);
	}

	public void checkDifficultyAchievement() {
		if (Settings.currentDifficulty == 3)
			executor.execute(new MyRunnable(insaneLevelCompleted));
		else if (Settings.currentDifficulty == 2)
			executor.execute(new MyRunnable(hardLevelCompleted));
		else if (Settings.currentDifficulty == 1)
			executor.execute(new MyRunnable(medLevelCompleted));
		else if (Settings.currentDifficulty == 0)
			executor.execute(new MyRunnable(easyLevelCompleted));
	}

	public class MyRunnable implements Runnable {
		private int m_whatToDo;

		public MyRunnable(int whatToDo) {
			m_whatToDo = whatToDo;
		}

		@Override
		public void run() {
			if (allCoinsAchievement == m_whatToDo) {
				SwarmAchievement.unlock(allCoinsAchievement);
			} else if (allEnemiesAchievement == m_whatToDo) {
				SwarmAchievement.unlock(allEnemiesAchievement);
			} else if (allPowerupsAchievement == m_whatToDo) {
				SwarmAchievement.unlock(allPowerupsAchievement);
			} else if (FlameAchievement == m_whatToDo) {
				SwarmAchievement.unlock(FlameAchievement);
			} else if (bulletEnemyKillAchievement == m_whatToDo) {
				SwarmAchievement.unlock(bulletEnemyKillAchievement);
			} else if (insaneLevelCompleted == m_whatToDo) {
				SwarmAchievement.unlock(insaneLevelCompleted);
			} else if (hardLevelCompleted == m_whatToDo) {
				SwarmAchievement.unlock(hardLevelCompleted);
			} else if (medLevelCompleted == m_whatToDo) {
				SwarmAchievement.unlock(medLevelCompleted);
			} else if (easyLevelCompleted == m_whatToDo) {
				SwarmAchievement.unlock(easyLevelCompleted);
			} else if (noPowerupsAchievement == m_whatToDo) {
				SwarmAchievement.unlock(noPowerupsAchievement);
			} else {
				return;
			}

		}
	}
}