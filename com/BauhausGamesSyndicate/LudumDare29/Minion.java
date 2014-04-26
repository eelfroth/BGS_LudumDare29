/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.BauhausGamesSyndicate.LudumDare29;

import com.BauhausGamesSyndicate.LudumDare29.overworld.Overworld;


/**
 *
 * @author Paul
 */
public class Minion extends AbstractCharacter{
    
    public Minion(float x, float y){
        super(x, y, "minion");
    }
    
    @Override
    public void update(float x, float y){
        setX(getX() + getSpeed() );
        setY(Overworld.getHeightmapValue((int) x));
    }

    @Override
    public void render(GameScreen gs){
        draw(gs.getBatch());
    }
}