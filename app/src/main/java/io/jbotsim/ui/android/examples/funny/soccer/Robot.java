package io.jbotsim.ui.android.examples.funny.soccer;

import io.jbotsim.core.Node;
import io.jbotsim.core.PRNG;

/**
 * Created by Arnaud Casteigts on 06/04/17.
 */
public class Robot extends Node {

    @Override
    public void onStart() {
        setSize(14);
        setIcon("soccer_robot");
        setDirection(PRNG.nextDouble()*Math.PI*2.0);
        setSensingRange(getSize()+10);
    }

    @Override
    public void onClock() {
        move(5);
        setDirection(getTopology().getNodes().get(0).getLocation());
//        wrapLocation();
    }

    @Override
    public void onSensingIn(Node node) {
        if (node instanceof Ball){
            ((Ball) node).shoot(getDirection(), PRNG.nextDouble()*50);
        }
    }
}
