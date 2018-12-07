package io.jbotsim.ui.android.examples.centralized;

import io.jbotsim.Algorithms;
import io.jbotsim.Connectivity;
import io.jbotsim.core.Link;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.TopologyInitializer;

import java.util.List;


public class MST implements ClockListener {
	Topology tp;
	public MST(Topology tp){
		this.tp = tp;
		tp.addClockListener(this, 10);
	}

	protected void updateMST(){
		if (Connectivity.isConnected(tp)) {
            List<Link> mstLinks = Algorithms.getMST(tp);
            for (Link l : tp.getLinks())
                l.setWidth(mstLinks.contains(l) ? 5 : 0);
        }else{
            for (Link l : tp.getLinks())
                l.setWidth(1);
        }
	}
	public static class Initializer implements TopologyInitializer {
		@Override
		public boolean initialize(Topology tp) {
			new MST(tp);
			return true;
		}
	}

    @Override
    public void onClock() {
        updateMST();
    }
}
