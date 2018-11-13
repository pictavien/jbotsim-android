package io.jbotsim.ui.android.examples.misc.randomwalks;

import io.jbotsim.core.Node;

import io.jbotsim.core.Color;
import io.jbotsim.core.PRNG;
import io.jbotsim.ui.android.NodeClassInitializer;

/**
 * Created by acasteig on 17/06/15.
 */
public class RandomWalkNode extends NodeClassInitializer {
    @Override
    public void onSelection() {
        setColor(Color.black);
    }

    @Override
    public void onClock() {
        if (getColor() == Color.black) {
            Node next = getNeighbors().get(PRNG.nextInt(getNeighbors().size()));
            next.setColor(Color.black);
            setColor(null);
        }
    }
}
