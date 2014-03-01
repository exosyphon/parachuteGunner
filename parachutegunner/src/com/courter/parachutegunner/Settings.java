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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.swarmconnect.Swarm;

public class Settings {
	public static boolean soundEnabled;
	public final static long[] highscores = new long[] { 100, 70, 50, 30, 20 };
	public static final String PREFS_NAME = "parachutegunner-prefs";
	public static boolean showTutorial;
	public static String firstStartup;
	public static int userid;

	public enum Difficulty {
		EASY, MEDIUM, HARD, INSANE
	}

	public static int currentDifficulty = 0;
	public static String[] difficulties = {
			Settings.Difficulty.EASY.toString(),
			Settings.Difficulty.MEDIUM.toString(),
			Settings.Difficulty.HARD.toString(),
			Settings.Difficulty.INSANE.toString() };

	public static void checkTutorialSetting() {
		Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
		String test = prefs.getString("firstStartup", "true");
		if (test.equals("true")) {
			prefs.putBoolean("tutorialSetting", true);
			prefs.putBoolean("soundSetting", true);

			prefs.flush();
		}

	}

	public static void load() {
		Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
		soundEnabled = prefs.getBoolean("soundSetting");
		currentDifficulty = prefs.getInteger("difficultySetting");

		for (int i = 0; i < 5; i++) {
			highscores[i] = prefs.getLong(Integer.toString(i));
		}
		showTutorial = prefs.getBoolean("tutorialSetting");

		firstStartup = prefs.getString("firstStartup", "false");

		userid = prefs.getInteger("userid");
		prefs.flush();
	}

	public static void save() {
		Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
		prefs.putBoolean("soundSetting", soundEnabled);
		prefs.putInteger("difficultySetting", currentDifficulty);

		for (int i = 0; i < 5; i++) {
			prefs.putLong(Integer.toString(i), highscores[i]);
		}
		prefs.putBoolean("tutorialSetting", showTutorial);

		prefs.putString("firstStartup", "false");

		prefs.putInteger("userid", userid);
		prefs.flush();

	}

	public static void addScore(long score) {
		for (int i = 0; i < 5; i++) {
			if (highscores[i] < score) {
				for (int j = 4; j > i; j--)
					highscores[j] = highscores[j - 1];
				highscores[i] = score;
				break;
			}
		}
	}

	public static void incrementDifficulty() {
		currentDifficulty = (currentDifficulty + 1) % 4;
	}

	public static void resetShowTutorial() {
		showTutorial = !showTutorial;
	}

	private static void clearScores() {
		for (int i = 0; i < 5; i++) {
			highscores[i] = 0;
		}
	}
	
	public static void updateCurrentUser() {
		Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
		userid = prefs.getInteger("userid", Swarm.user.userId);
		if (Swarm.user.userId != userid) {
			prefs.putInteger("userid", Swarm.user.userId);
			clearScores();
			prefs.flush();
		}
	}
}