package io.jbotsim.ui.android.examples.fancy.canadairs;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

/**
 * Created by acasteig on 22/03/15.
 */
public class Lake extends Node {

    public Lake(){
        disableWireless();
        setIcon(Icon.getResourceURI(R.drawable.lake));
        setSize(45);
    }
}
