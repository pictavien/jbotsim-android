package io.jbotsim.ui.android.examples.fancy.vectorracer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ClockListener;
import io.jbotsim.ui.android.AndroidViewerActivity;
import io.jbotsim.ui.android.ViewerActivityInitializer;

import io.jbotsim.core.Point;
import io.jbotsim.ui.android.painting.BackgroundPainter;

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
        activity.getViewer().getView().addBackgroundPainter(this);

        tp.setClockSpeed(10);
        CherrySets.distribute(tp);
        tp.addNode(5 * tp.getWidth() / 8, 4 * tp.getHeight() / 6, new Drone());
        tp.start();

        return true;
    }

    public boolean hasReturned(VectorNode drone) {
        if (drone.getLocation().equals(startPoint) && drone.vector.distance(new Point(0, 0)) < VectorNode.DEVIATION) {
            return true;
        } else
            return false;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void paintBackground(Canvas canvas, Topology topology) {
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
        canvas.drawOval(l, t, r, b, pt);

        if (score != null) {
            String s = "Score: " + Integer.toString(score);
            float x = (float) (drone.getX() + 30);
            float y = (float) (drone.getY() + 30);
            canvas.drawText(s, x, y, pt);
        }
    }
}
