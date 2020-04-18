package com.mygdx.tubby_wars.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tubby_wars.TubbyWars;
import com.mygdx.tubby_wars.model.Assets;

public class HighScoreScreen extends ScreenAdapter implements ScreenInterface{

    private TubbyWars game;
    private Engine engine;

    //Textures for title of page and the background
    private Texture title;
    private Texture background;

    //Textures for buttons
    private Texture backB;

    private Sound click;

    private Stage stage;

    public HighScoreScreen(TubbyWars game, Engine engine){
        super();
        this.game = game;
        this.engine = engine;

        background = Assets.getTexture(Assets.mainBackground);
        backB = Assets.getTexture(Assets.backButton);
        title = Assets.getTexture(Assets.highscoreTitle);

        this.click = game.getClickSound();

        // one-time operations
        create();
    }

    public void create(){
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        //Initialiserer button to get GameScreen
        final Button menuButton = new Button(new TextureRegionDrawable(new TextureRegion(backB)));
        menuButton.setSize(60, 60);
        menuButton.setPosition(Gdx.graphics.getWidth() / 2 - menuButton.getWidth() / 2 , Gdx.graphics.getHeight() / 6 - menuButton.getHeight() / 2);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float xpos, float ypos) {
                game.playSound(click);
                game.setScreen(new MenuScreen(game, engine));
            }

        });

        stage.addActor(menuButton);
    }

    @Override
    public void update(float dt){
        // check for user input
        handleinput();
    }

    @Override
    public void draw(){
        game.getBatch().begin(); // Draw elements to Sprite Batch
        game.getBatch().draw(background, 0,0, TubbyWars.WIDTH, TubbyWars.HEIGHT); //Draws background photo
        game.getBatch().draw(title,Gdx.graphics.getWidth()/2 - 200,Gdx.graphics.getHeight()/2,400,100); //Draws logo
        game.getBatch().end();

        stage.draw();
    }

    @Override
    public void handleinput(){
    }

    @Override
    public void render(float dt){
        update(dt);
        draw();
    }

    @Override
    public void dispose(){
        super.dispose();
    }
}