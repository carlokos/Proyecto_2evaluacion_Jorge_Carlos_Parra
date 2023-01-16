package com.joyride;

import com.badlogic.gdx.Game;
import com.joyride.Screens.GameOverScreen;
import com.joyride.Screens.GameScreen;
import com.joyride.Screens.getReadyScreen;
import com.joyride.extra.AssetMan;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {
	public GameScreen gameScreen;

	public GameOverScreen gameOverScreen;
	public com.joyride.Screens.getReadyScreen getReadyScreen;

	public AssetMan assetManager;


	//Este metodo crea los assets para que funcione la aplicacion
	@Override
	public void create(){
		this.assetManager = new AssetMan();

		this.gameScreen = new GameScreen(this);
		this.getReadyScreen = new getReadyScreen(this);
		this.gameOverScreen = new GameOverScreen(this);

		setScreen(this.getReadyScreen);
	}
}