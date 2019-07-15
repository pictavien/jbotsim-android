package io.jbotsim.ui.android.examples.misc.mobilitymodels;

import io.jbotsim.core.Point;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.TopologyInitializer;

import java.util.Random;

public class GlobalRWP implements ClockListener, TopologyInitializer {
    private static final Random PRNG = new Random();
    Topology tp;

    public void onClock() {
        for (Node n : tp.getNodes()) {
            Point target = (Point) n.getProperty("target");
            if (target == null || n.getLocation().distance(target) < 2) {
                target = new Point(PRNG.nextDouble() * tp.getWidth(),
                        PRNG.nextDouble() * tp.getHeight());
                n.setProperty("target", target);
            }
            n.setDirection(target);
            n.move();
        }
    }

    @Override
    public boolean initialize(Topology topology) {
        tp = topology;
        tp.addClockListener(this);
        return true;
    }
}
