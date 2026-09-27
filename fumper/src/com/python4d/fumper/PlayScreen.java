package com.python4d.fumper;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class PlayScreen extends AbstractScreen {
	private enum GamePart {
		DEBUT, FIN, EN_COURS, LEVEL_END, NEW_LEVEL, NEW_FRUIT, QUIT, GAMEOVER, BONUS_TIME, BONUS_TIME_END
	};
	
	private static String copyright="(c) BacoLand/Python4D - 9/2014 - v1.05";
	private boolean superman=false;
	
	ParticleEffectActor effectActor;
	private GamePart examGamePart = GamePart.DEBUT;
	private Image bgImage, tapandplayActor,splash;
	private Bascule bascule;
	private Array<Fruit> fruits = new Array<Fruit>();
	private Array<Image> imageFruits = null;

	public Array<Fruit> getFruits() {
		return fruits;
	}

	private boolean start_ok = false;
	private int level = 1;
	private int nb_fruits = 10;

	private int score = 0;
	private int lastlevel_score = 0;
	private int highscore = 0, highscoreWEB = -1;
	private String highscore_name = new String("!wait for local info!");
	private String highscoreWEB_name = new String("!wait for web info!");
	private boolean bonus_flag=false;
	
	private AnimatedActor fusee, oiseau;
	private Panier panier = null;
	private TextActor textCopyright,textScore, textHighScore, textGameOver, textStart,textBonus, textInvincible;
	private Timer scoreTimer;
	private boolean promptShown = false;
	private int birdClickCount = 0;
	private long lastBirdClickTime = 0;
	private long lastBirdClickTimestamp = 0;
	private boolean invincible = false;
	private boolean invincibleUsedInSession = false;
	private int fruitsInBasketThisLevel = 0;

	public PlayScreen(Fumper game) {
		super(game);
		highscore = game.FumperPrefs.getHighScore();
		highscore_name = new String(game.FumperPrefs.getHighScoreName());

		// Timer pour rafraichir le high score web toutes les 30 secondes
		scoreTimer = new Timer();
		scoreTimer.scheduleTask(new Task() {
			@Override
			public void run() {
				refreshWebScore();
			}
		}, 0, 30);
	}

	/**
	 * Boite de dialog de base pour enregistrer le nom WEB du farmer vainqueur
	 */
	private TextInputListener HighScoreWebBox = new TextInputListener() {

		@Override
		public void input(String text) {
			if (text != null && !text.trim().isEmpty()) {
				highscoreWEB_name = text.trim();
			}

			OnlineScoreService.submitHighScore(highscoreWEB_name, highscore, new OnlineScoreService.SubmitCallback() {
				@Override
				public void onSuccess() {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							highscoreWEB = highscore;
							myToast.makeText("HighScore WEB saved!", font_berlin, 5f);
						}
					});
				}

				@Override
				public void onError(final String error) {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							myToast.makeText("WebScore error: " + error, font_berlin, 5f);
						}
					});
				}
			});
		}

		@Override
		public void canceled() {
			myToast.makeText("Canceled? What a Strange Farmer...", font_berlin,
					5f);
		}

	};

	/**
	 * Boite de dialog de base pour enregistrer le nom du farmer vainqueur
	 */

	@SuppressWarnings("unused")
	private TextInputListener listener = new TextInputListener() {

		@Override
		public void input(String text) {
			highscore_name = text.toString();
			game.FumperPrefs.setHighScore(score, highscore_name);
		}

		@Override
		public void canceled() {
			game.FumperPrefs.setHighScore(score, highscore_name);
		}

	};

	@Override
	public void show() {
		super.show();
		FumperSound.bascule.load();
		FumperSound.fruit_bascule.load();
		FumperSound.spaceship.load();
		FumperSound.bird.load();

		// Mettre le fond d'écran - valade pendant tout le jeu
		AtlasRegion bgRegion = getAtlas().findRegion("background/background");
		Drawable bgDrawable = new TextureRegionDrawable(bgRegion);
		bgImage = new Image(bgDrawable);
		bgImage.setFillParent(true);
		stage.addActor(bgImage);
		
		//copyright
		textCopyright=new TextActor(font_berlin_copyright,copyright);

		stage.addActor(textCopyright);
		// Splash
		splash = new Image(new TextureRegionDrawable(getAtlas()
				.findRegion(TypeOfObject.splash.getNameImage())));
		splash.setOrigin(splash.getWidth()/2f, splash.getHeight()/2f);
		stage.addActor(splash);
		// Petite main instructive
		tapandplayActor = new Image(new TextureRegionDrawable(getAtlas()
				.findRegion(TypeOfObject.tapandplay.getNameImage())));
		stage.addActor(tapandplayActor);
		//oiseau
		Array<AtlasRegion> arrayOiseau = new Array<AtlasRegion>();
		for (int i = 0; i < TypeOfObject.oiseau.getNbImages(); i++) {
			arrayOiseau.add(getAtlas().findRegion(
					TypeOfObject.oiseau.getAllImages()[i]));
		}
		Animation animationOiseau = new Animation(0.15f, arrayOiseau,
				Animation.PlayMode.LOOP);
		oiseau = new AnimatedActor(animationOiseau);
		oiseau.setTouchable(Touchable.enabled);
		oiseau.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				handleBirdClick();
			}
		});
		stage.addActor(oiseau);
		// fusée animée
		Array<AtlasRegion> arrayFusee = new Array<AtlasRegion>();
		for (int i = 0; i < TypeOfObject.fusee.getNbImages(); i++) {
			arrayFusee.add(getAtlas().findRegion(
					TypeOfObject.fusee.getAllImages()[i]));
		}
		Animation animationFusee = new Animation(0.05f, arrayFusee,
				Animation.PlayMode.LOOP);
		fusee = new AnimatedActor(animationFusee);
		fusee.setPosition(-1000, -1000);
		stage.addActor(fusee);
		// la fusee qui brule
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("effects/ParticleEffects.p"),
				Gdx.files.internal("effects"));
		effectActor = new ParticleEffectActor(effect, fusee);
		stage.addActor(effectActor);

		// Animation du text GameOver & text Start ou Replay
		textBonus = new TextActor(font_goodgirl3, "Message Bonus!");
		textBonus.addAction(fadeOut(0f));
		textBonus.setScale(Gdx.graphics.getWidth()/500f);
		stage.addActor(textBonus);
		// Animation du text GameOver & text Start ou Replay
		textGameOver = new TextActor(font_goodgirl2, "GAME OVER");
		textGameOver.setVisible(false);
		textGameOver.addAction(forever(parallel(
				sequence(moveBy(5f, 0, 0.5f),
						moveBy(-5f, 0, 0.5f)),
				sequence(scaleBy(0.1f, 0.1f, 0.5f),
						scaleBy(-0.1f, -0.1f, 0.5f)))));
		stage.addActor(textGameOver);
		textStart = new TextActor(font_goodgirl, "START FUMPER");
		textStart.setTouchable(Touchable.enabled);
		textStart.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(
						"Fumper/PlayScreen/textStart/clicked(x,y)" + "(" + x
								+ "," + y + ")" + "(getX,getY)=",
						"(" + textStart.getX() + "," + textStart.getY()
								+ ") et (textStart.width,height)=("
								+ textStart.getWidth() + ","
								+ textStart.getHeight() + ")");
				start_ok = true;
			}
		});
		textStart.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log("Example", "touch started at (" + x + ", " + y
						+ ")");
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log("Example", "touch done at (" + x + ", " + y + ")");
			}
		});

		stage.addActor(textStart);

		// HUD=Score et Highscore
		textScore = new TextActor(font_hennypenny, "Score=0\nLevel=1");
		stage.addActor(textScore);
		textHighScore = new TextActor(font_hennypenny2, "Local HighScore=0\nWeb HighScore=...");
		stage.addActor(textHighScore);

		// Label Invincible clignotant en bas a gauche
		textInvincible = new TextActor(font_berlin, "invincible");
		textInvincible.setColor(1f, 0.85f, 0.15f, 1f);
		textInvincible.setVisible(false);
		textInvincible.addAction(forever(sequence(fadeOut(0.35f), fadeIn(0.35f))));
		stage.addActor(textInvincible);

		Gdx.input.setInputProcessor(stage);
		stage.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("Fumper/PlayScreen/ClickListener of stage/",
						"button clicked");
			}

		});

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					if (!myToast
							.makeText("Back Again to Quit", font_berlin, 1f))
						examGamePart = GamePart.QUIT;
				}
				return super.keyDown(event, keycode);
			}

			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log("Fumper/PlayScreen/InputListenerStage",
						"touch started at (" + x + ", " + y + ")");

				// Detection supplementaire du toucher sur l'oiseau
				if (oiseau != null) {
					float so = Gdx.graphics.getWidth() / 200.0f;
					float birdW = oiseau.getWidth() * so;
					float birdH = oiseau.getHeight() * so;
					float birdX = oiseau.getX();
					float birdY = oiseau.getY();
					float margin = Math.max(30f, birdW * 0.35f);
					if (x >= birdX - margin && x <= birdX + birdW + margin
							&& y >= birdY - margin && y <= birdY + birdH + margin) {
						handleBirdClick();
					}
				}

				getBascule().push(level+(bonus_flag?100:0)+(superman?100:0));
				Gdx.app.log("PlayScreen/touchDown=>", "bonus="+bonus_flag+", level="+level+", force="+(level+(bonus_flag?100:0)));
				FumperSound.bascule.play(0.5f);

				Gdx.app.log(
						"Fumper/PlayScreen/InputListenerStage/touchDown/Hit Actor=",
						"" + stage.hit(x, y, false).getName());
				try {
					Gdx.input.vibrate(50);
				} finally {

				}
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				Gdx.app.log("Fumper/PlayScreen/InputListenerStage/touchUp",
						"touch done at (" + x + ", " + y + ")");
			}
		});
	}

	private void handleBirdClick() {
		long now = TimeUtils.millis();
		if (now - lastBirdClickTimestamp < 180) {
			return; // Evite le double comptage
		}
		lastBirdClickTimestamp = now;

		if (now - lastBirdClickTime > 2500) {
			birdClickCount = 1;
		} else {
			birdClickCount++;
		}
		lastBirdClickTime = now;

		Gdx.app.log("Fumper/PlayScreen", "Bird clicked! Count: " + birdClickCount);
		FumperSound.bird.play(0.5f);

		if (birdClickCount >= 3) {
			birdClickCount = 0;
			invincible = !invincible;
			if (invincible) {
				invincibleUsedInSession = true;
				if (textInvincible != null) {
					textInvincible.setVisible(true);
					textInvincible.toFront();
				}
				myToast.makeText("Mode Invincible ACTIVE", font_berlin, 2f);
				Gdx.app.log("Fumper/PlayScreen", "INVINCIBLE MODE ACTIVATED");
			} else {
				if (textInvincible != null) {
					textInvincible.setVisible(false);
				}
				myToast.makeText("Mode Invincible DESACTIVE", font_berlin, 2f);
				Gdx.app.log("Fumper/PlayScreen", "INVINCIBLE MODE DEACTIVATED");
			}
		}
	}

	/**
	 * Permet de supprimer les fruits hors champ et controler ceux qui sont dans
	 * le panier
	 * 
	 * @param fruits
	 * @return la taille de Array des fruits
	 */
	private int UpdateFruits(Array<Fruit> fruits, boolean immediat) {

		for (int index = fruits.size - 1; index >= 0; index--) {
			Fruit i = fruits.get(index);
			// cas spécial du fruit qui va dans les airs => le Bonus time permet de lancer très fort...
			if (i.getY() > Gdx.graphics.getWidth()*3f
					&& fusee.getActions().size == 0) {
				if (!invincibleUsedInSession) {
					score+=5;
				}
				textScore.addAction(sequence(scaleBy(0.5f,0.5f, 0.5f), scaleBy(-0.5f,-0.5f, 0.5f)));
				// On positionne en dehors de l'écran la fusee
				fusee.setZIndex(oiseau.getZIndex()-1);
				fusee.setScale(Gdx.graphics.getWidth()/500f );
				fusee.setPosition(Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight() -100);
				fusee.setRotation(80);
				effectActor.setScale(fusee.getScaleX());
				fusee.addAction(sequence(
						parallel(
								moveTo(-500, Gdx.graphics.getHeight()/3, 5f),
								rotateTo(100f,5f)
								)));
				FumperSound.spaceship.play(0.3f);
				effectActor.start();
			}
			//cas spécial de l'oiseau => le Bonus time permet de lancer très fort...
			float so=Gdx.graphics.getWidth()/200.0f;
			float x=i.getBody().getWorldCenter().x * AbstractScreen.BOX_TO_WORLD;
			float xo=oiseau.getX()+oiseau.getWidth()/2.0f*so;
			float yo=oiseau.getY()+oiseau.getHeight()/2.0f*so;
			float y=i.getBody().getWorldCenter().y * AbstractScreen.BOX_TO_WORLD;
			
			if ((y>(yo-oiseau.getHeight()*so/2f))&&
				(y<(yo+oiseau.getHeight()*so/2f))&&
				(x>(xo-oiseau.getWidth()*so/2f))&&
				(x<(xo+oiseau.getWidth()*so/2f))&&
				splash.getActions().size == 0){
				resizeOiseau();
				if (!invincibleUsedInSession) {
					score+=2;
				}
				splash.setPosition(xo-splash.getOriginX(), yo-splash.getOriginY());
				splash.toFront();
				splash.addAction((sequence(
											fadeIn(0f),
											parallel(
													rotateBy(90f, 0.2f),
													scaleTo(0.5f, 0.5f, 0.2f)),
											delay(1f),
											fadeOut(0f),
											rotateBy(-90f),
											scaleTo(Gdx.graphics.getWidth()/200, Gdx.graphics.getWidth()/200))));
				
				Gdx.app.log("Playscreen/UpdateFruits=>", "Oiseau touché!");
				FumperSound.bird.getSnd().play(1f, 0.5f, 0f);
				//FumperSound.bird.play();
			}
			// Quels fruits détruire?
			boolean outOfBounds = i.getBody().getPosition().x * AbstractScreen.BOX_TO_WORLD < -100
					|| i.getBody().getPosition().x * AbstractScreen.BOX_TO_WORLD > Gdx.graphics.getWidth() + 100
					|| i.getBody().getPosition().y * AbstractScreen.BOX_TO_WORLD < -100
					|| i.getColor().a == 0.5f;
			boolean inBasket = i.getBody().getUserData() != null && i.getBody().getUserData().equals("in");

			if ((outOfBounds || (inBasket && immediat)) && !worldbox.isLocked()) {
				if (inBasket) {
					fruitsInBasketThisLevel++;
					if (!invincibleUsedInSession) {
						score += Math.max(1, level);
					}
					textScore.addAction(sequence(scaleBy(0.5f,0.5f, 0.5f), scaleBy(-0.5f,-0.5f, 0.5f)));
					FumperSound.fruit_bascule.play();
				}
				worldbox.destroyBody(i.getBody());
				i.remove();
				fruits.removeIndex(index);
			} else {
				i.setPosition(i.getBody().getPosition().x
						* AbstractScreen.BOX_TO_WORLD, i.getBody()
						.getPosition().y * AbstractScreen.BOX_TO_WORLD);
				i.setRotation(i.getBody().getAngle()
						* MathUtils.radiansToDegrees);
			}
		}
		return fruits.size;
	}

	private float getHudTopPadding() {
		float height = Gdx.graphics.getHeight();
		float width = Gdx.graphics.getWidth();
		float ratio = height / Math.max(1f, width);
		if (ratio > 1.9f) {
			// Ecrans allongés modernes (19.5:9, 20:9) : monte légèrement le HUD sous la zone de statut
			return Math.max(38f, height * 0.042f);
		} else {
			// Ecrans classiques (16:9, tablettes, desktop)
			return Math.max(24f, height * 0.032f);
		}
	}

	private float getHudGap() {
		return Math.max(8f, Gdx.graphics.getHeight() * 0.009f);
	}

	private float getHudX() {
		return Math.max(20f, Gdx.graphics.getWidth() * 0.035f);
	}

	private void UpdateHUD() {
		String txt = new String("Score=" + score + "\nLevel=" + level);
		textScore.setText(txt);
		StringBuffer chaine = new StringBuffer("Local HighScore=" + highscore);
		if (highscoreWEB < 0 || highscoreWEB_name.startsWith("!"))
			textHighScore.setText(chaine
					+ "\nWeb HighScore=<Internet not available>");
		else
			textHighScore.setText("Local HighScore=" + highscore
					+ "\nWeb HighScore=" + highscoreWEB + "\t by "
					+ highscoreWEB_name);

		float hudX = getHudX();
		float topPadding = getHudTopPadding();
		float gap = getHudGap();

		textScore.setPosition(hudX, Gdx.graphics.getHeight() - textScore.getHeight() - topPadding);
		textHighScore.setPosition(hudX, textScore.getY() - textHighScore.getHeight() - gap);

		if (textInvincible != null) {
			if (invincible) {
				textInvincible.setVisible(true);
				textInvincible.toFront();
			} else {
				textInvincible.setVisible(false);
			}
		}
	}

	/**
	 * State machine générale / Création de la frame via super() (cf
	 * AbsctractScreen)
	 */
	@Override
	public void render(float delta) {
		super.render(delta);

		UpdateHUD();
		getBascule().update();
		getPanier().update();
		switch (examGamePart) {

		case DEBUT:
			if (start_ok) {
				promptShown = false;
				invincible = false;
				invincibleUsedInSession = false;
				birdClickCount = 0;
				fruitsInBasketThisLevel = 0;
				if (textInvincible != null) {
					textInvincible.setVisible(false);
				}
				textGameOver.setVisible(false);
				textStart.setVisible(false);
				score = 0;
				level = superman?9:1;
				nb_fruits = 10;
				resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				examGamePart = GamePart.EN_COURS;
			}
			break;
		case EN_COURS:
			// quand doit-on laisser un nouveau fruit? soit plus de body fruits dans le jeu wait-- < 0 
			if (fruits.size-panier.check_fruits_in()<= 0 || (bonus_flag? wait--<0:false))
				examGamePart = GamePart.NEW_FRUIT;
			else
				UpdateFruits(fruits, false);
			break;

		case NEW_FRUIT:
			panier.check_fruits_in();
			textScore.addAction(sequence(alpha(0.0f, 0.2f), alpha(1.0f,0.2f)));
			wait = bonus_flag?NB_CYCLE_FOR_5S:NB_CYCLE_FOR_15S;
			wait = debug?NB_CYCLE_FOR_1S:wait;
			if (--nb_fruits < 0)
				examGamePart = GamePart.LEVEL_END;
			else {
				fruits.add(new Fruit(this, tofTable[(level - 1)
						% TypeOfObject.getNbFruits()], 0.1f*(1+(level-1)/100f)));
				Fruit currentFruit = fruits.peek();
				currentFruit.addAction(
						sequence(delay(10.0f), alpha(0.5f, 5.0f),
								run(new Runnable() {
									public void run() {
										System.out.println("Action complete!");
									}
								})));
				currentFruit.getBody().setUserData(new String("out"));
				// laché de fruit depuis l'arbre : positionné pour atterrir parfaitement sur la partie gauche de la bascule
				float heightOffset = MathUtils.random(-Gdx.graphics.getHeight() * 0.02f, Gdx.graphics.getHeight() * 0.02f);
				// Centre de réception de la bascule : le pivot est à ~0.28 W, l'extrémité gauche à ~0.05 W.
				// On place le fruit à ~0.15 W (centre de la zone de réception) pour qu'il arrive en plein sur la planche.
				float targetDropX = getBascule().getImgBuche().getX() - Gdx.graphics.getWidth() / 10f;
				int x = (int) (targetDropX + MathUtils.random(-Gdx.graphics.getWidth() * 0.008f, Gdx.graphics.getWidth() * 0.008f));
				// Hauteur sous le feuillage de l'arbre
				int y = (int) (Gdx.graphics.getHeight() / 2.1f + heightOffset);
				if (debug)
					x = (int) (panier.getImgPanier().getX() + 5);
				int angle = MathUtils.random(-10, 10);
				currentFruit.ResetPosition(x, y, angle);

				// Diversité naturelle et contrôlée de vitesse de chute :
				float gravityScale = MathUtils.random(0.9f, 1.15f);
				float linearDamping = MathUtils.random(0.02f, 0.15f);
				float initialVy = MathUtils.random(-0.5f, 0.0f);
				float initialAngularVel = MathUtils.random(-0.3f, 0.3f);
				currentFruit.getBody().setGravityScale(gravityScale);
				currentFruit.getBody().setLinearDamping(linearDamping);
				currentFruit.getBody().setLinearVelocity(0f, initialVy);
				currentFruit.getBody().setAngularVelocity(initialAngularVel);

				stage.addActor(currentFruit);
				imageFruits.removeIndex(imageFruits.size - 1).remove();
				panier.getImgPanier_front().setZIndex(50);
				Gdx.app.log("Fumper/playScreen/Render/NEW_FRUIT/", "nb fruits="
						+ fruits.size + "/zindex=" + currentFruit.getZIndex()
						+ "/zindex panier=" + panier.getImgPanier().getZIndex()
						+ "/gravityScale=" + gravityScale);
				examGamePart = GamePart.EN_COURS;
			}

			break;

		case LEVEL_END:
			if (UpdateFruits(fruits, true) == 0) // vérifie qu'il n'y a plus de
													// fruit en mouvement ou sur
													// l'écran
				if (invincible || bonus_flag || fruitsInBasketThisLevel > 0 || score > lastlevel_score) {
					examGamePart = GamePart.NEW_LEVEL;
					lastlevel_score = score;
					fruitsInBasketThisLevel = 0;
				} else {
					examGamePart = GamePart.GAMEOVER;
					lastlevel_score = 0;
					fruitsInBasketThisLevel = 0;
				}
			else
				panier.check_fruits_in();
			break;

		case NEW_LEVEL:
			fruitsInBasketThisLevel = 0;
			if (bonus_flag) {
				examGamePart = GamePart.BONUS_TIME;
				break;
			}
			level++;
			if (level % TypeOfObject.getNbFruits() == 0) {
				textBonus.setText("Bonus Time !");	
				textBonus.toFront();
				textBonus.setPosition(Gdx.graphics.getWidth()/2-textBonus.getWidth()/2, Gdx.graphics.getHeight()/2);
				textBonus.addAction(parallel(
						fadeIn(0f),
								moveBy(0f, Gdx.graphics.getHeight()/3, 3f),
								scaleBy(1f, 1f, 3f),
								sequence(
										delay(2f),
										fadeOut(1f),
										scaleBy(-1f, -1f, 0f))));
				bonus_flag=true;
				nb_fruits = 50;
			}
			else
				nb_fruits = 10;
			
			// On monte le panier
			if (panier != null)
				panier.RemovePanier();
			panier = new Panier(
					this,
					Gdx.graphics.getWidth() / 1.5f,
					(Gdx.graphics.getWidth() / 3f * (1.0f + ((level - 1) % TypeOfObject
							.getNbFruits()) / 10f)), 0.2f * Gdx.graphics
							.getWidth() / 400);
			panier.getImgPanier_front().toFront();

			if (score > highscore) {
				highscore = score;
				highscore_name = "Still UnFamousKnown";
			}
			examGamePart = GamePart.EN_COURS;
			FruitsDansArbre();
			break;
		case BONUS_TIME:
			wait=NB_CYCLE_FOR_20S;
			examGamePart = GamePart.BONUS_TIME_END;
			bonus_flag=false;
			textBonus.setText("End of Bonus Time...");
			textBonus.toFront();
			textBonus.setPosition(Gdx.graphics.getWidth()/2-textBonus.getWidth()/2, Gdx.graphics.getHeight()/2);
			textBonus.addAction(parallel(fadeIn(0f),
							moveBy(0f, Gdx.graphics.getHeight()/3, 3f),
							scaleBy(0.5f, 0.5f, 3f),
							sequence(
									delay(2f),
									fadeOut(1f)),
							scaleBy(-0.5f, -0.5f, 0f)));
			
			break;
		case BONUS_TIME_END:
			if (wait--<0) 
				examGamePart = GamePart.NEW_LEVEL;
			break;
		case GAMEOVER:
			if (!invincibleUsedInSession) {
				if (score > highscore) {
					highscore = score;
					game.FumperPrefs.setHighScore(score, highscore_name);
				}
				if (score > highscoreWEB && highscoreWEB > 0 && !promptShown) {
					promptShown = true;
					Gdx.input.getTextInput(HighScoreWebBox,
							"Nice ! You kill WebScore ! \nWhat's Your Name Farmer?",
							"", "");
				}
			}
			textGameOver.setVisible(true);
			textStart.setText("Play Again");
			resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			textStart.setVisible(true);
			examGamePart = GamePart.DEBUT;
			start_ok = false;
			break;
		case QUIT:
			start_ok = false;
			if (!invincibleUsedInSession) {
				if (game.FumperPrefs.getHighScore() < score) {
					game.FumperPrefs.setHighScore(score, "local");
					highscore = score;
				}
				if (highscore > highscoreWEB && highscoreWEB > 0 && !promptShown) {
					promptShown = true;
					Gdx.input.getTextInput(HighScoreWebBox,
							"Nice ! You kill WebScore ! \nWhat's Your Name Farmer?",
							"", "");
				}
			}
			dispose();
			game.setScreen(new SplashScreen(game));
			break;
		default:
			break;
		}
	}

	/**
	 * Récupération et vérification asynchrone du high score Web
	 */
	private void refreshWebScore() {
		OnlineScoreService.fetchHighScore(new OnlineScoreService.ScoreCallback() {
			@Override
			public void onSuccess(final String farmer, final int score) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						highscoreWEB = score;
						highscoreWEB_name = farmer;
						if (highscore > highscoreWEB && highscoreWEB > 0 && !start_ok && !promptShown) {
							promptShown = true;
							Gdx.input.getTextInput(HighScoreWebBox,
									"Your Local HighScore Kills WebScore's ! What's Your Name Farmer?",
									"", "");
						}
					}
				});
			}

			@Override
			public void onError(final String error) {
				Gdx.app.log("Fumper/PlayScreen", "Online score error: " + error);
			}
		});
	}

	void resizeOiseau(){
		//oiseau
		oiseau.clearActions();
		oiseau.setScale(Gdx.graphics.getWidth()/200f);
		oiseau.setPosition(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()/1.2f);
		oiseau.addAction(forever(parallel(
								sequence(
										moveTo(-Gdx.graphics.getWidth()/10, oiseau.getY(), 10f),
										moveTo(Gdx.graphics.getWidth(), oiseau.getY(),0f))
								)));
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		// Recréation du Panier
		if (panier != null)
			panier.RemovePanier();
		panier = new Panier(
				this,
				Gdx.graphics.getWidth() / 1.5f,
				(Gdx.graphics.getWidth() / 3f * (1.0f + ((level - 1) % TypeOfObject
						.getNbFruits()) / 10f)), 0.2f * Gdx.graphics
						.getWidth() / 400);
		// Recréation de la Bascule
		if (bascule != null) {
			bascule.RemoveCatapult();
			bascule = null;
		}
		bascule = new Bascule(this, width / 4, width / 10f, 0.08f * width / 400);
		// Mettre les pommes dans l'arbre
		if (imageFruits != null)
			while (imageFruits.size > 0) {
				imageFruits.removeIndex(imageFruits.size - 1).remove();
			}
		FruitsDansArbre();
		
		//Resize Oiseau et Splash pour l'oiseau mort
		splash.setScale(Gdx.graphics.getWidth()/200);
		splash.getColor().a=0;
		resizeOiseau();			

		
		// Resize the tapandplay & start button
		tapandplayActor.setScale(width / 1000.0f);
		tapandplayActor.setPosition(Gdx.graphics.getWidth() / 1.5f,
				Gdx.graphics.getHeight() / 50.0f);

		// Resize Font
		if (textStart != null) {
			float sizefont = (float) width / 500f;
			textStart.setScale(sizefont);
			textStart.setPosition(width / 2 - textStart.getWidth() / 2, height);
			textStart.toFront();
			textStart.addAction(sequence(
					delay(1f),
					moveTo(width / 2 - textStart.getWidth() / 2f,
							height / 2f, 1f, Interpolation.bounceOut),
					forever(sequence(delay(3f), moveBy(0f, 10f,
							1f), moveBy(0f, -10f, 1f,
							Interpolation.bounceOut)))));
		}

		if (textGameOver != null) {
			float sizefont = (float) width / 300f;
			textGameOver.setScale(sizefont);
			textGameOver.setPosition(width / 2 - textGameOver.getWidth() / 2,
					height / 5);
			textGameOver.toFront();
		}
		if (textScore != null) {
			float sizefont = (float) width / 450f;
			textScore.setScale(sizefont);
			float hudX = getHudX();
			float topPadding = getHudTopPadding();
			textScore.setPosition(hudX, height - textScore.getHeight() - topPadding);
		}
		if (textHighScore != null) {
			float sizefont = (float) width / 750f;
			textHighScore.setScale(sizefont);
			float hudX = getHudX();
			float gap = getHudGap();
			float scoreY = (textScore != null) ? (textScore.getY() - textHighScore.getHeight() - gap) : (height - 130);
			textHighScore.setPosition(hudX, scoreY);
		}
		if (textCopyright != null) {
			textCopyright.toFront();
			textCopyright.setScale(Gdx.graphics.getWidth()/1500f);
			textCopyright.setPosition(Gdx.graphics.getWidth()-textCopyright.getStrWidth(),textCopyright.getStrHeight()/2f);
		}
		if (textInvincible != null) {
			textInvincible.setScale((float) width / 1100f);
			float invX = getHudX();
			float invY = Math.max(12f, height * 0.015f);
			textInvincible.setPosition(invX, invY);
			textInvincible.toFront();
		}
	}

	private void FruitsDansArbre() {
		FruitsDansArbre(nb_fruits, level);
	}

	private void FruitsDansArbre(int nb_fruits, int level) {

		imageFruits = new Array<Image>();
		AtlasRegion ImgRegion = getAtlas().findRegion(
				tofTable[(level - 1) % TypeOfObject.getNbFruits()]
						.getNameImage());
		Drawable ImgDrawable = new TextureRegionDrawable(ImgRegion);
		for (int i = 0; i < nb_fruits; i++) {
			imageFruits.add(new Image(ImgDrawable));
			imageFruits.peek().setScale(Gdx.graphics.getWidth() * 0.0005f);
			imageFruits.peek()
					.setPosition(
							Gdx.graphics.getWidth()
									/ 11
									+ new Random().nextInt(Gdx.graphics
											.getWidth() / 6),
							Gdx.graphics.getHeight()
									/ 2.2f
									+ new Random().nextInt(Gdx.graphics
											.getHeight() / 10));
			stage.addActor(imageFruits.peek());
		}
	}

	@Override
	public void dispose() {
		if (scoreTimer != null) {
			scoreTimer.clear();
			scoreTimer.stop();
		}
		super.dispose();
	}

	/**
	 * @return the bascule
	 */
	public Bascule getBascule() {
		return bascule;
	}
	/**
	 * @return the panier
	 */
	public Panier getPanier() {
		return panier;
	}
	/**
	 * @param bascule
	 *            the bascule to set
	 */
	public void setBascule(Bascule bascule) {
		this.bascule = bascule;
	}
}
