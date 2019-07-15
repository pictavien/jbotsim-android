package io.jbotsim.ui.android.examples.misc.mobilitymodels;

import io.jbotsim.core.Point;

import io.jbotsim.ui.android.NodeClassInitializer;

import java.util.Random;

public class DavidNode extends NodeClassInitializer{
	private static final Random PRNG = new Random();
	Point vec;

    @Override
    public void onStart() {
		vec = new Point(0.0,0.0);
    }

    @Override
    public void onClock() {
		double randx = ((PRNG.nextInt(3)-1)/10.0);
		double randy = ((PRNG.nextInt(3)-1)/10.0);

		vec.setLocation(vec.getX()+randx, vec.getY()+randy);
        Point next = new Point(getX()+vec.getX(),getY()+vec.getY());
		setLocation(next);
		wrapLocation();
	}
}
