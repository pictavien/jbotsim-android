package io.jbotsim.ui.android.examples.fancy.vectorracer;

import io.jbotsim.core.Topology;

import java.util.Random;

/**
 * Created by acasteig on 26/01/17.
 */
public class CherrySets {
    private static final Random PRNG = new Random();
    public static void distribute(Topology tp){
        for (int i=0; i<20; i++) {
            double x = PRNG.nextDouble() * tp.getWidth();
            double y = PRNG.nextDouble() * tp.getHeight();
            tp.addNode(x, y, new Cherry());
        }
    }
}
