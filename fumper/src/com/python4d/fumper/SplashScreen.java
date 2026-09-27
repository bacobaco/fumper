package com.python4d.fumper;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class SplashScreen extends AbstractScreen {

	private AnimatedActor tapFingerActor;
	private Image splashImage, bgImage, fruitjumperImage;
	private Body splashBody;
	private Array<Fruit> pommes = new Array<Fruit>();
	private boolean quit = false;
	private TextActor textActor;
	public SplashScreen(Fumper game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();
		FumperSound.INTRO.play(1.0f, true);
		AtlasRegion bgRegion = getAtlas().findRegion("background/background");
		Drawable bgDrawable = new TextureRegionDrawable(bgRegion);
		bgImage = new Image(bgDrawable);
		bgImage.setFillParent(true);
		bgImage.getColor().a = 0.8f;
		stage.addActor(bgImage);

		AtlasRegion splashRegion = getAtlas().findRegion(TypeOfObject.helice.getNameImage());
		Drawable splashDrawable = new TextureRegionDrawable(splashRegion);
		splashImage = new Image(splashDrawable);
		stage.addActor(splashImage);

		textActor = new TextActor(font_pecita, "\n\n JUMPER");
		textActor.setZIndex(9);
		//stage.addActor(textActor);
		
		fruitjumperImage=new Image(new TextureRegionDrawable(getAtlas().findRegion(TypeOfObject.fruitjumper.getNameImage())));
		stage.addActor(fruitjumperImage);

		Array<AtlasRegion> arrayTapFinger=new Array<AtlasRegion>();
		for (int i=0;i<TypeOfObject.tap_finger.getNbImages();i++){
			arrayTapFinger.add(getAtlas().findRegion(TypeOfObject.tap_finger.getAllImages()[i]));
		}
		Animation tapFinger=new Animation(0.5f,arrayTapFinger,Animation.PlayMode.LOOP);
		tapFingerActor=new AnimatedActor(tapFinger);
		stage.addActor(tapFingerActor);
		
		Gdx.input.setInputProcessor(stage);
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("Fumper/ClickListener/", "button clicked");
				worldbox.setGravity(new Vector2(0, -9.8f));
				game.setScreen(new PlayScreen(game));
				FumperSound.INTRO.getMusic().pause();
				dispose();
			}

		});
		stage.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					if (!myToast.makeText("Back Again to Quit", font_berlin, 1f))
						quit = true;
				}
				return super.keyDown(event, keycode);
			}

		});
		
		
	}

	// Utilisation de render pour repositionner les images si issues du monde
	// world
	@Override
	public void render(float delta) {
		super.render(delta);
		if (pommes.size < 10) {
			pommes.add(new Fruit(this, tofTable[new Random().nextInt(TypeOfObject.getNbFruits())], 0.1f));
			int x = new Random().nextInt(Gdx.graphics.getWidth());
			int y = new Random().nextInt(100) + Gdx.graphics.getHeight();
			int angle = new Random().nextInt(360);
			pommes.peek().ResetPosition(x, y, angle);
			pommes.peek().setZIndex(99);
			stage.addActor(pommes.peek());
		}
		for (Fruit i : pommes) {
			i.setPosition(i.getBody().getPosition().x
					* AbstractScreen.BOX_TO_WORLD, i.getBody().getPosition().y
					* AbstractScreen.BOX_TO_WORLD);
			i.setRotation(i.getBody().getAngle() * MathUtils.radiansToDegrees);
		}
		splashImage.setPosition(splashBody.getPosition().x
				* AbstractScreen.BOX_TO_WORLD, splashBody.getPosition().y
				* AbstractScreen.BOX_TO_WORLD);
		splashImage.setRotation(splashBody.getAngle()
				* MathUtils.radiansToDegrees);
		if (wait-- < 0) {
			int index = 0;
			for (Fruit i : pommes) {
				// Quels fruits détruire?
				if (i.getBody().getPosition().x * AbstractScreen.BOX_TO_WORLD < -100
						|| i.getBody().getPosition().y
								* AbstractScreen.BOX_TO_WORLD < -100
						&& !worldbox.isLocked()) {
					worldbox.destroyBody(i.getBody());
					i.remove();
					pommes.removeIndex(index);
				}

				index++;
			}
			wait = NB_CYCLE_FOR_1S;
			Gdx.app.log("Fumper/SplashScreen/Render:",
					"AccX=" + Gdx.input.getAccelerometerX() + "AccY="
							+ Gdx.input.getAccelerometerY() + "AccZ="
							+ Gdx.input.getAccelerometerZ() + "/pitch="
							+ (int) Gdx.input.getPitch() + "/azimut="
							+ (int) Gdx.input.getAzimuth() + "/roll="
							+ (int) Gdx.input.getRoll());
			worldbox.setGravity(new Vector2(-Gdx.input.getAccelerometerX(), -9.8f));
		}
		if (quit)
			game.quit(game.getScreen());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		

		// Taille Image et Création du Body
		if (splashBody != null) {
			worldbox.destroyBody(splashBody);
		}
		splashImage.setSize(width /2f,	(width /2f) * (splashImage.getHeight())/ splashImage.getWidth());
		splashBody = CreateSplashBody(width/1.4f,height/2.2f );

		textActor.setScale(width/500.0f);
		textActor.setPosition(0, Gdx.graphics.getHeight());
		
		//Resize the fruitjumper
		fruitjumperImage.setZIndex(1);
		fruitjumperImage.setScale(height/600.0f);
		fruitjumperImage.setPosition((width)/50.0f,
				Gdx.graphics.getHeight()/20.0f);
		
		//resize start hand 
		tapFingerActor.setScale(width/300.0f);
		tapFingerActor.setPosition(Gdx.graphics.getWidth()/1.8f,
				Gdx.graphics.getHeight()/20.0f);
	}

	protected Body CreateSplashBody(float Posx, float Posy) {
		Posx = Posx * AbstractScreen.WORLD_TO_BOX;
		Posy = Posy * AbstractScreen.WORLD_TO_BOX;
		final TypeOfObject too = TypeOfObject.helice;

		// Create our body definition
		BodyDef bd = new BodyDef();
		// Set its world position
		bd.position.set(new Vector2(Posx, Posy));
		bd.type = BodyType.DynamicBody;
		// Create a body from the definition and add it to the world
		Body body = getWorldbox().createBody(bd);
		FixtureDef fd = new FixtureDef();
		fd.friction = 0.90f;
		fd.restitution = 0.6f;
		fd.density = 1f;
		getBodyLoader().attachFixture(body, too.getNameBody(), fd,
				splashImage.getWidth() * AbstractScreen.WORLD_TO_BOX);
		Body groundbody = worldbox.createBody(new BodyDef());
		RevoluteJointDef jd = new RevoluteJointDef();
		//jd.enableLimit = true;
		//jd.lowerAngle = -30 * MathUtils.degreesToRadians;
		//jd.upperAngle = 30 * MathUtils.degreesToRadians;
		jd.bodyA = groundbody;
		jd.bodyB = body;
		jd.collideConnected = false;

		jd.localAnchorA.add(Posx ,Posy );
		jd.localAnchorB.add(body.getLocalCenter().x, body.getLocalCenter().y);
		worldbox.createJoint(jd);

		return body;
	}

	@Override
	public void dispose() {
		super.dispose();

		if (FumperSound.INTRO != null) 
			FumperSound.INTRO.dispose();
	}
}