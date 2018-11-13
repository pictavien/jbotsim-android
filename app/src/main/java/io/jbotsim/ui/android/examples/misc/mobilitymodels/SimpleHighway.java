package io.jbotsim.ui.android.examples.misc.mobilitymodels;

import io.jbotsim.core.Node;
import io.jbotsim.core.PRNG;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.core.event.TopologyListener;
import io.jbotsim.ui.android.TopologyInitializer;

public class SimpleHighway implements ClockListener, TopologyListener, TopologyInitializer {
	Topology tp;
	boolean voie=true;

	@Override
	public boolean initialize(Topology topology) {
		this.tp=topology;
		tp.addTopologyListener(this);
		tp.addClockListener(this);
		return true;
	}

	public void onNodeAdded(Node n) {
		n.setProperty("speed", new Double(PRNG.nextDouble()*50+30));
		n.setLocation(n.getX(),voie?200:186);
		n.setDirection(0);
        voie=!voie;
	}
	public void onClock() {
		for (Node n : tp.getNodes()){
			n.move((Double)n.getProperty("speed")/16.0);
			if (n.getX()>tp.getWidth())
				n.setLocation(0, n.getY());
		}
	}
	public void onNodeRemoved(Node n) {}
}
