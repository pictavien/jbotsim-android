package io.jbotsim.ui.android.examples.misc.dynamicgraphs;

import io.jbotsim.core.Link;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.TopologyInitializer;

import java.util.List;
import java.util.Random;

/**
 * Created by acasteig on 17/03/15.
 */
public class PopulationProtocol implements ClockListener {
    Topology topology;
    Random random = new Random();

    public PopulationProtocol(Topology topology) {
        this.topology = topology;
        topology.addClockListener(this, 10);
    }

    @Override
    public void onClock() {
        List<Link> links = topology.getLinks();

        for (Link link : links)
            link.setWidth(1);
        if (links.size()>0) {
            Link link = links.get(random.nextInt(links.size()));
            link.setWidth(4);
            interact(link);
        }
    }

    private void interact(Link link){

    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(Topology tp) {
            new PopulationProtocol(tp);
            return true;
        }
    }
}
