package com.python4d.fumper.client;

import com.python4d.fumper.Fumper;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(320, 480);
		return cfg;
	}

	@Override
	public ApplicationListener createApplicationListener () {
		setLogLevel(LOG_DEBUG);
		return new Fumper();
	}
}