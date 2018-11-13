package io.jbotsim.ui.android.examples.fancy.canadairs;

import io.jbotsim.ui.android.TopologyInitializer;
import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.LinkResolver;

import io.jbotsim.core.Color;

/**
 * Created by acasteig on 22/03/15.
 */
public class CanadairsExample extends LinkResolver implements TopologyInitializer {
    @Override
    public boolean isHeardBy(Node n1, Node n2) {
        if ((n1 instanceof Sensor && n2 instanceof Canadair) ||
                (n2 instanceof Sensor && n1 instanceof Canadair))
            return false;
        return (n1.isWirelessEnabled() && n2.isWirelessEnabled()
                && n1.distance(n2) < n1.getCommunicationRange());
    }

    public void createMap(Topology topology){
        for (int i=0; i<6; i++)
            for (int j=0; j<4; j++)
                topology.addNode(i*100+180-(j%2)*30, j*100+100, new Sensor());
        topology.addNode(50, 400, new Station());
        for (Link link : topology.getLinks())
            link.setColor(Color.gray);
        topology.addNode(50, 500, new Canadair());
        topology.addNode(100, 500, new Canadair());
        topology.addNode(50, 50, new Lake());
    }

    @Override
    public boolean initialize(Topology topology) {
        topology.setDimensions(800,600);
        topology.setLinkResolver(this);
        topology.getMessageEngine().setSpeed(10);
        createMap(topology);
        topology.setClockSpeed(30);
        topology.setDefaultNodeModel(Fire.class);
        return true;
    }
}
