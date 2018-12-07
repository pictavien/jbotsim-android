package io.jbotsim.ui.android.examples.misc.dynamicgraphs;

import io.jbotsim.Connectivity;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.dynamicity.graph.EMEGPlayer;
import io.jbotsim.dynamicity.graph.TVG;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.android.ViewerActivityInitializer;


public class NbComponentsEG implements ViewerActivityInitializer {
    @Override
    public boolean initialize(AndroidViewerActivity activity) {
        TVG tvg = new TVG(Node.class);
        tvg.buildCompleteGraph(12);
        Topology tp = activity.getTopology();
        tp.setClockSpeed(100);

        (new EMEGPlayer(tvg, tp, .05, .6)).start();
        tp.addClockListener(new ClockListener() {
            @Override
            public void onClock() {
                int i = Connectivity.splitIntoConnectedSets(tp.getNodes()).size();
                activity.getViewer().setClientInfo("# components = " + i);
            }
        });

        return true;
    }
}
