package com.python4d.fumper;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.python4d.fumper.AbstractScreen.TypeOfObject;

public class Bascule{

	private AbstractScreen screen; 
	private Image imgBuche,imgPlanche;
	private long lasttime_push=0;

	private Body bodyBuche,bodyPlanche;
	public Bascule(AbstractScreen screen, float Posx, float Posy, float size) {
		this.screen=screen;
		lasttime_push=TimeUtils.millis();
		CreateBascule(Posx, Posy,size);
		//bodyBuche.setAwake(false);
		imgPlanche.addListener(new InputListener() {
		 	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log("Fumper/Bascule/imgPlache Actor", "touch started at (" + x + ", " + y + ")");
				return false;
		 	}
		 
		 	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log("Fumper/Bascule/imgPlache Actor", "touch done at (" + x + ", " + y + ")");
		 	}
		 });
	}

	public Image getImgBuche() {
		return imgBuche;
	}
	public Image getImgPlanche() {
		return imgPlanche;
	}

	public Body getBodyPlanche() {
		return bodyPlanche;
	}

	private Body CreateBascule(float Posx, float Posy, float size) {


		final TypeOfObject buche = TypeOfObject.buche;
		final TypeOfObject planche = TypeOfObject.planche;

		AtlasRegion bgRegion = screen.getAtlas().findRegion(buche.getNameImage());
		Drawable bgDrawable = new TextureRegionDrawable(bgRegion);
		imgBuche = new Image(bgDrawable);
		imgBuche.setScale(size);
		imgBuche.setPosition(Posx, Posy);
		screen.stage.addActor(imgBuche);
		
		float PosxW = (imgBuche.getX()) * AbstractScreen.WORLD_TO_BOX;
		float PosyW = (imgBuche.getY()) * AbstractScreen.WORLD_TO_BOX;
		// Create our body definition
		BodyDef bdBuche = new BodyDef();
		// Set its world position
		bdBuche.position.set(new Vector2(PosxW, PosyW));
		bdBuche.type = BodyType.StaticBody;
		// Create a body from the definition and add it to the world
		 bodyBuche = screen.getWorldbox().createBody(bdBuche);
		FixtureDef fdBuche = new FixtureDef();
		fdBuche.friction = 0.50f;
		fdBuche.restitution = 0.3f;
		fdBuche.density = 1.0f;
		screen.getBodyLoader().attachFixture(
				bodyBuche,
				buche.getNameBody(),
				fdBuche,
				imgBuche.getWidth() * imgBuche.getScaleX()
						* AbstractScreen.WORLD_TO_BOX);
		bodyBuche.setUserData(imgBuche);
		
		
		bgRegion = screen.getAtlas().findRegion(planche.getNameImage());
		bgDrawable = new TextureRegionDrawable(bgRegion);
		imgPlanche = new Image(bgDrawable);
		imgPlanche.setScale(size*3);
	
		screen.stage.addActor(imgPlanche);

		float instable = 1.2f;
		float anchorAx = PosxW + imgBuche.getWidth() * imgBuche.getScaleX() * AbstractScreen.WORLD_TO_BOX / 2f;
		float anchorAy = PosyW + imgBuche.getHeight() * imgBuche.getScaleY() * AbstractScreen.WORLD_TO_BOX;
		float anchorBx = imgPlanche.getWidth() * imgPlanche.getScaleX() * AbstractScreen.WORLD_TO_BOX / 2f * instable;
		float anchorBy = imgPlanche.getHeight() * imgPlanche.getScaleY() * AbstractScreen.WORLD_TO_BOX / 2f;

		// Create bodyPlanche directly at its resting joint position in world coordinates
		BodyDef bd = new BodyDef();
		bd.position.set(anchorAx - anchorBx, anchorAy - anchorBy);
		bd.type = BodyType.DynamicBody;
		bodyPlanche = screen.getWorldbox().createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.friction = 0.2f;
		fd.restitution = 0.2f;
		fd.density = 5.0f;
		screen.getBodyLoader().attachFixture(
				bodyPlanche,
				planche.getNameBody(),
				fd,
				imgPlanche.getWidth() * imgPlanche.getScaleX()
						* AbstractScreen.WORLD_TO_BOX);

		RevoluteJointDef jd = new RevoluteJointDef();
		jd.enableLimit = true;
		jd.lowerAngle = -30 * MathUtils.degreesToRadians;
		jd.upperAngle = 10 * MathUtils.degreesToRadians;
		jd.bodyA = bodyBuche;
		jd.bodyB = bodyPlanche;
		jd.collideConnected = false;
		jd.localAnchorA.set(anchorAx - PosxW, anchorAy - PosyW);
		jd.localAnchorB.set(anchorBx, anchorBy);
		screen.getWorldbox().createJoint(jd);
		bodyPlanche.setUserData(imgPlanche);

		imgPlanche.setPosition(bodyPlanche.getPosition().x * AbstractScreen.BOX_TO_WORLD,
				bodyPlanche.getPosition().y * AbstractScreen.BOX_TO_WORLD);

		return bodyPlanche;
	}
	public void RemoveCatapult(){
		imgBuche.remove();
		imgBuche=null;
		imgPlanche.remove();
		imgPlanche=null;
		screen.getWorldbox().destroyBody(bodyBuche);
		bodyBuche=null;
		screen.getWorldbox().destroyBody(bodyPlanche);
		bodyPlanche=null;
		
	}

	public void update() {
		imgBuche.setPosition(bodyBuche.getPosition().x
				* AbstractScreen.BOX_TO_WORLD, bodyBuche.getPosition().y
				* AbstractScreen.BOX_TO_WORLD);
		imgPlanche.setPosition(bodyPlanche.getPosition().x
				* AbstractScreen.BOX_TO_WORLD, bodyPlanche.getPosition().y
				* AbstractScreen.BOX_TO_WORLD);
		imgPlanche.setRotation(bodyPlanche.getAngle()
				* MathUtils.radiansToDegrees);
		
	}

	public void push(int level) {
		if (TimeUtils.millis()-lasttime_push>100){
			lasttime_push=TimeUtils.millis();
			float forceY=(float) -Math.pow(bodyPlanche.getMass(), 1.90)*(1+level/100.0f);//-bodyPlanche.getMass()*bodyPlanche.getMass();//
			Gdx.app.log("Fumper/Bascule/push()",": forceY="+forceY);
			bodyPlanche.applyAngularImpulse(forceY, true);
			//bodyPlanche.applyTorque(forceY, true);
		}			
	}
}
