package com.courter.parachutegunner;

public class Bullet extends DynamicGameObject{
	public static final float BULLET_WIDTH = 1f;
	public static final float BULLET_HEIGHT = .5f;
	public static final int MISSILE_SPEED = 20;
	public static final int KILL_SCORE = 100;
	
	float stateTime = 0;

	enum BulletDirection {
		NOTSET, RIGHT, LEFT;
	}
	
	BulletDirection direction = BulletDirection.NOTSET;

	public Bullet(float x, float y) {
		super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
		velocity.set(MISSILE_SPEED, 0);
	}

	public void update(float deltaTime, BulletDirection bobDirection)
	{
		if(direction == BulletDirection.NOTSET)
			this.direction = bobDirection;
		
		if(direction == BulletDirection.RIGHT) // facing right 1
			position.add(velocity.x * deltaTime, -15 * deltaTime);
		else //facing left 2
			position.add(-velocity.x * deltaTime, -15 * deltaTime);
		bounds.x = position.x - BULLET_WIDTH / 2;
		bounds.y = position.y - BULLET_HEIGHT / 2;

		stateTime += deltaTime;
	}

}
