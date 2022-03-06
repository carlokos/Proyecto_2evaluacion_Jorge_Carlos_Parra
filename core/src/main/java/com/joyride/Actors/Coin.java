package com.joyride.Actors;

import static com.joyride.extra.Utils.USER_COIN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Coin extends Actor {
    private Animation<TextureRegion> animation;
    private Vector2 position;
    private World world;
    private Body body;
    private Fixture fixture;
    private static final float SPEED = -2f;
    public static final int STATE_NORMAL = 0;
    public int state;

    float stateTime;

    public Coin(World world, Animation<TextureRegion> animation,Vector2 position){
        this.world = world;
        this.animation = animation;
        this.position = position;
        this.state = STATE_NORMAL;


        stateTime = 0f;

        createBody();
        createFixture();
    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();

        bodyDef.position.set(position);

        bodyDef.type = BodyDef.BodyType.KinematicBody;

        body = this.world.createBody(bodyDef);
        //metodo para que el actor se mueva
        body.setLinearVelocity(SPEED,0);
    }

    public boolean isOutOfScreen(){
        //Metodo que se encargar de indicar los objetos fuera de pantalla
        return this.body.getPosition().x <= -2f;
    }

    public void stop(){
        this.body.setLinearVelocity(0,0);
    }

    private void createFixture(){
        CircleShape circle = new CircleShape();
        circle.setRadius(0.1f);

        this.fixture = this.body.createFixture(circle, 3);
        this.fixture.setSensor(true);
        //importante para las colisiones, darle el data a la fixture en lugar de al body
        this.fixture.setUserData(USER_COIN);

        circle.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        setPosition(body.getPosition().x - 0.1f , body.getPosition().y - 0.1075f);
        batch.draw(this.animation.getKeyFrame(stateTime, true), getX(), getY(), 0.2f, 0.215f);
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public void detach(){
        this.body.destroyFixture(fixture);
        this.world.destroyBody(body);
    }
}
