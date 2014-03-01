package com.courter.parachutegunner;

public class Explosion extends GameObject{
	public static final float EXPLOSION_HEIGHT = .8f;
	public static final float EXPLOSION_WIDTH = .8f;
	
	float stateTime = 0;

	public Explosion(float x, float y) {
		super(x, y, EXPLOSION_WIDTH, EXPLOSION_HEIGHT);
	}
	
	public void update(float deltaTime)
	{
		stateTime += deltaTime;
	}

}
