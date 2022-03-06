package com.joyride.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.joyride.extra.AssetMan;
import com.joyride.extra.Utils;

public class Robot extends Actor {
    private Animation<TextureRegion> robotAnimation;
    private Vector2 position;
    private World world;

    private Body body;
    private Fixture fixture;
    public int state;
    private Sound sound;
    private AssetMan assetManager = new AssetMan();

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DEAD = 1;
    private static final float JUMP_SPEED = 3.4f;

    float statetime;

    public Robot(World world, Animation<TextureRegion> animation, Sound sound, Vector2 position) {
        //constructor con todos los parametros necesarios
        this.robotAnimation = animation;
        this.position = position;
        this.world = world;
        this.state = STATE_NORMAL;
        this.sound = sound;

        statetime = 0f;

        createBody();
        createFixture();
    }

    public void createBody(){
        BodyDef bodyDef = new BodyDef();

        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        this.body = this.world.createBody(bodyDef);
    }

    public void createFixture(){
        PolygonShape polygon = new PolygonShape();
        //he hecho la hitbox un poquito mas pequeña que el sprite ya que te daba cuando no debia
        polygon.setAsBox(0.45f, 0.55f);

        //segun la documentacion de lbdgx la densidad influye directamente en el peso
        this.fixture = this.body.createFixture(polygon,1000);

        this.fixture.setUserData(Utils.USER_ROBOT);

        polygon.dispose();
    }

    public void RoboDead(){
        //metodo que cambia el estado del actor para pasar a game over,
        // tambien suena el sonidito y cambia la animacion
        this.state = STATE_DEAD;
        //intente que se quedase en el sitio al morir cambiando el body a static pero no funciona
        // agradeceria si puedras poder como hacerlo
        this.sound.play();
        this.robotAnimation = assetManager.deathAnimation();
        this.statetime = 0;
    }

    public void tocaSuelo(){
        //metodo que maneja la animacion del suelo para que no haya problemas
        if(this.state == STATE_NORMAL) {
            this.robotAnimation = this.assetManager.RobotAnimation();
        }
    }

    @Override
    public void act(float delta) {
        /*
        * nuestro robot puede volar indefinidamente, asi que tiene una animacion especial para ello
        * siempre que se este tocando la pantalla estara elevandose hasta chocarse con el techo
        * */
        boolean jump = Gdx.input.isTouched();

        if(jump && this.state == STATE_NORMAL){
            this.body.setLinearVelocity(0, JUMP_SPEED);
            this.robotAnimation = this.assetManager.jumpAnimation();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //para que la hitbox encaje con el actor tenemos que restar la mitad de la position del actor
        // esto se debe a como funciona el eje de coordenadas del mundo y de la camara
        setPosition(body.getPosition().x - 0.45f ,body.getPosition().y - .65f);
        batch.draw(this.robotAnimation.getKeyFrame(statetime, true), getX(), getY(), 0.9f, 1.3f);
        statetime += Gdx.graphics.getDeltaTime();
    }

    public void detach(){
        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);
    }
}
