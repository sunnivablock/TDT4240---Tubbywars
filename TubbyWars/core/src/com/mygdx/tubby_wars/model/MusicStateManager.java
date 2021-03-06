package com.mygdx.tubby_wars.model;
import com.mygdx.tubby_wars.TubbyWars;
import com.badlogic.gdx.audio.Music;

public class MusicStateManager {
    private TubbyWars game;
    private boolean mute; //Checks if the music is muted

    public MusicStateManager(TubbyWars game) {
        this.game = game;
        this.mute = false;
    }

    public void muteMusic() {
        mute = true;
    }

    public void unmuteMusic() {
        mute = false;
    }

    public Boolean getMuteMusicState() {
        return mute;
    }
}
