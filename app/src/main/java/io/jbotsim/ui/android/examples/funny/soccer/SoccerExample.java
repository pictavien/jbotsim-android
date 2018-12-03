package io.jbotsim.ui.android.examples.funny.soccer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import io.jbotsim.ui.android.ViewerActivityInitializer;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.painting.BackgroundPainter;
import io.jbotsim.ui.painting.UIComponent;


public class SoccerExample implements  BackgroundPainter, ViewerActivityInitializer {
    @Override
    public boolean initialize(AndroidViewerActivity activity) {
        int unit = 6;
        Topology tp = activity.getTopology();
        tp.setDimensions(105 * unit+200, 68*unit+200);
        tp.setClockSpeed(50);
        tp.addNode(100, 100, new Ball());
        tp.setDefaultNodeModel(Robot.class);
        activity.getViewer().addBackgroundPainter(this);
        return true;
    }

    @Override
    public void paintBackground(UIComponent g2d, Topology topology) {
        Canvas canvas = (Canvas) g2d.getComponent ();
        Paint pt = new Paint();
        pt.setStyle(Paint.Style.FILL_AND_STROKE);
        pt.setColor(Color.rgb(0, 156, 0));
        float w = topology.getWidth();
        float h = topology.getHeight();
        canvas.drawRect(100f,100f, w - 100, h - 100, pt);

        pt.setStyle(Paint.Style.STROKE);
        pt.setColor(Color.WHITE);
        canvas.drawOval(new RectF(w / 2 - 60, h / 2 - 60, w / 2 + 60,
                h / 2 + 60), pt);
    }
}
