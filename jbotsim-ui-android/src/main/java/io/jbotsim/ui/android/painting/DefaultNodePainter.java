package io.jbotsim.ui.android.painting;

import android.graphics.*;

import io.jbotsim.core.Node;
import io.jbotsim.io.FileManager;
import io.jbotsim.ui.icons.Icons;
import io.jbotsim.ui.painting.NodePainter;
import io.jbotsim.ui.painting.UIComponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DefaultNodePainter implements NodePainter {
    public static final double CIRCLE_RATIO = 0.5;
    public static final String NODE_ICON_BITMAP_PROPERTY = "icon-bitmap";
    public static final String DEFAULT_NODE_ICON = Icons.DEFAULT_NODE_ICON;
    private static final HashMap<String, Bitmap> bmpCache = new HashMap<>();
    private static Bitmap defaultIcon = null;

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
            float size = 2.0f * node.getIconSize();
            float sx = size / bmp.getWidth();
            float sy = size / bmp.getHeight();
            mat.postScale(sx, sy);
            mat.postRotate(degrees);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
            float x = (float) (node.getX() - bmp.getWidth() / 2);
            float y = (float) (node.getY() - bmp.getHeight() / 2);
            canvas.drawBitmap(bmp, x, y, np);
        }

        if (node.getColor() != null) {
            Paint nP = new Paint();
            nP.setColor(node.getColor().getRGB());
            nP.setStyle(Paint.Style.FILL_AND_STROKE);
            double radius = node.getIconSize() * CIRCLE_RATIO;
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

        try {
            FileManager fm = node.getTopology().getFileManager();
            String iconLoc = node.getIcon();
            if (iconLoc == null)
                return getDefaultIcon(fm);

            if (bmpCache.containsKey(iconLoc)) {
                result = bmpCache.get(iconLoc);
            } else {
                InputStream inputStream = fm.getInputStreamForName(iconLoc);
                result = BitmapFactory.decodeStream(inputStream, null, null);
                bmpCache.put(iconLoc, result);
            }
            node.setProperty(DefaultNodePainter.NODE_ICON_BITMAP_PROPERTY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private synchronized Bitmap getDefaultIcon(FileManager fm) {
        if (defaultIcon == null) {
            try {
                InputStream inputStream = fm.getInputStreamForName(DEFAULT_NODE_ICON);
                defaultIcon = BitmapFactory.decodeStream(inputStream, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defaultIcon;
    }
}
