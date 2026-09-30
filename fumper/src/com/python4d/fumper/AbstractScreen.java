package com.python4d.fumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class AbstractScreen implements Screen {

	protected boolean debug=false;
	public static boolean showBox2DDebug=false;
	
	// the fixed viewport dimensions (ratio: 1.6)
	public static int GAME_VIEWPORT_WIDTH = 320,
			GAME_VIEWPORT_HEIGHT = 480;
	public static float WORLD_TO_BOX = GAME_VIEWPORT_HEIGHT/20000.0f;//0.01f;
	public static float BOX_TO_WORLD = 1.0f/WORLD_TO_BOX;
	protected float delta=60.0f;
	protected int NB_CYCLE_FOR_20S = (int) (20000.0f /delta);
	protected int NB_CYCLE_FOR_15S = (int) (15000.0f /delta);
	protected int NB_CYCLE_FOR_10S = (int) (10000.0f /delta);
	protected int NB_CYCLE_FOR_5S = (int) (5000.0f / delta);
	protected int NB_CYCLE_FOR_1S = (int) (1000.0f /delta);
	protected int NB_CYCLE_FOR_2S = (int) (2000.0f / delta);
	protected int wait = NB_CYCLE_FOR_1S;

	protected Fumper game;
	
	protected BitmapFont 	font_berlin,
							font_berlin_copyright,
							font_pecita,
							font_hennypenny,
							font_hennypenny2,
							font_goodgirl,
							font_goodgirl2,
							font_goodgirl3;

	protected  Stage stage;
	protected TypeOfObject[] tofTable = TypeOfObject.values();
	private TextureAtlas atlas;

	protected World worldbox;
	private float physicsAccumulator = 0.0f;
	private static final float FIXED_TIMESTEP = 1.0f / 60.0f;
	private static final float PHYSICS_SPEED = 1.12f;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private Matrix4 MatZoom;
	private BodyEditorLoader bodyloader = new BodyEditorLoader(
			Gdx.files.internal("texture/body/fumperbody.bd"));

	protected Toast myToast=new Toast(1,0);

	
	//definition des objets
	public enum TypeOfObject {
		citron(1,"fruits/citron","citron"),
		pomme(1,"fruits/pomme-dessin", "pomme-dessin"),
		poire(1,"fruits/poire", "poire"),
		pomme_verte(1,"fruits/pomme_verte","pomme_verte"),
		cerises(1,"fruits/cerises","cerises"),
		citrouille(1,"fruits/citrouille","citrouille"),
		pomme2(1,"fruits/pomme-dessin2", "pomme-dessin2"),
		fraise(1,"fruits/fraise","fraise"),
		orange(1,"fruits/orange","orange"),
		pomme3(1,"fruits/pomme-photo", "pomme-photo"),
		cadre(1,"splash-screen/splash-image", "splash-image"),
		helice(1,"splash-screen/helice", "helice"),
		buche(1,"bascule/buche_bois","buche"),
		planche(1,"bascule/planche","planche"),
		panier(1,"objets/panier","panier"),
		panier_front(1,"objets/panier-front",""),
		tap_finger(2,"autres/tap_finger1","autres/tap_finger2"),
		tapandplay(1,"autres/tapandplay",""),
		start(1,"autres/start",""),
		fusee(4,"objets/fusee1","objets/fusee2","objets/fusee3","objets/fusee4"),
		fruitjumper(1,"autres/fruitjumper",""),
		splash(1,"autres/splash",""),
		oiseau(8,"oiseau/bird2","oiseau/bird1","oiseau/bird5","oiseau/bird3","oiseau/bird4","oiseau/bird3","oiseau/bird5","oiseau/bird1");
		
		String[] st;
		private int nb_images;
		private static int NB_FRUITS=10;

		TypeOfObject(int nb_images,String ... st) {
			this.st = st;
			this.nb_images=nb_images;

		}

		public String getNameImage() {
			return st[0];
		}
		public String[] getAllImages() {
			return st;
		}
		public int getNbImages(){
			return nb_images;
		}
		
		public String getNameBody() {
			return st[nb_images];
		}

		/**
		 * @return le nb de fruits possible dans la banque d'image
		 */
		public static int getNbFruits() {
			return NB_FRUITS;
		}

	}
	//definition des sons
	public enum FumperSound
	{	//  Name in ASSETS and use it as music or sound(false)
	    spaceship( "sons/spaceship.mp3",false),
	    INTRO( "sons/intro.mp3",true),
	    bascule( "sons/bascule.mp3",false),
	    bird( "sons/bird.mp3",false),
	    fruit_bascule( "sons/fruit_bascule.mp3",false);

	    private String fileName;
	    private boolean music;
	    private Sound snd=null;
		private Music msc=null;
	    private long id;
		
	    private FumperSound(
	        String fileName, boolean music)
	    {
	        this.fileName = fileName;
	        this.music=music;
	    }

	    public String getFileName()
	    {
	        return fileName;
	    }
	    /**
	     * load en mémoire le son ou music
	     */
	    public void load(){
	    	if (music)
	    		msc=Gdx.audio.newMusic(Gdx.files.internal(fileName));
	    	else
	    		snd=Gdx.audio.newSound(Gdx.files.internal(fileName));
	    }
	    public Music getMusic() {
			return msc;
		}

		/**
	     * Joue un son/music, le charge via load si 'null'
	     * @param volume (float)
	     * @param looping (boolean)
	     */
	    public void play(float volume,boolean looping){
	    	if (music){
	    		if (msc==null)
	    			load();
	    		msc.setLooping(looping);
	    		msc.setVolume(volume);
	    		msc.play();
	    	}
	    	else
	    	{
		    	if (snd==null)
		    		load();
		    	id=snd.play(volume);
		    	snd.setLooping(id, looping);
	    	}
	    }
	    

		public Sound getSnd() {
			return snd;
		}
	    /**
	     * Joue le son/music sans loop volume max.
	     */
	    public void play(){
	    	play(1.0f,false);
	    }
	    /**
	     * Joue le son/music sans loop.
	     * 
	     * @param volume
	     */
	    public void play(float volume){
	    	play(volume,false);
	    }
	    public void dispose(){
	    	if (music){
		    	msc.dispose();
	    		msc=null;
	    	}
	    	else{
	    		snd.dispose();
	    		snd=null;
	    	}
	    }
	}
	public BodyEditorLoader getBodyLoader() {
		return bodyloader;
	}

	public AbstractScreen(Fumper game) {
		this.game = game;

		this.font_berlin_copyright = new BitmapFont(Gdx.files.internal("font/berlin.fnt"),
		         Gdx.files.internal("font/berlin.png"), false);
		this.font_berlin = new BitmapFont(Gdx.files.internal("font/berlin.fnt"),
		         Gdx.files.internal("font/berlin.png"), false);
		this.font_hennypenny= new BitmapFont(Gdx.files.internal("font/HennyPenny.fnt"),
		         Gdx.files.internal("font/HennyPenny.png"), false);
		this.font_hennypenny2= new BitmapFont(Gdx.files.internal("font/HennyPenny.fnt"),
		         Gdx.files.internal("font/HennyPenny.png"), false);
		this.font_pecita= new BitmapFont(Gdx.files.internal("font/pecita.fnt"),
		         Gdx.files.internal("font/pecita.png"), false);
		this.font_goodgirl= new BitmapFont(Gdx.files.internal("font/goodgirl.fnt"),
		         Gdx.files.internal("font/goodgirl.png"), false);
		this.font_goodgirl2= new BitmapFont(Gdx.files.internal("font/goodgirl.fnt"),
		         Gdx.files.internal("font/goodgirl.png"), false);
		this.font_goodgirl3= new BitmapFont(Gdx.files.internal("font/goodgirl.fnt"),
		         Gdx.files.internal("font/goodgirl.png"), false);
		this.stage = new Stage();
	}

	protected String getName() {
		return "";
		//return getClass().getSimpleName();
	}

	public World getWorldbox() {
		if (worldbox == null) {
			worldbox = new World(new Vector2(0, -9.8f), true);
			World.setVelocityThreshold(0.0f);

			debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
		}
		return worldbox;
	}

	public TextureAtlas getAtlas() {
		if (atlas == null) {
			atlas = new TextureAtlas(Gdx.files.internal("texture/Fumper.atlas"));
		}
		return atlas;
	}

	@Override
	public void render(float delta) {
		// the following code clears the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		
		// update and draw the stage actors
		stage.act(delta);
		stage.draw();

		if (myToast!=null)
			myToast.toaster();
		//calcul du monde Box
		if (worldbox != null) {
			float frameTime = Math.min(delta, 0.1f) * PHYSICS_SPEED;
			physicsAccumulator += frameTime;
			int steps = 0;
			while (physicsAccumulator >= FIXED_TIMESTEP && steps < 5) {
				worldbox.step(FIXED_TIMESTEP, 8, 3);
				physicsAccumulator -= FIXED_TIMESTEP;
				steps++;
			}
			MatZoom=camera.combined.cpy();
			if (debug || showBox2DDebug)
				debugRenderer.render(worldbox, MatZoom.scl(BOX_TO_WORLD));
		}
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("Fumper/AbstractScreen/resize/", "Resizing screen: " + getName() + " to: "
				+ width + " x " + height);

		// resize the stage
		stage.getViewport().update(width, height, false);
		
		// Le monde Box2D ne doit pas changer
		WORLD_TO_BOX = GAME_VIEWPORT_HEIGHT/20000.0f*GAME_VIEWPORT_WIDTH/(float)width;
		BOX_TO_WORLD=1.0f/WORLD_TO_BOX;
		
		MatZoom = new Matrix4();			
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		Gdx.app.log("Fumper/show/", "Showing screen: " + getName());

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		Gdx.app.log("Fumper/abstracScreen/dispose", "Disposing screen: " + getName());

		// dispose the collaborators
		if (atlas!=null) atlas.dispose();
		if (worldbox!=null) worldbox.dispose();
		if (stage!=null) stage.dispose();
		if (font_berlin!=null) font_berlin.dispose();
		if (font_pecita!=null) font_pecita.dispose();

	}

}
