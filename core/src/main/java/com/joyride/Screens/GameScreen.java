package com.joyride.Screens;

import static com.joyride.extra.Utils.SCREEN_HEIGHT;
import static com.joyride.extra.Utils.SCREEN_WIDTH;
import static com.joyride.extra.Utils.USER_COIN;
import static com.joyride.extra.Utils.USER_FLOOR;
import static com.joyride.extra.Utils.USER_ROBOT;
import static com.joyride.extra.Utils.USER_SPIKE;
import static com.joyride.extra.Utils.WORLD_HEIGHT;
import static com.joyride.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.joyride.Actors.Coin;
import com.joyride.Actors.Robot;
import com.joyride.Actors.Spike_ball;
import com.joyride.MainGame;

import java.util.Iterator;
import java.util.ListIterator;

public class GameScreen extends  BaseScreen implements ContactListener {
    private Stage stage;
    private Image background;
    private Robot robot;

    //Array de nuestros objetos
    private Array<Spike_ball> arrayPinchos;
    private Array<Coin> arrayMonedas;

    //boolean que nos ayuda a manejar nuestra animacion del robot, diferenciando si esta
    // o no en el aire
    private boolean Andando = true;

    //contador que indicadad nuestra puntuacion
    public static int contador;

    //Estos numeros de spawn son importantes ya que queremos evitar que salgan una bola de pinchos
    // y una moneda en la misma posicion, asi que no pueden coincidir en ningun momento
    private final float TIME_TO_SPAWN_SPIKES = 1.5f;
    private float timeToCreateSpike;
    private  final float TIME_TO_SPAWN_COINS = 2.3f;
    private float timeToCreateCoin;

    private Music musicbg;

    private World world;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera ortCamera;

    private OrthographicCamera fontCamera;
    private BitmapFont score;

    public GameScreen(MainGame mainGame){
        super(mainGame);

        this.world = new World(new Vector2(0, -10), true);
        this.world.setContactListener(this);
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage = new Stage(fitViewport);

        this.arrayPinchos = new Array();
        this.arrayMonedas = new Array();
        this.timeToCreateSpike = 0f;
        this.timeToCreateCoin = 0f;

        this.musicbg = this.mainGame.assetManager.getMusicBG();

        this.ortCamera = (OrthographicCamera) this.stage.getCamera();
        this.debugRenderer = new Box2DDebugRenderer();

        contador = 0;
        prepareScore();
    }

    /* AQUI PONEMOS EL STAGE BONITO */

    private void addFloor(){
        /*
        *   preparamos el suelo
        * */
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(WORLD_WIDTH / 2f, 0.6f);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape suelo = new PolygonShape();
        suelo.setAsBox(WORLD_WIDTH/2, 0.101f);

        Fixture fixture = body.createFixture(suelo, 3);
        body.createFixture(suelo, 3);
        fixture.setUserData(USER_FLOOR);

        suelo.dispose();
    }

    private void addTecho(){
        //nuestro techo aunque sea se tiene que ver un poquito, ya que si no
        //el robot se queda a veces volando cuando queremos bajar, tambien
        //necesitamos techo ya que nuestro robot vuela indefinidamente
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,WORLD_HEIGHT - 0.001f ,WORLD_WIDTH,WORLD_HEIGHT - 0.001f);
        body.createFixture(edge, 1);
        edge.dispose();
    }

    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage.addActor(this.background);
    }

    private void prepareScore(){
        this.score = this.mainGame.assetManager.getFont();
        this.score.getData().scale(0.005f);

        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCamera.update();
    }

    /* AQUI TRABAJAMOS CON LOS ACTORES */

    private void addRobot(){
        Animation<TextureRegion> robotSprite = mainGame.assetManager.RobotAnimation();
        //el sonido solo se activa cuando se pasamos a gameover
        Sound sound = this.mainGame.assetManager.getDeathSound();
        this.robot = new Robot(this.world,robotSprite,sound ,new Vector2(1.25f,  1.4f));
        this.stage.addActor(this.robot);
    }

    public void addSpikes(float delta){
        //rellenamos la array segun el tiempo de delta y de nuestros parametros
        TextureRegion imagenPinchos = mainGame.assetManager.getSpikedBall();

        if(robot.state == robot.STATE_NORMAL){
            this.timeToCreateSpike+=delta;

            if(this.timeToCreateSpike >= TIME_TO_SPAWN_SPIKES){
                this.timeToCreateSpike-=TIME_TO_SPAWN_SPIKES;
                float floatY = MathUtils.random(0.9f, WORLD_HEIGHT - 0.5f);
                Spike_ball ball = new Spike_ball(this.world, imagenPinchos, new Vector2(WORLD_WIDTH,floatY));
                arrayPinchos.add(ball);
                this.stage.addActor(ball);
            }
        }
    }

    public void addCoins(float delta){
        //rellenamos la array segun el tiempo de delta y de nuestros parametros
        Animation<TextureRegion> animation = mainGame.assetManager.coinAnimation();

        if(robot.state == robot.STATE_NORMAL){
            this.timeToCreateCoin+=delta;

            if(this.timeToCreateCoin >= TIME_TO_SPAWN_COINS){
                this.timeToCreateCoin -= TIME_TO_SPAWN_COINS;
                float floatY = MathUtils.random(0.9f,WORLD_HEIGHT - 0.5f);
                Coin coin = new Coin(this.world, animation, new Vector2(WORLD_WIDTH, floatY));
                arrayMonedas.add(coin);
                this.stage.addActor(coin);
            }
        }
    }
    /* Metodos para quitar los objetos fuera de la pantalla */
    public void removeSpikes(){
        for(Spike_ball spike : this.arrayPinchos){
            if(!world.isLocked()){
                if(spike.isOutOfScreen()){
                    spike.detach();
                    spike.remove();
                    arrayPinchos.removeValue(spike, false);
                }
            }
        }
    }

    public void removeCoins(){
        for(Coin coin : this.arrayMonedas){
            if(!world.isLocked()){
                if(coin.isOutOfScreen()){
                    coin.detach();
                    coin.remove();
                    arrayMonedas.removeValue(coin, false);
                }
            }
        }
    }


    /* METODOS DONDE IMPLEMENTAREMOS TODO LO ANTERIOR */

    @Override
    public void show() {
        addBackground();
        addFloor();
        addTecho();
        addRobot();
        this.musicbg.setLooping(true);
        this.musicbg.play();
    }

    @Override
    public void render(float delta) {
        /*Ponemos todos los parametros que necesitamos que se vayan actualizando
        * importante limpiamos la pantalla antes de hacer nada para evitar problemas con
        * los cambios de pantalla
        * */
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /* vamos añadiendo segund delta */
        addSpikes(delta);
        addCoins(delta);

        this.stage.getBatch().setProjectionMatrix(ortCamera.combined);
        this.stage.act();
        this.world.step(delta,6,2);
        this.stage.draw();

        this.ortCamera.update();
        this.debugRenderer.render(this.world, this.ortCamera.combined);

        /* vamos removiendo los objetos que esten fuera de la pantalla */
        removeSpikes();
        removeCoins();

        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.score.draw(this.stage.getBatch(), "" + contador,780, 445);
        this.stage.getBatch().end();

        //Importante siempre que sea true hay que cambiarlo para que una, cuando vuelva a tocar
        // el suelo este if vuelva a funcionar, y dos, para que no vuele caminando.
        //Este if cambia la animacion de caminar al robot
        if(Andando){
            Andando = false;
            this.robot.tocaSuelo();
        }
    }

    //Metodo que usaremos para eliminar una moneda concreta
    //para eso usaremos un iterator y cogeremos solo la moneda que haga contacto con .next()
    //sumamos uno al contador y quitamos la moneda
    private void eliminarMoneda() {
        if (robot.state == 0) {
            Iterator<Coin> listaMonedas = arrayMonedas.iterator();
            Coin coin = listaMonedas.next();
            //comprobamos si la moneda ya se ha "cogido" ya que la hitbox se queda
            //solo desaparece la imagen y no queremos sumar de más
            if(coin.state == 0) {
                contador++;
                coin.remove();
                coin.state = 1;
            }
        }
    }

    @Override
    public void hide() {
        this.robot.detach();
        this.robot.remove();
        //intentamos esconder cada objeto que tengamos de las arrays
        for(Coin coin : this.arrayMonedas){
            coin.detach();
            coin.remove();
        }
        for(Spike_ball ball : this.arrayPinchos){
            ball.detach();
            ball.remove();
        }
        this.musicbg.pause();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.world.dispose();
    }

    /* AQUI TRABAJAMOS LAS COLISIONES */
        /* Se que hay mejores metodos que con puros ifs, pero no se porque el metodo
        *   que nos dio y esta en github me daba error asi que he intentado ingeniarmelas para que
        *   funcione aunque este menos optimizado
        * */

    @Override
    public void beginContact(Contact contact) {
        Fixture fA = contact.getFixtureA(), fB = contact.getFixtureB();
        //En teoria solo el robot tocara el suelo así que bastara con saber si el suelo
        // ha chocado con algo, esto activara de vuelta la animacion de caminar del robot
        //mediante el if de arriba
        if(fA.getUserData() == USER_FLOOR || fB.getUserData() == USER_FLOOR){
            Andando = true;
        }

        //En teoria las monedas y los pinchos nunca se chocan asi que de nuevo,
        // vale con comprobar si algunos de ellos se choca
        if(fA.getUserData() == USER_COIN || fB.getUserData() == USER_COIN){
            //cada vez que toquemos una moneda llamamos al metodo que eliminar esa
            //moneda concreta
            eliminarMoneda();
        }else if(fA.getUserData() == USER_SPIKE || fB.getUserData() == USER_SPIKE){
            this.robot.RoboDead();
            for(Spike_ball ball : this.arrayPinchos){
                ball.stop();
            }

            for(Coin coin : this.arrayMonedas){
                coin.stop();
            }
            this.musicbg.stop();
            this.stage.addAction(Actions.sequence(
                    Actions.delay(2.4f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            mainGame.setScreen(new GameOverScreen(mainGame));
                        }
                    })
            ));
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
