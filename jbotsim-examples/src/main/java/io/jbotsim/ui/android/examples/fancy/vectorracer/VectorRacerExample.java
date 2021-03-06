package io.jbotsim.ui.android.examples.fancy.vectorracer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.android.ViewerActivityInitializer;

import io.jbotsim.core.Point;
import io.jbotsim.ui.painting.BackgroundPainter;
import io.jbotsim.ui.painting.UIComponent;

public class VectorRacerExample implements ClockListener, BackgroundPainter, ViewerActivityInitializer {
    Topology tp;
    Integer score = null;
    Point startPoint = new Point(400, 300);
    Integer binome = 4;
    Integer set = 1;

    @Override
    public boolean initialize(AndroidViewerActivity activity) {
        tp = activity.getTopology();
        startPoint = new Point(tp.getWidth() / 2, tp.getHeight() / 2);
        tp.addClockListener(this);
        activity.getViewer().addBackgroundPainter(this);

        tp.setTimeUnit(10);
        CherrySets.distribute(tp);
        tp.addNode(5 * tp.getWidth() / 8, 4 * tp.getHeight() / 6, new Drone());

        return true;
    }

    public boolean hasReturned(VectorNode drone) {
        return (drone.getLocation().equals(startPoint) &&
                drone.vector.distance(new Point(0, 0)) < VectorNode.DEVIATION);
    }

    @Override
    public void onClock() {
        if (tp.getNodes().size() == 1) {
            VectorNode drone = (VectorNode) tp.getNodes().get(0);
            if (hasReturned(drone)) {
                score = (int) Math.ceil(tp.getTime() / 10.0);
                String binome = drone.getClass().toString().substring(27);
                System.out.println("Score: " + score);
            }
        }
    }

    @Override
    public void paintBackground(UIComponent g2d, Topology topology) {
        Canvas canvas = (Canvas) g2d.getComponent();
        VectorNode drone = null;
        for (Node node : tp.getNodes())
            if (node instanceof VectorNode)
                drone = (VectorNode) node;
        if (drone == null)
            return;

        Paint pt = new Paint();
        double radius = 5;
        float l = (float) (drone.getX() + drone.vector.getX() - radius);
        float t = (float) (drone.getY() + drone.vector.getY() - radius);
        float r = (float) (drone.getX() + drone.vector.getX() + radius);
        float b = (float) (drone.getY() + drone.vector.getY() + radius);
        canvas.drawOval(new RectF(l, t, r, b), pt);

        if (score != null) {
            String s = "Score: " + score;
            float x = (float) (drone.getX() + 30);
            float y = (float) (drone.getY() + 30);
            canvas.drawText(s, x, y, pt);
        }
    }
}
