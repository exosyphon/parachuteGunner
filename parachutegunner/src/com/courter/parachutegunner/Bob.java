package com.courter.parachutegunner;


public class Bob extends DynamicGameObject {
	public static final int BOB_STATE_FALL = 1;
	public static final int BOB_STATE_HIT = 2;
	public static final float BOB_MOVE_VELOCITY = 40;
	public static final float BOB_WIDTH = 1;
	public static final float BOB_HEIGHT = 1;

	int state;
	float stateTime;
	float fallSpeed;

	public Bob (float x, float y) {
		super(x, y, BOB_WIDTH, BOB_HEIGHT);
		state = BOB_STATE_FALL;
		stateTime = 0;
	}

	public void update (float deltaTime) {
		position.add(velocity.x * deltaTime, -fallSpeed );
		bounds.x = position.x - bounds.width / 2;
		bounds.y = position.y - bounds.height / 2;

		if (velocity.y < 0 && state != BOB_STATE_HIT) {
			if (state != BOB_STATE_FALL) {
				state = BOB_STATE_FALL;
				stateTime = 0;
			}
		}

		if (position.x < 0) position.x = World.WORLD_WIDTH;
		if (position.x > World.WORLD_WIDTH) position.x = 0;
		
		if(position.y < 2) {
			position.y = 1.5f;
			velocity.y = 0;
			stateTime = 0;
		}

		stateTime += deltaTime;
	}

	public void hitSquirrel () {
		velocity.set(0, 0);
		state = BOB_STATE_HIT;
		stateTime = 0;
	}
}