package com.mygdx.tubby_wars.desktop;

import com.mygdx.tubby_wars.backend.IBackend;

import java.util.ArrayList;
import java.util.List;

public class connection implements IBackend {
    List<String> emptyList;

    @Override
    public void connect() {

    }

    @Override
    public List<String> getTopTen() {
        emptyList= new ArrayList<>();
        return emptyList;
    }

    @Override
    public void addResult(String name, int score) {
        
    }
}
