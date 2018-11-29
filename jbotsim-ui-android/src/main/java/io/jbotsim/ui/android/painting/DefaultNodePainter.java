package io.jbotsim.ui.android.painting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import io.jbotsim.core.Node;

public class DefaultNodePainter implements NodePainter {
    @Override
    public void paintNode(Canvas canvas, Node node) {

        // Different from SWING version
        // We did not paint into a button but in the canvas


        Bitmap bmp = (Bitmap) node.getProperty("icon-bitmap");
        if (bmp != null) {
            Paint np = new Paint();
            Matrix mat = new Matrix();
            float degrees = (float) (180.0 * node.getDirection() / Math.PI);
            float size = 2.0f * node.getSize();
            float sx = size / bmp.getWidth();
            float sy = size / bmp.getHeight();
            mat.postScale(sx, sy);
            mat.postRotate((float) degrees);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            float x = (float) (node.getX() - bmp.getWidth()/2);
            float y = (float) (node.getY() - bmp.getHeight()/2);
            canvas.drawBitmap(bmp, x, y, np);
        }

        if (node.getColor() != null) {
            Paint nP = new Paint();
            nP.setColor(node.getColor().getRGB());
            nP.setStyle(Paint.Style.FILL_AND_STROKE);
            double radius = node.getSize();
            float l = (float) (node.getX() - radius);
            float t = (float) (node.getY() - radius);
            float r = (float) (node.getX() + radius);
            float b = (float) (node.getY() + radius);

            canvas.drawOval(new RectF(l, t, r, b), nP);
        }
    }
}
