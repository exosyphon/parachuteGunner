package com.courter.parachutegunner;

public class Cloud extends GameObject{
	public static final float CLOUD_HEIGHT = 1;
	public static final float CLOUD_WIDTH = 1;
	

	public Cloud(float x, float y) {
		super(x, y, CLOUD_WIDTH, CLOUD_HEIGHT);
	}

}
