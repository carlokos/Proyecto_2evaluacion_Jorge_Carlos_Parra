package com.joyride.Screens;

import static com.joyride.Screens.GameScreen.contador;
import static com.joyride.extra.Utils.SCREEN_HEIGHT;
import static com.joyride.extra.Utils.SCREEN_WIDTH;
import static com.joyride.extra.Utils.WORLD_HEIGHT;
import static com.joyride.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.joyride.MainGame;

public class GameOverScreen extends BaseScreen{

    private Stage stage;
    private Music musicbg;
    private OrthographicCamera fontCamera;
    private BitmapFont title, puntuacion, again;

    public GameOverScreen(MainGame mainGame) {
        super(mainGame);
        //solo necesitamos el stage y la camara para indicar que se ha perdido
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        musicbg = mainGame.assetManager.getGameoverBG();

        prepareTitle();
    }

    @Override
    public void show() {
        musicbg.setLooping(true);
        musicbg.play();
    }

    @Override
    public void render(float delta) {
        //Esto es importante para asegurarnos de tener una pantalla limpia al cambiarla
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.title.draw(this.stage.getBatch(), "GAME OVER",SCREEN_WIDTH/3.2f, SCREEN_HEIGHT/1.4f);
        this.puntuacion.draw(this.stage.getBatch(), "Score: "+contador,SCREEN_WIDTH/3.2f, SCREEN_HEIGHT/1.7f);
        this.again.draw(this.stage.getBatch(), "TAP TO RETRY",SCREEN_WIDTH/3.2f, SCREEN_HEIGHT/2.5f);

        if(Gdx.input.isTouched()){
            //cuando toque la pantalla de vuelta a la pantalla principal
            this.musicbg.stop();
            mainGame.setScreen(new GameScreen(mainGame));
        }

        this.stage.getBatch().end();
    }

    private void prepareTitle(){
        this.title = this.mainGame.assetManager.getFont();
        this.title.getData().scale(0.005f);
        this.puntuacion = this.mainGame.assetManager.getFont();
        this.puntuacion.getData().scale(0.005f/2);
        this.again = this.mainGame.assetManager.getFont();
        this.again.getData().scale(0.005f/4);

        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCamera.update();
    }

    @Override
    public void dispose() {
        stage.dispose();
        puntuacion.dispose();
        title.dispose();
    }

    @Override
    public void hide() {
        musicbg.pause();
    }
}
