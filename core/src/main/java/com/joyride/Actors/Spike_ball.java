package com.joyride.Actors;

import static com.joyride.extra.Utils.USER_SPIKE;

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

public class Spike_ball extends Actor {
    private static final float SPIKE_WIDTH = 1f;
    private static final float SPIKE_HEIGHT = 1f;
    private static final float SPEED = -2f;


    private TextureRegion imagen;
    private Body body;
    private Fixture fixture;
    private World world;

    public Spike_ball(World world, TextureRegion image, Vector2 position){
        this.world = world;
        this.imagen = image;

        createSpikedBody(position);
        createFixture();
    }

    private void createSpikedBody(Vector2 position){
        BodyDef def = new BodyDef();
        def.position.set(position);
        def.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(def);
        body.setLinearVelocity(SPEED,0);
    }

    public boolean isOutOfScreen(){
        //metodo que se encarga de indicar los objetos que estan fuera de la pantalla
        return this.body.getPosition().x <= -2f;
    }

    private void createFixture(){
        CircleShape shape = new CircleShape();
        shape.setRadius(0.2f);

        this.fixture = body.createFixture(shape, 8 );
        this.fixture.setUserData(USER_SPIKE);
        shape.dispose();
    }

    public void stop(){
        this.body.setLinearVelocity(0,0);
    }

    @Override
    public void act(float delta){
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        setPosition(body.getPosition().x, body.getPosition().y);
        batch.draw(this.imagen, body.getPosition().x - SPIKE_WIDTH/1.9f,body.getPosition().y - SPIKE_HEIGHT/2.2f,
                SPIKE_WIDTH, SPIKE_HEIGHT);
    }

    public void detach(){
       body.destroyFixture(fixture);
       world.destroyBody(body);
    }
}
