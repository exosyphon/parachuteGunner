package com.courter.parachutegunner;

public class FlameWall extends DynamicGameObject{
	public static final float FLAMEWALL_HEIGHT = 1;
	public static final float FLAMEWALL_WIDTH = 1;
	
	float stateTime = 0;
	float startYPosition;
	float speed;
	

	public FlameWall(float x, float y) {
		super(x, y, FLAMEWALL_WIDTH, FLAMEWALL_HEIGHT);
		startYPosition = y;
	}
	
	public void update(float deltaTime) {
		position.add(0, -speed);
		bounds.x = position.x - FLAMEWALL_WIDTH / 2;
		bounds.y = position.y - FLAMEWALL_HEIGHT / 2;
		
		stateTime += deltaTime;
	}

}
