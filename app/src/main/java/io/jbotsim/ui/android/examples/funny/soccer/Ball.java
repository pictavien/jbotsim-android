package io.jbotsim.ui.android.examples.funny.soccer;

import io.jbotsim.core.Node;

import io.jbotsim.core.Point;
import io.jbotsim.ui.android.AndroidFileAccessor;
import io.jbotsim.ui.android.examples.R;

import java.util.Random;

/**
 * Created by Arnaud Casteigts on 06/04/17.
 */
public class Ball extends Node {
    private static final Random PRNG = new Random();

    double speed = 0;
    static final double fadingRatio = 1.2;

    @Override
    public void onStart() {
        setIcon(AndroidFileAccessor.getResourceURI(R.drawable.ball));
        disableWireless();
    }

    @Override
    public void onClock() {
        if (speed > 0) {
            move(speed);
            speed = speed / fadingRatio;
            wrapLocation();
            if (speed < 1) {
                speed = 0;
            }
        }
    }

    public void randomShoot(){
        double x = PRNG.nextDouble()*getTopology().getWidth();
        double y = PRNG.nextDouble()*getTopology().getHeight();
        Point p = new Point(x, y);
        double speed = PRNG.nextDouble()*40 + 10;
        shoot(p, speed);
    }

    public void shoot(double angle, double speed){
        setDirection(angle);
        this.speed = speed;
    }

    public void shoot(Point direction, double speed){
        setDirection(direction);
        this.speed = speed;
    }
}
