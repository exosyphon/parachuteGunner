package com.courter.parachutegunner;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.courter.parachutegunner.IReqHandler;
import com.google.ads.AdView;

public class RequestHandler implements IReqHandler {

	protected AdView adV;
	protected AdView fadV;
	protected int adHeight = 50;

	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;
	private final int SHOW_FULL_ADS = 3;
	private final int HIDE_FULL_ADS = 4;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ADS: {
				adV.setVisibility(View.VISIBLE);
				break;
			}
			case HIDE_ADS: {
				adV.setVisibility(View.GONE);
				break;
			}
			case SHOW_FULL_ADS: {
				fadV.setVisibility(View.VISIBLE);
				break;
			}
			case HIDE_FULL_ADS: {
				fadV.setVisibility(View.GONE);
				break;
			}
			}
		}
	};

	public void showFullAd(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_FULL_ADS : HIDE_FULL_ADS);
	}

	public RequestHandler(AdView adView, AdView fad) {
		this.adV = adView;
		this.fadV = fad;
	}

	public void showAd(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
}
