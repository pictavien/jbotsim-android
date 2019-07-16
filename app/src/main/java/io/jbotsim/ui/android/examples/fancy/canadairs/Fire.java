package io.jbotsim.ui.android.examples.fancy.canadairs;

import io.jbotsim.core.Node;

import java.util.ArrayList;
import java.util.Random;

/* Gardez cette classe telle quelle */
public class Fire extends Node {
    private static final Random PRNG = new Random();
    static ArrayList<Fire> allFires = new ArrayList<Fire>();

	public Fire(){
        disableWireless();
        allFires.add(this);
        setIcon("fire");
        setSize(10);
	}
	public void onClock(){
        if (PRNG.nextDouble() < 0.01)
            propagate();
	}
    public void propagate(){
        if (allFires.size() < 100) {
            double x = getX() + PRNG.nextDouble() * 20 - 10;
            double y = getY() + PRNG.nextDouble() * 20 - 10;
            for (Fire fire : allFires)
                if (fire.distance(x, y) < 10)
                    return;
            getTopology().addNode(x, y, new Fire());
        }
    }
    public void die(){
        allFires.remove(this);
        getTopology().removeNode(this);
    }
}
