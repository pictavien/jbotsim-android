package io.jbotsim.ui.android.painting;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import io.jbotsim.core.Color;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.painting.BackgroundPainter;
import io.jbotsim.ui.painting.UIComponent;

public class DefaultBackgroundPainter implements BackgroundPainter {
    public static final Color DEFAULT_SENSING_RANGE_COLOR = Color.gray;

    @Override
    public void paintBackground(UIComponent g2d, Topology topology) {
        Canvas canvas = (Canvas) g2d.getComponent();
        Paint srPaint = new Paint();
        srPaint.setStyle(Paint.Style.STROKE);
        srPaint.setColor(DEFAULT_SENSING_RANGE_COLOR.getRGB());
        for (Node n : topology.getNodes()) {
            double sR = n.getSensingRange();
            if (sR > 0) {
                float l = (float) (n.getX() - sR);
                float t = (float) (n.getY() - sR);
                float r = (float) (n.getX() + sR);
                float b = (float) (n.getY() + sR);
                canvas.drawOval(new RectF(l, t, r, b), srPaint);
            }
        }
    }
}
