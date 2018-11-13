package io.jbotsim.ui.android.examples.funny.cowboy;

import io.jbotsim.core.Node;

public class Cow extends Node{
    static Node farmer;
    int speed = 0;

    public Cow(){
        setIcon("cow");
        setSize(30);
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
