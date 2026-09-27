package com.python4d.fumper;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
	ParticleEffect effect;
	Actor actor;
	Map<String, Float> scaling = new HashMap<String, Float>();

	public ParticleEffectActor(ParticleEffect effect,Actor actor) {
		this.effect = effect;
		this.actor = actor;
	    scaling.put("ScaleHighMax", effect.getEmitters().get(0).getXScale().getHighMax()); 
	    scaling.put("ScaleLowMax",  effect.getEmitters().get(0).getXScale().getLowMax());
	    scaling.put("VelocityHighMax",effect.getEmitters().get(0).getVelocity().getHighMax());
	    scaling.put("VelocityLowMax", effect.getEmitters().get(0).getVelocity().getLowMax());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		effect.draw(batch); 
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		effect.setPosition(actor.getX()+actor.getWidth()*actor.getScaleX()/5f, actor.getY()+actor.getHeight()*actor.getScaleY()/2.8f);
//		for (ParticleEmitter emitter : effect.getEmitters()) { //get the list of emitters - things that emit particles
			effect.getEmitters().get(0).getAngle().setLow(-actor.getRotation()+45,actor.getRotation()+45); //low is the minimum rotation
			effect.getEmitters().get(0).getAngle().setHigh(-actor.getRotation()+45,actor.getRotation()+45); //high is the max rotation		
//         }
		effect.update(delta); // update it
	}
	


	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		for (ParticleEmitter emitter : effect.getEmitters()) { 
			emitter.getXScale().setHigh(scaling.get("ScaleHighMax") * scale);
			emitter.getXScale().setLow(scaling.get("ScaleLowMax") * scale);
			emitter.getYScale().setHigh(scaling.get("ScaleHighMax") * scale);
			emitter.getYScale().setLow(scaling.get("ScaleLowMax") * scale);
			emitter.getVelocity().setHigh(scaling.get("VelocityHighMax") * scale);
			emitter.getVelocity().setLow(scaling.get("VelocityLowMax") * scale);
         }
	}

	public void start(){
		effect.start();
	}
	
	public void reset(){
		effect.reset();
	}

	public ParticleEffect getEffect() {
		return effect;
	}
}
