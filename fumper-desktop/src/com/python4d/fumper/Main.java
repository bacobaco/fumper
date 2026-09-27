package com.python4d.fumper;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;

public class Main {
	public static void main(String[] args) {
		new SharedLibraryLoader().load("gdx-box2d");

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "fumper";
		cfg.width = 640;
		cfg.height = 960;

		
		new LwjglApplication(new Fumper(), cfg);
	}
}
