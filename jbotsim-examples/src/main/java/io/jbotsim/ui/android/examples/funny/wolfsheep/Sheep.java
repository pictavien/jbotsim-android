package io.jbotsim.ui.android.examples.funny.wolfsheep;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.icons.Icons;

import java.util.Random;

/**
 * Created by acasteig on 31/08/16.
 */
public class Sheep extends Node {
    private static final Random PRNG = new Random();
    private static final int SPEED = 1;
    private boolean isAlive = true;

    @Override
    public void onStart() {
        setIcon(Icons.SHEEP);
        setIconSize(12);
        setDirection(PRNG.nextDouble() * Math.PI * 2);
    }

    @Override
    public void onClock() {
        move(SPEED);
        wrapLocation();
    }

    @Override
    public void onPostClock() {
        Topology tp = getTopology();
        if ( ! isAlive ) {
            tp.removeNode(this);
            if (PRNG.nextDouble() < 0.5){
                tp.addNode(-1, -1, new Wolf());
            }
        }else{
            if (PRNG.nextDouble() < 0.01){
                tp.addNode(-1, -1, new Sheep());
            }
        }
    }

    public void kill(){
        isAlive = false;
    }
}
