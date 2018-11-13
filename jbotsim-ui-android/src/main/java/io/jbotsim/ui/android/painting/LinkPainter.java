package io.jbotsim.ui.android.painting;

import android.graphics.Canvas;
import io.jbotsim.core.Link;

public interface LinkPainter {
    void paintLink(Canvas canvas, Link link);
}
