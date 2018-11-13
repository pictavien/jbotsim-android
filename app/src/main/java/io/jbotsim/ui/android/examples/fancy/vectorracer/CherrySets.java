package io.jbotsim.ui.android.examples.fancy.vectorracer;

import io.jbotsim.core.PRNG;
import io.jbotsim.core.Topology;

/**
 * Created by acasteig on 26/01/17.
 */
public class CherrySets {
    public static void distribute(Topology tp){
        for (int i=0; i<20; i++) {
            double x = PRNG.nextDouble() * 600 + 100;
            double y = PRNG.nextDouble() * 400 + 100;
            tp.addNode(x, y, new Cherry());
        }
    }
}
