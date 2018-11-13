package io.jbotsim.ui.android.examples.misc.mobilitymodels;

import io.jbotsim.ui.android.NodeClassInitializer;

public class CircleMovement extends NodeClassInitializer {
	@Override
	public void onClock() {
		setDirection((getDirection()-0.1)%(2.0*Math.PI));
		move(3);
	}
}