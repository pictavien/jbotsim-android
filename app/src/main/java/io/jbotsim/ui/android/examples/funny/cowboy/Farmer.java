package io.jbotsim.ui.android.examples.funny.cowboy;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

/**
 * Created by acasteig on 17/06/15.
 */
public class Farmer extends Node {
    public Farmer() {
        setIcon(Icon.getResourceURI(R.drawable.farmer));
        setSize(30);
        disableWireless();
    }
}
