package io.jbotsim.ui.android.examples.fancy.vectorracer;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

/**
 * Created by acasteig on 18/11/16.
 */
public class Cherry extends Node {
    @Override
    public void onStart() {
        disableWireless();
        setIcon(Icon.getResourceURI(R.drawable.cherry));
    }
}
