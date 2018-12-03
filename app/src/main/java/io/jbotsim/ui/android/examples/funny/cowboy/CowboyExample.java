package io.jbotsim.ui.android.examples.funny.cowboy;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.android.ViewerActivityInitializer;
import io.jbotsim.ui.painting.BackgroundPainter;
import io.jbotsim.ui.painting.UIComponent;

/**
 * Created by acasteig on 17/06/15.
 */
public class CowboyExample implements ClockListener, BackgroundPainter, ViewerActivityInitializer {
    boolean finished = false;
    Topology tp;

    private boolean isFinished(){
        for (Node node : tp.getNodes())
            if (node instanceof Cow)
                if (node.distance(125,125) > 75)
                    return false;
        return true;
    }
    @Override
    public void onClock() {
        finished = isFinished();
    }

    @Override
    public void paintBackground(UIComponent g2d, Topology topology) {
        Canvas canvas = (Canvas) g2d.getComponent();
        Paint pt = new Paint();
        pt.setColor(Color.BLACK);
        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(1.0f);
        canvas.drawOval(new RectF(50f,50f,200f,200f), pt);
        if (finished) {
            canvas.drawText("YEAH !", 300, 150, pt);
        }
    }

    @Override
    public boolean initialize(AndroidViewerActivity activity) {
        tp = activity.getTopology();
        tp.addClockListener(this);
        tp.addNode(100, 100, Cow.farmer = new Farmer());
        tp.addNode(-1, -1, new Cow());
        tp.addNode(-1,-1, new Cow());
        tp.addNode(-1,-1, new Cow());
        activity.getViewer().addBackgroundPainter(this);

        return true;
    }
}
