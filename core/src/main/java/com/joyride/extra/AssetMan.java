package com.joyride.extra;

import static com.joyride.extra.Utils.ATLAS_MAP;
import static com.joyride.extra.Utils.BACKGROUND_IMAGE;
import static com.joyride.extra.Utils.DEATH_SOUND;
import static com.joyride.extra.Utils.FUENTE_FNT;
import static com.joyride.extra.Utils.FUENTE_PNG;
import static com.joyride.extra.Utils.GAMEOVER_BG;
import static com.joyride.extra.Utils.MUSIC_BG;
import static com.joyride.extra.Utils.USER_SPIKE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class AssetMan {
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public AssetMan(){
        this.assetManager = new AssetManager();

        assetManager.load(ATLAS_MAP, TextureAtlas.class);

        assetManager.load(MUSIC_BG,Music.class);
        assetManager.load(GAMEOVER_BG, Music.class);
        assetManager.load(DEATH_SOUND, Sound.class);

        assetManager.finishLoading();

        textureAtlas = assetManager.get(ATLAS_MAP);

    }

    public TextureRegion getBackground() {
        return this.textureAtlas.findRegion(BACKGROUND_IMAGE);
    }

    public TextureRegion getSpikedBall(){
        return this.textureAtlas.findRegion(USER_SPIKE);
    }

    public Sound getDeathSound(){
        return this.assetManager.get(DEATH_SOUND);
    }

    public Music getMusicBG(){
        return this.assetManager.get(MUSIC_BG);
    }

    public Music getGameoverBG(){
        return this.assetManager.get(GAMEOVER_BG);
    }

    public BitmapFont getFont(){
        return new BitmapFont(Gdx.files.internal(FUENTE_FNT), Gdx.files.internal(FUENTE_PNG), false);
    }

    //Aqui estan todas las animaciones que usaremos
    //Para calcular bien lo que durara nuestra animacion en frames dividimos esto:
    // 1/x
    public Animation<TextureRegion> RobotAnimation(){
        return new Animation<TextureRegion>(0.17f,
                textureAtlas.findRegion("robot1"),
                textureAtlas.findRegion("robot2"),
                textureAtlas.findRegion("robot3"),
                textureAtlas.findRegion("robot4"),
                textureAtlas.findRegion("robot5"),
                textureAtlas.findRegion("robot6"));
    }

    //Animacion de salto del robot
    public Animation<TextureRegion> jumpAnimation(){
        return new Animation<TextureRegion>(0.25f,
                textureAtlas.findRegion("robot_volando1"),
                textureAtlas.findRegion("robot_volando2"),
                textureAtlas.findRegion("robot_volando3"),
                textureAtlas.findRegion("robot_volando4"));
    }

    //Animacion de muerte del robot
    public Animation<TextureRegion> deathAnimation(){
        return new Animation<TextureRegion>(0.7f,
                textureAtlas.findRegion("muerte_robot1"),
                textureAtlas.findRegion("muerte_robot2"),
                textureAtlas.findRegion("muerte_robot3"),
                textureAtlas.findRegion("muerte_robot4")
        );
    }

    //Animacion de las monedas
    public Animation<TextureRegion> coinAnimation(){
        return new Animation<TextureRegion>(0.14f,
                textureAtlas.findRegion("moneda1"),
                textureAtlas.findRegion("moneda2"),
                textureAtlas.findRegion("moneda3"),
                textureAtlas.findRegion("moneda4"),
                textureAtlas.findRegion("moneda5"),
                textureAtlas.findRegion("moneda6"),
                textureAtlas.findRegion("moneda7")
        );
    }
}
