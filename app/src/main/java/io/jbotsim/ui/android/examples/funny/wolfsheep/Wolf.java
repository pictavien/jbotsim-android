package io.jbotsim.ui.android.examples.funny.wolfsheep;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

import java.util.Random;

/**
 * Created by acasteig on 31/08/16.
 */
public class Wolf extends Node {
    private static final Random PRNG = new Random();
    private static final int SPEED = 2;

    @Override
    public void onStart() {
        setIcon(Icon.getResourceURI(R.drawable.wolf));
        setIconSize(20);
        setSensingRange(50);
        setDirection(PRNG.nextDouble() * Math.PI * 2);
    }

    @Override
    public void onClock() {
        move(SPEED);
        wrapLocation();
    }

    @Override
    public void onPostClock() {
        if (PRNG.nextDouble() < 0.005){
            getTopology().removeNode(this);
        }
    }

    @Override
    public void onSensingIn(Node node) {
        if (node instanceof Sheep){
            ((Sheep) node).kill();
        }
    }
}
