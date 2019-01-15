package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.core.event.TopologyListener;

import io.jbotsim.core.Point;
import io.jbotsim.ui.android.TopologyInitializer;

import java.util.Random;

public class ParkCleaningExample implements TopologyListener, ClockListener, TopologyInitializer {
    private static final Random PRNG = new Random();

    Topology tp;
    Point dim;
    boolean selective = true; // moves only the nodes whose "type" property is "rwp"

    public void onNodeAdded(Node n) {
        String type = (String) n.getProperty("type");
        if ((type != null && type.equals("rwp")) || !selective)
            n.setProperty("target", new Point(PRNG.nextInt((int) dim.getX()), PRNG.nextInt((int) dim.getY())));
    }

    public void onNodeRemoved(Node n) {
    }

    public void onClock() {
        for (Node n : tp.getNodes()) {
            String type = (String) n.getProperty("type");
            if ((type != null && type.equals("rwp")) || !selective) {
                Point target = (Point) n.getProperty("target");
                n.setDirection(target);
                n.move(5);
                if (n.distance(target) < 5)
                    n.setProperty("target", new Point(PRNG.nextInt((int) dim.getX()), PRNG.nextInt((int) dim.getY())));
            }
        }
    }

    @Override
    public boolean initialize(Topology topology) {
        tp = topology;
        tp.setNodeModel("UAV", UAV.class);
        tp.setNodeModel("Robot blue", RobotBlue.class);
        tp.setNodeModel("Robot red", RobotRed.class);
        tp.setNodeModel("Garbage blue", GarbageBlue.class);
        tp.setNodeModel("Garbage red", GarbageRed.class);

        dim = new Point(tp.getWidth(), tp.getHeight());

        tp.addTopologyListener(this);
        tp.addClockListener(this, 2);

        return true;
    }
}