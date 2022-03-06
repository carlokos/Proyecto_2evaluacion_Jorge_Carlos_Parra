package com.joyride.Screens;

import static com.joyride.extra.Utils.SCREEN_HEIGHT;
import static com.joyride.extra.Utils.SCREEN_WIDTH;
import static com.joyride.extra.Utils.WORLD_HEIGHT;
import static com.joyride.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.joyride.MainGame;

public class getReadyScreen extends BaseScreen{
    private Stage stage;
    private OrthographicCamera fontCamera;
    private OrthographicCamera ortCamera;
    private BitmapFont title;
    private Image background;

    public getReadyScreen(MainGame mainGame) {
        super(mainGame);
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.ortCamera = (OrthographicCamera) this.stage.getCamera();

        prepareTitle();
    }

    public void prepareTitle(){
        this.title = this.mainGame.assetManager.getFont();
        this.title.getData().scale(0.005f);
        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCamera.update();
    }

    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage.addActor(this.background);
    }

    @Override
    public void render(float delta) {
        //solo tiene el fondo y la font asi que no tiene mucho el render, aun asi debemos poner
        //el listener para que cambie de pantalla
        this.stage.getBatch().setProjectionMatrix(ortCamera.combined);
        this.stage.act();
        this.stage.draw();

        //Gdx.input.isTouched devuelve un booelano cuando se toca la pantalla
        if(Gdx.input.isTouched()){
            mainGame.setScreen(new GameScreen(mainGame));
        }

        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.title.draw(this.stage.getBatch(), "TOUCH THE SCREEN TO START",SCREEN_WIDTH/7.8f, SCREEN_HEIGHT/1.7f);
        this.stage.getBatch().end();
    }

    @Override
    public void show() {
        addBackground();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}

