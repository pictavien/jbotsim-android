package io.jbotsim.ui.android.examples.funny.cowboy;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.AndroidFileAccessor;
import io.jbotsim.ui.android.examples.R;

public class Cow extends Node {
    static Node farmer;
    int speed = 0;

    public Cow(){
        setIcon(AndroidFileAccessor.getResourceURI(R.drawable.cow));
        setIconSize(30);
        disableWireless();
    }

    @Override
    public void onClock() {
        if (speed > 0)
            move(speed--);
        else
            if (speed == 0 && this.distance(farmer)<20) {
                setDirection(farmer.getLocation(),true);
                speed = 12;
            }
        wrapLocation();
    }
}
