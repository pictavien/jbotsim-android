package io.jbotsim.ui.android.painting;

import android.graphics.Canvas;
import android.graphics.Paint;

import io.jbotsim.ui.painting.LinkPainter;
import io.jbotsim.core.Link;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.painting.UIComponent;

public class DefaultLinkPainter implements LinkPainter {
    @Override
    public void paintLink(UIComponent g2d, Link link) {
        Canvas canvas = (Canvas) g2d.getComponent();
        // TODO
        Integer width = link.getWidth();
        if (width == 0)
            return;
        Paint edgePaint = new Paint();
        edgePaint.setColor(link.getColor().getRGB());
        edgePaint.setStrokeWidth(width);

        float srcX = (float) link.source.getX();
        float srcY = (float) link.source.getY();
        float dstX = (float) link.destination.getX();
        float dstY = (float) link.destination.getY();
        canvas.drawLine(srcX, srcY, dstX, dstY, edgePaint);
        Topology topology = link.source.getTopology();
        if (topology != null && topology.hasDirectedLinks()) {
            float x = (srcX + 4 * dstX) / 5;
            float y = (srcY + 4 * dstY) / 5;
            canvas.drawCircle(x, y, 2, edgePaint);
        }
    }
}
