package io.jbotsim.ui.android.examples.basic.moving;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.TopologyInitializer;

import java.util.Random;

/**
 * Created by acasteig on 2/20/15.
 */
public class MovingNode extends Node {
    private static final Random PRNG = new Random();

    @Override
    public void onStart() {
        setDirection(PRNG.nextDouble() * 2 * Math.PI);
    }

    @Override
    public void onClock() {
        move();
        wrapLocation(); // toroidal space
    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(Topology topology) {
            topology.setDefaultNodeModel(MovingNode.class);
            return true;
        }
    }
}

