package io.jbotsim.ui.android.examples.basic.broadcasting;

import io.jbotsim.core.Color;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.TopologyInitializer;

/**
 * Created by acasteig on 10/03/15.
 */
public class BroadcastingNode extends Node {
    boolean informed;

    @Override
    public void onStart() {
        informed = false;
        setColor(null); // optional (for restart only)
    }

    @Override
    public void onSelection() {
        informed = true;
        setColor(Color.red);
        sendAll(new Message());
    }

    @Override
    public void onMessage(Message message) {
        if ( ! informed ){
            informed = true;
            setColor(Color.red);
            sendAll(message);
        }
    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(Topology topology) {
            topology.setDefaultNodeModel(BroadcastingNode.class);
            topology.setTimeUnit(500);

            for (int i = 0; i < 7; i++){
                for (int j = 0; j < 5; j++) {
                    topology.addNode(50 + i * 80, 50 + j * 80);
                }
            }
            return true;
        }
    }
}
