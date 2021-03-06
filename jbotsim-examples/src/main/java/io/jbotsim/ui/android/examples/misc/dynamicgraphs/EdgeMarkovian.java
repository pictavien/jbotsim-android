package io.jbotsim.ui.android.examples.misc.dynamicgraphs;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.gen.dynamic.graph.EMEGPlayer;
import io.jbotsim.gen.dynamic.graph.TVG;
import io.jbotsim.ui.android.TopologyInitializer;

public class EdgeMarkovian implements TopologyInitializer {
    @Override
    public boolean initialize(Topology tp) {
        tp.setTimeUnit(100);
        TVG tvg = new TVG(Node.class);
        tvg.buildCompleteGraph(10);
        (new EMEGPlayer(tvg, tp, .02, .6)).start();

        return true;
    }
}
