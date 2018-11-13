package io.jbotsim.ui.android.painting;

import android.graphics.Canvas;
import io.jbotsim.core.Topology;

public interface BackgroundPainter {
    void paintBackground(Canvas canvas, Topology topology);
}
