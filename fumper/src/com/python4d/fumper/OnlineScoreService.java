package com.python4d.fumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Service cloud REST/HTTP moderne pour stocker et récupérer
 * le meilleur score mondial (HighScore WEB) de Fumper.
 * Fonctionne de façon asynchrone sur toutes les plateformes (Android, Desktop, Web).
 */
public class OnlineScoreService {

	private static final String API_URL = "https://api.restful-api.dev/objects/ff808181a09d98f701a0df92f10c2041";
	private static final String FALLBACK_URL = "http://forumpanhard.free.fr/fumper.txt";
	private static final int TIMEOUT_MS = 6000;

	public interface ScoreCallback {
		void onSuccess(String farmer, int score);
		void onError(String error);
	}

	public interface SubmitCallback {
		void onSuccess();
		void onError(String error);
	}

	/**
	 * Récupère le high score en ligne de manière asynchrone (avec secours automatique).
	 */
	public static void fetchHighScore(final ScoreCallback callback) {
		HttpRequest request = new HttpRequest(HttpMethods.GET);
		request.setUrl(API_URL);
		request.setHeader("Accept", "application/json");
		request.setTimeOut(TIMEOUT_MS);

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				int status = httpResponse.getStatus().getStatusCode();
				if (status >= 200 && status < 300) {
					try {
						String responseText = httpResponse.getResultAsString();
						Gdx.app.log("OnlineScoreService", "Fetch success: " + responseText);
						JsonValue root = new JsonReader().parse(responseText);
						JsonValue data = root != null ? root.get("data") : null;
						if (data != null) {
							String farmer = data.getString("farmer", "Unknown Farmer");
							int hs = data.getInt("hs", 0);
							if (callback != null) {
								callback.onSuccess(farmer, hs);
							}
							return;
						}
						fetchFallback(callback, "Structure JSON inattendue");
					} catch (Exception e) {
						Gdx.app.error("OnlineScoreService", "Erreur parsing JSON", e);
						fetchFallback(callback, "Parsing error: " + e.getMessage());
					}
				} else {
					Gdx.app.error("OnlineScoreService", "Fetch HTTP status: " + status + ", bascule vers fallback free.fr");
					fetchFallback(callback, "HTTP " + status);
				}
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.error("OnlineScoreService", "Fetch failed: " + (t != null ? t.getMessage() : "erreur"), t);
				fetchFallback(callback, t != null ? t.getMessage() : "Connexion impossible");
			}

			@Override
			public void cancelled() {
				if (callback != null) {
					callback.onError("Requete annulee");
				}
			}
		});
	}

	/**
	 * Fallback vers le fichier historique hébergé sur Free.fr
	 */
	private static void fetchFallback(final ScoreCallback callback, final String initialError) {
		HttpRequest fallbackReq = new HttpRequest(HttpMethods.GET);
		fallbackReq.setUrl(FALLBACK_URL);
		fallbackReq.setTimeOut(TIMEOUT_MS);

		Gdx.net.sendHttpRequest(fallbackReq, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				int status = httpResponse.getStatus().getStatusCode();
				if (status >= 200 && status < 300) {
					try {
						String text = httpResponse.getResultAsString();
						String farmer = "baco";
						int hs = 152;
						String[] lines = text.split("[\\r\\n]+");
						for (String line : lines) {
							if (line.contains("=")) {
								String[] parts = line.split("=", 2);
								if (parts[0].trim().equalsIgnoreCase("farmer")) {
									farmer = parts[1].trim();
								} else if (parts[0].trim().equalsIgnoreCase("hs")) {
									try {
										hs = Integer.parseInt(parts[1].trim());
									} catch (NumberFormatException ignored) {}
								}
							}
						}
						Gdx.app.log("OnlineScoreService", "Fallback success from free.fr: farmer=" + farmer + ", hs=" + hs);
						if (callback != null) {
							callback.onSuccess(farmer, hs);
						}
						return;
					} catch (Exception e) {
						Gdx.app.error("OnlineScoreService", "Fallback parsing error", e);
					}
				}
				if (callback != null) {
					callback.onError(initialError);
				}
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.error("OnlineScoreService", "Fallback free.fr failed: " + (t != null ? t.getMessage() : "unknown"));
				if (callback != null) {
					callback.onError(initialError);
				}
			}

			@Override
			public void cancelled() {
				if (callback != null) {
					callback.onError("Requete annulee");
				}
			}
		});
	}

	/**
	 * Met à jour le high score en ligne de manière asynchrone.
	 */
	public static void submitHighScore(String farmer, int score, final SubmitCallback callback) {
		if (farmer == null || farmer.trim().isEmpty()) {
			farmer = "Anonymous Farmer";
		}
		// Echappement des guillemets pour JSON propre
		final String cleanFarmer = farmer.trim().replace("\"", "\\\"");

		HttpRequest request = new HttpRequest(HttpMethods.PUT);
		request.setUrl(API_URL);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Accept", "application/json");
		request.setTimeOut(TIMEOUT_MS);

		String jsonPayload = "{\"name\":\"fumper_highscore\",\"data\":{\"farmer\":\"" + cleanFarmer + "\",\"hs\":" + score + "}}";
		request.setContent(jsonPayload);

		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				int status = httpResponse.getStatus().getStatusCode();
				if (status >= 200 && status < 300) {
					Gdx.app.log("OnlineScoreService", "Submit success: " + httpResponse.getResultAsString());
					if (callback != null) {
						callback.onSuccess();
					}
				} else {
					Gdx.app.error("OnlineScoreService", "Submit HTTP error: " + status);
					if (callback != null) {
						callback.onError("HTTP " + status);
					}
				}
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.error("OnlineScoreService", "Submit failed", t);
				if (callback != null) {
					callback.onError(t != null ? t.getMessage() : "Envoi echoue");
				}
			}

			@Override
			public void cancelled() {
				if (callback != null) {
					callback.onError("Envoi annule");
				}
			}
		});
	}
}
