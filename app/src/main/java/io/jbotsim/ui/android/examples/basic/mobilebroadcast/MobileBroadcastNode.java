package io.jbotsim.ui.android.examples.basic.mobilebroadcast;

import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;
import io.jbotsim.core.PRNG;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.TopologyInitializer;

public class MobileBroadcastNode extends Node {

    boolean informed;

    @Override
    public void onStart() {
        setDirection(Math.PI * 2 * PRNG.nextDouble());
        informed = false;
        setColor(null); // optional (for restart only)
    }

    @Override
    public void onSelection() {
        informed = true;
        setColor(Color.red);
    }

    @Override
    public void onClock() {
        if (informed)
            sendAll(new Message());
        move();
        wrapLocation();
    }

    @Override
    public void onMessage(Message message) {
        informed = true;
        setColor(Color.red);
    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(Topology topology) {
            topology.setDefaultNodeModel(MobileBroadcastNode.class);

            return true;
        }
    }
}
