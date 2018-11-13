package io.jbotsim.ui.android.painting;

import android.graphics.Canvas;
import io.jbotsim.core.Node;

public interface NodePainter {
    void paintNode(Canvas canvas, Node node);
}
