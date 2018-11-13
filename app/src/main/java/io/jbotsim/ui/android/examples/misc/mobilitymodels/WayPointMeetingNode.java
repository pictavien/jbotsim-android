package io.jbotsim.ui.android.examples.misc.mobilitymodels;

import io.jbotsim.core.Node;
import io.jbotsim.core.PRNG;
import io.jbotsim.core.Point;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.NodeClassInitializer;

/**
 * Created by acasteig on 14/06/15.
 */
public class WayPointMeetingNode extends NodeClassInitializer implements ArrivalListener {

    Point refPoint;
    WayPointMover mover;

    @Override
    public void onStart() {
        refPoint = getLocation();
        mover = new WayPointMover(this);
        mover.addArrivalListener(this);
        onArrival();
    }

    @Override
    public void onArrival() {
        if (getTime() % 1000 < 200) {
            mover.addDestination(300, 50);
        }
        else
            mover.addDestination(refPoint.getX()+(PRNG.nextDouble()*100)-50,
                refPoint.getY()+(PRNG.nextDouble()*100)-50);
    }
}
