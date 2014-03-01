package com.courter.parachutegunner;

public class Background extends GameObject{
	public static final float BACKGROUND_HEIGHT = 1;
	public static final float BACKGROUND_WIDTH = 1;
	
	float stateTime = 0;
	float startYPosition;
	boolean appended = false;
	

	public Background(float x, float y) {
		super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
		startYPosition = y;
	}
	
	public void setAppended(boolean x)
	{
		appended = x;
	}
}
