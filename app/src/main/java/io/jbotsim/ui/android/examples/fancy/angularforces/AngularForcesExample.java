package io.jbotsim.ui.android.examples.fancy.angularforces;

import io.jbotsim.ui.android.TopologyInitializer;
import io.jbotsim.core.Topology;

/**
 * Created by Arnaud Casteigts on 15/03/17.
 */
public class AngularForcesExample implements TopologyInitializer  {
    @Override
    public boolean initialize(Topology tp) {
        tp.setDimensions(400,300);
        tp.setCommunicationRange(70);
        Forces.Dth=tp.getCommunicationRange()*0.851;
        tp.setSensingRange(Forces.Dth / 2);
        tp.setDefaultNodeModel(Robot.class);
        tp.setClockSpeed(6);

        return true;
    }
}
