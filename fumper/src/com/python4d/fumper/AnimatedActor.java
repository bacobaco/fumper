package com.python4d.fumper;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Redéfini la fonction act de la classe Actor avec une animation récupérer via
 * la Class {@link AnimationDrawable}
 * 
 * @author {@link <a href=
 *         "http://stackoverflow.com/questions/16059578/libgdx-is-there-an-actor-that-is-animated"
 *         >stackoverflow<a/>}
 */
public class AnimatedActor extends Image {
	public final Animation<TextureRegion> anim;
	private float stateTime = 0;
	
	@SuppressWarnings("unchecked")
	public AnimatedActor(Animation anim) {
		super((TextureRegion) anim.getKeyFrame(0));
	    this.anim = anim;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		setDrawable(new TextureRegionDrawable(anim.getKeyFrame(stateTime)));
	}

	@Override
	public void act(float delta) {
		stateTime += delta;
	    super.act(delta);
	}
}