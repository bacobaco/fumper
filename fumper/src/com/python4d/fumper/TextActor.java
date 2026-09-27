package com.python4d.fumper;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TextActor extends Actor {
	private BitmapFont font;
	private CharSequence str;
	private GlyphLayout layout = new GlyphLayout();

	public TextActor(BitmapFont font, CharSequence str) {
		this.font = font;
		setText(str.toString());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		font.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
		font.draw(batch, str, this.getX(), this.getY() + this.getHeight());
	}

	@Override
	public void setScale(float scale) {
		super.setScale(1);
		font.getData().setScale(scale);
		layout.setText(font, str);
		this.setBounds(getX(), getY(), layout.width, layout.height);
	}

	@Override
	public void setScale(float scalex, float scaley) {
		super.setScale(1);
		font.getData().setScale(scalex, scaley);
		layout.setText(font, str);
		this.setBounds(getX(), getY(), layout.width, layout.height);
	}

	@Override
	public void scaleBy(float scaleX, float scaleY) {
		super.scaleBy(scaleX, scaleY);
		layout.setText(font, str);
		float w = layout.width;
		float h = layout.height;
		font.getData().scaleX += scaleX;
		font.getData().scaleY += scaleY;
		layout.setText(font, str);
		float W = layout.width;
		float H = layout.height;
		this.setBounds(getX() - (W - w) / 2f, getY() - (H - h) / 2f, W, H);
	}

	protected void setText(String string) {
		this.str = string;
		layout.setText(font, str);
		this.setBounds(getX(), getY(), layout.width, layout.height);
	}

	public BitmapFont getFont() {
		return font;
	}

	public int getStrWidth() {
		layout.setText(font, str);
		return (int) layout.width;
	}

	public float getStrHeight() {
		layout.setText(font, str);
		return layout.height;
	}
}
