package io.jbotsim.ui.android.examples.funny.cowboy;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.android.ViewerActivityInitializer;
import io.jbotsim.ui.android.painting.BackgroundPainter;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void paintBackground(Canvas canvas, Topology topology) {
        Paint pt = new Paint();
        pt.setColor(Color.BLACK);
        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(1.0f);
        canvas.drawOval(50,50,200,200, pt);
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
        activity.getViewer().getView().addBackgroundPainter(this);

        return true;
    }
}
