package io.jbotsim.ui.android.examples.basic.spanningtree;

import io.jbotsim.core.Link;
import io.jbotsim.core.Message;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.StartListener;
import io.jbotsim.ui.android.TopologyInitializer;

public class SpanningTreeNode extends Node {
    Node parent;

    @Override
    public void onStart() {
        parent = null;
    }

    @Override
    public void onSelection() {
        parent = this;
        sendAll(new Message());
    }

    @Override
    public void onMessage(Message message) {
        if ( parent == null ){
            parent = message.getSender();
            getCommonLinkWith(parent).setWidth(4);
            sendAll(new Message());
        }
    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(final Topology tp) {
            tp.setDefaultNodeModel(SpanningTreeNode.class);
            tp.addStartListener(new StartListener() { // optional
                // reset links upon restart
                @Override
                public void onStart() {
                    for (Link link : tp.getLinks()){
                        link.setWidth(1);
                    }
                }
            });

            for (int i = 0; i < 7; i++){
                for (int j = 0; j < 5; j++) {
                    tp.addNode(50 + i * 80, 50 + j * 80);
                }
            }
            return true;
        }
    }
}
