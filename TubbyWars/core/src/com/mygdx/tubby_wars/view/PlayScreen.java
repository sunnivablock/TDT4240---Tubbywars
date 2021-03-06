package com.mygdx.tubby_wars.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.tubby_wars.TubbyWars;
import com.mygdx.tubby_wars.controller.InputProcessor;
import com.mygdx.tubby_wars.controller.MapLoader;
import com.mygdx.tubby_wars.controller.PhysicsSystem;
import com.mygdx.tubby_wars.controller.PlayerSystem;
import com.mygdx.tubby_wars.model.Assets;
import com.mygdx.tubby_wars.model.B2WorldCreator;
import com.mygdx.tubby_wars.model.CollisionListener;
import com.mygdx.tubby_wars.model.ControllerLogic;
import com.mygdx.tubby_wars.model.PlayerModel;

public class PlayScreen implements Screen {


    private OrthographicCamera gameCam;
    private Viewport viewPort;
    public TubbyWars game;
    private World world;
    private PlayerModel player1, player2;

    //MAP
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dr;

    // MAP PROPERTIES
    private int mapPixelWidth;
    private int mapWidth;
    private int tilePixelWidth;

    private TrajectoryActor trajectoryActor;
    //public Physics physics;
    private Stage stage;
    private Stage settingsStage;

    // HUD
    private Hud hud;

    private InputMultiplexer inputMultiplexer;

    private float gameCamMaxPosition, gameCamMinPosition;

    private Texture settingsB;

    private Sound click;
    private Sound hitSound;
    private Sound shotSound;

    // ASHLEY
    private Engine engine;
    private PlayerSystem ps;
    private ImmutableArray players;
    private PhysicsSystem physicsSystem;
    private Entity physicsEntity;

    public PlayScreen(TubbyWars game, Engine engine) {
        this.game = game;
        this.engine = engine;

        ps = engine.getSystem(PlayerSystem.class);
        players = engine.getEntities();
        physicsSystem = engine.getSystem(PhysicsSystem.class);
        physicsEntity = physicsSystem.getEntities().get(0);

        gameCam = new OrthographicCamera(TubbyWars.V_WIDTH, TubbyWars.V_HEIGHT);
        viewPort = new StretchViewport(TubbyWars.V_WIDTH, TubbyWars.V_HEIGHT, gameCam);
        viewPort.apply();
        gameCam.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);
        gameCam.update();

        //Button
        settingsB = Assets.getTexture(Assets.pauseGameButton);

        // INITIALIZES NEW WORLD AND STAGE
        world = new World(new Vector2(0, -9.81f), true);
        stage = new Stage();
        settingsStage = new Stage();

        // INITIALIZES PHYSICS AND THE TRAJECTORYACTOR IS ADDED TO THE STAGE.
        trajectoryActor = new TrajectoryActor(game, engine);
        stage.addActor(trajectoryActor);

        // LOADS THE MAP
        mapLoader = new TmxMapLoader();
        MapLoader loader  = new MapLoader(mapLoader);
        map = loader.getMap(ControllerLogic.roundCount);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 0.01f);

        // MAP AND CAM/VIEW PROPERTIES
        MapProperties properties = map.getProperties();
        mapWidth = properties.get("width", Integer.class);
        tilePixelWidth = properties.get("tilewidth", Integer.class);
        mapPixelWidth = mapWidth * tilePixelWidth;
        gameCamMaxPosition = mapPixelWidth / 100f - gameCam.viewportWidth / 2;
        gameCamMinPosition = gameCam.viewportWidth / 2;

        player1 = new PlayerOne(world, game,viewPort.getWorldWidth() / 2  , 1.2f, (Entity) players.get(0), engine);
        player2 = new PlayerTwo(world, game, mapPixelWidth/100f - viewPort.getWorldWidth() / 2 , 1.2f, (Entity) players.get(1), engine);
        player2.flip(true,false);
        physicsSystem.setPlayer(physicsEntity, player1);

        // Contact listener
        world.setContactListener(new CollisionListener());
        hud = new Hud(game.batch, players);

        // TODO DENNE FIKSER SETTINGSKNAPPEN, HUK AV DENNE NÅR DEN ER KLAR
        createSettingsButton();
        ControllerLogic.currentGame = this;

        //TODO: Implement in game
        click = Assets.getSound(Assets.clickSound);
        hitSound = Assets.getSound(Assets.hitSound);
        shotSound = Assets.getSound(Assets.shootingSound);
    }

    /**
     * Called when this screen becomes the current screen for a
     */
    @Override
    public void show() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(settingsStage);
        inputMultiplexer.addProcessor(new InputProcessor(physicsSystem));
        new B2WorldCreator(world, map);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        update(delta);
        //MAP RENDERING
        mapRenderer.render();
        mapRenderer.setView(gameCam);

        // PLAYER RENDERING
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player1.draw(game.batch);
        player2.draw(game.batch);
        game.batch.end();

        // STAGE RENDERING
        game.batch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
        settingsStage.draw();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    public void update(float dt) {
        world.step(1 / 60f, 6, 2);
        gameCam.update();
        player1.update(dt);
        player2.update(dt);
        hud.update(dt);

        // TODO CLEAN
        // PROHIBITS PLAYERS FROM SHOOTING WHILE A BULLET IS ACTIVE
        if(gameCam.position.x == player1.b2Body.getPosition().x || gameCam.position.x == player2.b2Body.getPosition().x){
            if(inputMultiplexer.getProcessors().size == 1){
                inputMultiplexer.addProcessor(new InputProcessor(physicsSystem));
                Gdx.input.setInputProcessor(inputMultiplexer);
            }
        }
        else{
            inputMultiplexer.clear();
            inputMultiplexer.addProcessor(settingsStage);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }

        // CHANGES TURNS
        if(ControllerLogic.isPlayersTurn && player2.getBullet() == null){
            System.out.println("Turn changed to player 1");
            ControllerLogic.isPlayersTurn = false;

        }
        else if(!ControllerLogic.isPlayersTurn && player1.getBullet() == null){
            System.out.println("Turn changed to player 2");
            ControllerLogic.isPlayersTurn = true;
        }

        //TODO Needs cleaning
        if(ControllerLogic.isPlayersTurn){
            physicsSystem.setPlayer(physicsEntity, player2);

            if(bulletOutOfBounds(player2.getBullet())){
                player2.getBullet().destroyBullet();
            }
            else if (checkBulletPosition(player2)) {
                gameCam.position.x = player2.getBullet().b2Body.getPosition().x ;
            }
            else if (checkCameraPosition(player2) ) {
                gameCam.position.x = Math.min(player2.b2Body.getPosition().x, gameCamMaxPosition);
            }
            else if(player2.b2Body.getPosition().x != player2.getPosX()){
                player2.setRedefine();
            }
        }
        else{
            physicsSystem.setPlayer(physicsEntity, player1);

            if(bulletOutOfBounds(player1.getBullet())){
                player1.getBullet().destroyBullet();
            }
            else if (checkBulletPosition(player1)) {
                gameCam.position.x = player1.getBullet().b2Body.getPosition().x;
            }
            else if (checkCameraPosition(player1)) {
                gameCam.position.x = Math.max(player1.b2Body.getPosition().x, gameCamMinPosition);
            }

            else if(player1.b2Body.getPosition().x != player1.getPosX()){
                player1.setRedefine();
            }
        }

        // TODO set players turn to the player with lowest score
        if(isRoundOver()){
            if (ControllerLogic.roundCount == 5) {
                game.gsm.changeScreen("HIGHSCORE");
                ControllerLogic.isPlayersTurn = false;
            }
            else {
                ps.setHealth((Entity)players.get(0),150);
                ps.setHealth((Entity)players.get(1),150);
                game.gsm.changeScreen("SHOP");
            }
        }
    }
/*
    //TODO RESET THE NEXT ROUND CORRECTLY, THIS IS JUST A TEST - La STÅ
    //TODO: Use ControllerLogic.roundCount to choose the right map (Changes for each round)
    // Quit game - reset players
    private void prepareForNextRound(){
        player1 = new PlayerOne(world, game,viewPort.getWorldWidth() / 2  , 0.64f, players.get(0), engine);
        player2 = new PlayerTwo(world, game, viewPort.getWorldWidth() / 2 + 3f , 0.64f, players.get(1), engine);
        // player2 = new PlayerTwo(world, game, mapPixelWidth/100f - viewPort.getWorldWidth() / 2 , 0.64f, players.get(1), engine);

        engine.getSystem(PlayerSystem.class).setHealth(players.get(0),150);
        engine.getSystem(PlayerSystem.class).setHealth(players.get(1),150);
    }

 */
    private boolean isRoundOver(){
        return engine.getSystem(PlayerSystem.class).getHealth((Entity) players.get(0)) < 0
                || engine.getSystem(PlayerSystem.class).getHealth((Entity) players.get(1)) < 0;
    }

    // TODO fix so that the settings button is clickable when playing, now the trajectory actor takes priority
    private void createSettingsButton(){
        //Initialize button to get to SettingsScreen
        final Button settingsButton = new Button(new TextureRegionDrawable(new TextureRegion(settingsB)));

        settingsButton.setSize(50, 50);
        settingsButton.setPosition(Gdx.graphics.getWidth() - (settingsButton.getWidth()*2f) , Gdx.graphics.getHeight() - (settingsButton.getWidth()*2f));

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
        // TODO Vi bør ikke lage nye screens hele tiden tror jeg, men heller ha de lagret,
        //  kan bli vanskelig å komme tilbake til playScreen hvis ikke.
        game.playSound(click);
        game.gsm.changeScreen("SETTINGS");
            }
        });
        settingsStage.addActor(settingsButton);
    }


    private boolean checkBulletPosition(PlayerModel player){
        return (player.getBullet() != null &&
                player.getBullet().b2Body.getPosition().x <= gameCamMaxPosition) &&
                player.getBullet().b2Body.getPosition().x >= gameCamMinPosition;
    }

    private boolean checkCameraPosition(PlayerModel player){
        return gameCam.position.x >= player1.b2Body.getPosition().x &&
                gameCam.position.x <= player2.b2Body.getPosition().x &&
                player.getBullet() == null;
    }

    private boolean bulletOutOfBounds(Bullet bullet){
        if(bullet != null && (bullet.b2Body.getPosition().x < 0 || bullet.b2Body.getPosition().x > mapPixelWidth / 100f)){
            return true;
        }
        else return bullet != null && bullet.b2Body.getPosition().y < 0;
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height);
        gameCam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
