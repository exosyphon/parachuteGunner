package com.courter.parachutegunner;

import java.util.Map;

import android.R.color;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.courter.parachutegunner.ParachuteGunner;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmAchievement.GotAchievementsMapCB;
import com.swarmconnect.SwarmActiveUser;
import com.swarmconnect.SwarmLeaderboard;
import com.swarmconnect.SwarmLeaderboard.GotLeaderboardCB;
import com.swarmconnect.delegates.SwarmLoginListener;

public class ParachuteGunnerAndroid extends AndroidApplication {
	protected AdView adView; // small ad
	protected AdView fullAdView; // full ad
	private final String ADCODE = "a1513edbf6e032e";

	protected static final int LEADERBOARD_ID = 7053;
	public static SwarmLeaderboard leaderboards;

	Map<Integer, SwarmAchievement> achievements;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;

		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		RelativeLayout layout = new RelativeLayout(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		adView = new AdView(this, AdSize.SMART_BANNER, ADCODE);
		fullAdView = new AdView(this, AdSize.IAB_MRECT, ADCODE);

		View gameView = initializeForView(new ParachuteGunner(
				new RequestHandler(adView, fullAdView)), cfg);

		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
		AdRequest fadReq = new AdRequest();
		fullAdView.loadAd(fadReq);

		// Add the libgdx view
		layout.addView(gameView);

		// Add the AdMob view
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		RelativeLayout.LayoutParams fadParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		fadParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		layout.addView(adView, adParams);
		layout.addView(fullAdView, fadParams);
		fullAdView.setVisibility(View.GONE);
		adView.setVisibility(View.GONE);

		// Hook it all up
		setContentView(layout);
		// }

		// @Override
		// public void onCreate(Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		//
		// AndroidApplicationConfiguration cfg = new
		// AndroidApplicationConfiguration();
		// cfg.useGL20 = true;
		//
		// initialize(new ParachuteGunner(), cfg);

		// Swarm
		// If the user has logged in at least once before, then
		// automatically login the user without showing the home screen.
		if (Swarm.isEnabled()) {
			Swarm.init(ParachuteGunnerAndroid.this, 4829,
					"f42ece02cb95aed1006e79505afb6d83", mySwarmLoginListener);
		} else {
			SwarmInitiate();
		}

	}

	// Swarm
	@Override
	public void onResume() {
		super.onResume();
		try {
			Swarm.setActive(this);
		} catch (Exception e) {
		}
	}

	// Swarm
	@Override
	public void onPause() {
		super.onPause();
		try {
			Swarm.setInactive(this);
		} catch (Exception e) {
		}
	}

	// Swarm
	private SwarmLoginListener mySwarmLoginListener = new SwarmLoginListener() {

		// This method is called when the login process has started
		// (when a login dialog is displayed to the user).
		public void loginStarted() {
		}

		// This method is called if the user cancels the login process.
		public void loginCanceled() {
		}

		// This method is called when the user has successfully logged in.
		public void userLoggedIn(SwarmActiveUser user) {

			// Load our Leaderboard
			try {
				SwarmLeaderboard.getLeaderboardById(LEADERBOARD_ID,
						new GotLeaderboardCB() {
							public void gotLeaderboard(SwarmLeaderboard lb) {
								leaderboards = lb;
							}
						});
			} catch (Exception e) {
			}

			try {
				SwarmAchievement.getAchievementsMap(callback);
			} catch (Exception e) {
			}
		}

		// This method is called when the user logs out.
		public void userLoggedOut() {
		}

	};

	// Swarm
	public boolean IsSwarmInitiated() {
		return Swarm.isInitialized();
	}

	// // Swarm
	public void SwarmInitiate() {

		// Ensure it runs on UI thread
		ParachuteGunnerAndroid.this.runOnUiThread(new Runnable() {
			public void run() {
				try {
					if (!Swarm.isInitialized()) {
						Swarm.init(ParachuteGunnerAndroid.this, 4829,
								"f42ece02cb95aed1006e79505afb6d83",
								mySwarmLoginListener);
					}
				} catch (Exception e) {
				}
			}
		});

	}

	GotAchievementsMapCB callback = new GotAchievementsMapCB() {

		public void gotMap(Map<Integer, SwarmAchievement> achievements) {
			// Store the map of achievements somewhere to be used later.
			ParachuteGunnerAndroid.this.achievements = achievements;
		}
	};
	//
	// public void SwarmUnlockAchievement(int AchievementID) {
	// // Make sure that we have our achievements map.
	// if (ParachuteGunnerAndroid.this.achievements != null) {
	//
	// // Grab the achievement from our map.
	// SwarmAchievement achievement = ParachuteGunnerAndroid.this.achievements
	// .get(AchievementID);
	//
	// // No need to unlock more than once…
	// if (achievement != null) {
	// try {
	// achievement.isUnlocked(AchievementID, callback2);
	// }
	// catch(Exception e)
	// {
	// }
	// if(!unlockedAchievement)
	// achievement.unlock();
	// }
	// }
	// }

}