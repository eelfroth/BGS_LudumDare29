package com.BauhausGamesSyndicate.LudumDare29.GameObjects;

import com.BauhausGamesSyndicate.LudumDare29.GameScreen;

/**
 *
 * @author Benedikt Vogler
 * @author Paul Flechsig
 * @author Jacob Bauer
 */
public class Fledermaus extends Minion{

    public Fledermaus(boolean world) {
       super(
           GameScreen.onOverworld()?GameScreen.getOverworld().getEingang().getX():500,
           0,
           "fledermaus",
           world,
           4,
           1
       );
    }  

    @Override
    public void fight(AbstractCharacter enemy) {
        //playSpacial(true);
    }
}
