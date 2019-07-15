package io.jbotsim.ui.android.examples.misc.spanningforest;

import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.TopologyInitializer;


public class SpanningForestExample implements TopologyInitializer {
	@Override
	public boolean initialize(Topology topology) {
		new Forest(topology);
		return true;
	}
}
