package io.jbotsim.ui.android.painting;

import android.graphics.*;

import io.jbotsim.core.Node;
import io.jbotsim.ui.painting.NodePainter;
import io.jbotsim.ui.painting.UIComponent;

import java.io.IOException;
import java.io.InputStream;

public class DefaultNodePainter implements NodePainter {
    public static final String NODE_ICON_BITMAP_PROPERTY = "icon-bitmap";
    public static final String DEFAULT_NODE_ICON = "/io/jbotsim/ui/circle.png";

    @Override
    public void paintNode(UIComponent g2d, Node node) {

        // Different from SWING version
        // We did not paint into a button but in the canvas

        Canvas canvas = (Canvas) g2d.getComponent();
        Bitmap bmp = getOrCreateIcon(node);
        if (bmp != null) {
            Paint np = new Paint();
            Matrix mat = new Matrix();
            float degrees = (float) (180.0 * (node.getDirection() - Node.DEFAULT_DIRECTION) / Math.PI);
            float size = 2.0f * node.getSize();
            float sx = size / bmp.getWidth();
            float sy = size / bmp.getHeight();
            mat.postScale(sx, sy);
            mat.postRotate(degrees);
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

    private Bitmap getOrCreateIcon(Node node) {
        Bitmap result = (Bitmap) node.getProperty(NODE_ICON_BITMAP_PROPERTY);
        if (result != null)
            return result;

        String iconLoc = node.getIcon();
        if (iconLoc == null)
            iconLoc = DEFAULT_NODE_ICON;
        try {
            InputStream inputStream = node.getTopology().getFileManager().getInputStreamForName(iconLoc);
            result = BitmapFactory.decodeStream(inputStream, null, null);
            node.setProperty(DefaultNodePainter.NODE_ICON_BITMAP_PROPERTY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
