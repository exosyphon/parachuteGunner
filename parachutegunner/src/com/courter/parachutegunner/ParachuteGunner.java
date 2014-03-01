package com.courter.parachutegunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.courter.parachutegunner.screens.MainMenuScreen;

public class ParachuteGunner extends Game {
//	private static final int VIRTUAL_WIDTH = 480;
//    private static final int VIRTUAL_HEIGHT = 800;
//    private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;

//    private Camera camera;
//    private Rectangle viewport;
//    private SpriteBatch sb;
	
	boolean firstTimeCreate = true;
	public static IReqHandler ExternalHandler;
	
	public ParachuteGunner(IReqHandler irh) {
		ParachuteGunner.ExternalHandler = irh;
	}

//	public ParachuteGunner() {
//	}

	@Override
	public void create () {
//		sb = new SpriteBatch();
//      camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		// now you can use the following in your main code:
		
		Settings.checkTutorialSetting();
		Settings.load();
		Assets.load();
		Gdx.input.setCatchBackKey(true);
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();

		getScreen().dispose();
	}

	@Override
	public void render() {		
//		// update camera
//        camera.update();
//        camera.apply(Gdx.gl10);
//
//        // set viewport
//        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
//                          (int) viewport.width, (int) viewport.height);
//
//        // clear previous frame
//        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
		super.render();
	}

	@Override
	public void resize(int width, int height) {
//		// calculate new viewport
//        float aspectRatio = (float)width/(float)height;
//        float scale = 1f;
//        Vector2 crop = new Vector2(0f, 0f);
//        
//        if(aspectRatio > ASPECT_RATIO)
//        {
//            scale = (float)height/(float)VIRTUAL_HEIGHT;
//            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
//        }
//        else if(aspectRatio < ASPECT_RATIO)
//        {
//            scale = (float)width/(float)VIRTUAL_WIDTH;
//            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
//        }
//        else
//        {
//            scale = (float)width/(float)VIRTUAL_WIDTH;
//        }
//
//        float w = (float)VIRTUAL_WIDTH*scale;
//        float h = (float)VIRTUAL_HEIGHT*scale;
//        viewport = new Rectangle(crop.x, crop.y, w, h);
//	
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
