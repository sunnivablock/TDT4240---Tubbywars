package com.mygdx.tubby_wars.model;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.tubby_wars.TubbyWars;
import com.mygdx.tubby_wars.controller.PlayerSystem;
import com.mygdx.tubby_wars.model.components.PlayerComponent;
import com.mygdx.tubby_wars.view.Bullet;
import com.mygdx.tubby_wars.view.Healthbar;
import com.mygdx.tubby_wars.view.Weapon;

public abstract class PlayerModel extends Sprite {


    protected World world;
    public Body b2Body;
    public Weapon weapon;
    public TubbyWars game;
    protected boolean showBullet = false;
    protected float posX, posY;

    // ASHLEY
    private Entity playerEntity;
    private ComponentMapper<PlayerComponent> pm;
    private Engine engine;

    public PlayerModel(World world, TubbyWars game, float posX, float posY, Entity playerEntity, Engine engine) {
        this.world = world;
        this.game = game;
        this.posX = posX;
        this.posY = posY;

        // ASHLEY
        this.playerEntity = playerEntity;
        // used to get variables from components
        pm = ComponentMapper.getFor(PlayerComponent.class);
        this.engine = engine;
    }

    public abstract void update(float dt);

    public abstract void addBullet();

    public abstract void definePlayer();

    public abstract void redefinePlayer();

    public abstract void setRedefine();

    public abstract Bullet getBullet();


    public void showBullet(){
        showBullet = true;
    }
    protected void hideBullet(){
        showBullet = false;
    }

    protected boolean isPlayersTurn(){
        return (ControllerLogic.isPlayersTurn);
    }

    public Vector2 getStartPoint(){
        return b2Body.getPosition();
    }

    public float getPosX(){
        return posX;
    }


    public PlayerComponent getPlayerComponent(){
        return pm.get(playerEntity);
    }

    Entity getPlayerEntity(){
        return playerEntity;
    }

    PlayerSystem getPlayerSystem(){
        return engine.getSystem(PlayerSystem.class);
    }



}
