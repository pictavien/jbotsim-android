package io.jbotsim.ui.android.examples.funny.wolfsheep;

import io.jbotsim.ui.android.TopologyInitializer;
import io.jbotsim.core.Topology;

public class WolfSheepExample implements TopologyInitializer {
    @Override
    public boolean initialize(Topology tp) {
        tp.setDimensions (800, 600);
        tp.setTimeUnit(20);
        tp.setDefaultNodeModel(Wolf.class);
        tp.disableWireless();
        tp.pause();
        for (int i = 0; i < 5; i++){
            // 3 randomly located wolves and sheeps.
            tp.addNode(-1, -1, new Wolf());
            tp.addNode(-1, -1, new Sheep());
        }
        return true;
    }
}
