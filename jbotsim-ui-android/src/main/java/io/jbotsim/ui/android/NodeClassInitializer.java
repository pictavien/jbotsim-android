package io.jbotsim.ui.android;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;

public class NodeClassInitializer extends Node implements TopologyInitializer {
    @Override
    public boolean initialize(Topology topology) {
        topology.setDefaultNodeModel(getClass());
        return true;
    }
}
