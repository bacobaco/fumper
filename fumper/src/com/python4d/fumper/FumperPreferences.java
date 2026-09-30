package com.python4d.fumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class FumperPreferences {
	 private String PREFS_NAME= new String("fumper"); ;
	 private String PREFS_HIGHSCORE= new String("high-score");
	 private String PREFS_HIGHSCORE_NAME = new String("high-score-name");;

	 private String PREFS_WEB_HIGHSCORE = "web-high-score";
	 private String PREFS_WEB_HIGHSCORE_NAME = "web-high-score-name";

	public FumperPreferences (){

    }
   
   public int getHighScore()
   {

	   //il faut ABSOLUMENT mettre dans une variable LOCAL l'objet Preference pour ANDROID...??
	   Preferences getPrefs= Gdx.app.getPreferences( PREFS_NAME );
       return getPrefs.getInteger( PREFS_HIGHSCORE,0 );
   }
   public String getHighScoreName()
   {

	   //il faut ABSOLUMENT mettre dans une variable LOCAL l'objet Preference pour ANDROID...??
	   Preferences getPrefs= Gdx.app.getPreferences( PREFS_NAME );
       return getPrefs.getString( PREFS_HIGHSCORE_NAME,"" );
   }
   public void setHighScore(int highscore,String name)
   {
	   //il faut ABSOLUMENT mettre dans une variable LOCAL l'objet Preference pour ANDROID...??
	    Preferences getPrefs= Gdx.app.getPreferences( PREFS_NAME );
	    getPrefs.putInteger ( PREFS_HIGHSCORE, highscore );
	    getPrefs.putString ( PREFS_HIGHSCORE_NAME, name );
        getPrefs.flush();
   }

   public int getWebHighScore()
   {
	   Preferences getPrefs = Gdx.app.getPreferences( PREFS_NAME );
       return getPrefs.getInteger( PREFS_WEB_HIGHSCORE, 42 );
   }
   public String getWebHighScoreName()
   {
	   Preferences getPrefs = Gdx.app.getPreferences( PREFS_NAME );
       return getPrefs.getString( PREFS_WEB_HIGHSCORE_NAME, "Baco" );
   }
   public void setWebHighScore(int highscore, String name)
   {
	    Preferences getPrefs = Gdx.app.getPreferences( PREFS_NAME );
	    getPrefs.putInteger( PREFS_WEB_HIGHSCORE, highscore );
	    getPrefs.putString( PREFS_WEB_HIGHSCORE_NAME, name );
        getPrefs.flush();
   }
   
}
