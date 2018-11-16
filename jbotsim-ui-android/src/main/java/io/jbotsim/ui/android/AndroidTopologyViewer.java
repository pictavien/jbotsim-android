package io.jbotsim.ui.android;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.*;

import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core._Properties;
import io.jbotsim.core.event.*;
import io.jbotsim.ui.android.painting.BackgroundPainter;
import io.jbotsim.ui.android.painting.DefaultBackgroundPainter;
import io.jbotsim.ui.android.painting.DefaultLinkPainter;
import io.jbotsim.ui.android.painting.DefaultNodePainter;
import io.jbotsim.ui.android.painting.LinkPainter;
import io.jbotsim.ui.android.painting.NodePainter;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AndroidTopologyViewer
        extends View
        implements TopologyListener, MovementListener,
                   ConnectivityListener, PropertyListener,
                   ClockListener {
    // TODO this should depend on screen size and or zoom (scale of matrix)
    public final static float USER_MISS_RADIUS = 10;
    /**
     * true if the labels should be drawn or not
     */
    public static boolean DO_SHOW_LABELS = true;
    /**
     * true if the labels should be drawn or not
     */
    public static boolean EDGE_DRAW_MODE = true;
    public static final int BACKGROUND_COLOR = Color.LTGRAY;

    private static Bitmap defaultNodeIcon = null;
    private String statusInfo = "";
    private Set<Node> userSelectedVertices = new HashSet<>();
    private Set<Link> markedEdges = new HashSet<>();
    private Node deleteVertex = null;
    protected ArrayList<BackgroundPainter> backgroundPainters = new ArrayList<>();
    protected LinkPainter linkPainter;
    protected ArrayList<NodePainter> nodePainters = new ArrayList<>();
    protected boolean showDrawings = true;
    private Topology tp;
    private Matrix transformMatrix = null;

    private DeleteIcon deleteIcon = null;

    private EventHandler evh;

    private Integer initialWidth = null;
    private Integer initialHeight = null;


    public AndroidTopologyViewer(Context context) {
        this(context, null);
    }

    public AndroidTopologyViewer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        tp = null;
        setFocusable(true);
        evh = new EventHandler();
        setOnClickListener(evh);
        setOnTouchListener(evh);
        setDefaultBackgroundPainter(new DefaultBackgroundPainter());
        setDefaultNodePainter(new DefaultNodePainter());
        setLinkPainter(new DefaultLinkPainter());
        defaultNodeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.nodeicon);
    }

    public void setTopology(Topology topology) {
        unsetTopology();
        if ((tp = topology) != null) {
            tp.addConnectivityListener(this);
            tp.addTopologyListener(this);
            tp.addMovementListener(this);
            tp.addPropertyListener(this);
            tp.addClockListener(this);
        }
    }

    private void unsetTopology() {
        if (tp == null)
            return;
        tp.removeClockListener(this);
        tp.removePropertyListener(this);
        tp.removeMovementListener(this);
        tp.removeTopologyListener(this);
        tp.removeConnectivityListener(this);
        initialHeight = null;
        initialWidth = null;
        tp = null;
    }

    public boolean toggleEdgeDraw() {
        EDGE_DRAW_MODE = !EDGE_DRAW_MODE;
        String message = EDGE_DRAW_MODE ? "Draw edges." : "Tap to create vertices.";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        clearAll();
        redraw();

        return EDGE_DRAW_MODE;

    }

    public Topology getTopology() {
        return tp;
    }

    public void setEdgeDrawMode(boolean mode) {
        if (EDGE_DRAW_MODE != mode)
            toggleEdgeDraw();
    }

    /**
     * Returns closest vertex to coordinate within the range of radius. Radius can
     * be POSITIVE_INFINITY, in which case we accept any radius.
     * <p>
     * We return null if no such vertex exists.
     * <p>
     * If two vertices have exactly the same distance, one is chosen arbitrarily.
     *
     * @param coordinate
     * @param radius
     * @return the vertex closest to coordinate constrained to radius, or null
     */
    public Node getClosestVertex(Point coordinate, double radius) {
        List<Node> vertices = getTopology().getNodes();
        if (vertices.isEmpty())
            return null;

        double bestDistance = radius;
        Node bestVertex = null;

        for (Node currentVertex : vertices) {
            io.jbotsim.core.Point pos = currentVertex.getLocation();
            double currentDistance = pos.distance(coordinate.getX(), coordinate.getY());
            if (currentDistance < bestDistance) {
                bestVertex = currentVertex;
                bestDistance = currentDistance;
            }
        }
        return bestVertex;
    }

    /**
     * Deselects all selected vertices and edges
     */
    public void clearAll() {
        markedEdges.clear();
        userSelectedVertices.clear();
        redraw();
    }

    public void selectAll() {
        clearAll();
        userSelectedVertices.addAll(getTopology().getNodes());
        redraw();
    }

    public void deselectAll() {
        userSelectedVertices.clear();
        redraw();
    }

    /**
     * Selects all vertices that are not selected and deselects those who are.
     */
    public void invertSelectedVertices() {
        List<Node> nodes = getTopology().getNodes();
        Set<Node> select = new HashSet<>(nodes.size());
        for (Node v : nodes) {
            if (!userSelectedVertices.contains(v)) {
                select.add(v);
            }
        }
        clearAll();
        userSelectedVertices = select;
        redraw();
    }

    /**
     * Deletes the selected vertices.
     *
     * @return Returns the number of vertices deleted.
     */
    public int deleteSelectedVertices() {
        int nbDeleted = 0;

        for (Node v : userSelectedVertices) {
            getTopology().removeNode(v);
            nbDeleted++;
        }
        clearAll();
        redraw();
        return nbDeleted;
    }

    /**
     * Toggles edges between given vertex with memory. If redraw is set, will
     * redraw after operation.
     *
     * @param v vertex v
     * @param u vertex u
     * @return returns the edge if it is added, null if it is removed
     */
    private Link toggleEdge(Node v, Node u, boolean redraw) {
        Link edge = v.getOutLinkTo(u);

        if (edge != null) {
            getTopology().removeLink(edge);
            edge = null;
        } else {
            edge = new Link(v, u);
            getTopology().addLink(edge);
        }

        if (redraw)
            redraw();
        return edge;
    }

    /**
     * Toggles edges between given vertex. Redraws as well! And adds to
     *
     * @param v vertex v
     * @param u vertex u
     * @return returns the edge if it is added, null if it is removed
     */
    private Link toggleEdge(Node v, Node u) {
        return toggleEdge(v, u, true);
    }

    public void centralize() {
        // TODO
    }

    public void addBackgroundPainter(BackgroundPainter painter) {
        backgroundPainters.add(0, painter);
    }

    public void setDefaultBackgroundPainter(BackgroundPainter painter) {
        backgroundPainters.clear();
        addBackgroundPainter(painter);
    }

    public void removeBackgroundPainter(BackgroundPainter painter) {
        backgroundPainters.remove(painter);
    }

    public void setLinkPainter(LinkPainter painter) {
        linkPainter = painter;
    }

    public void addNodePainter(NodePainter painter) {
        nodePainters.add(painter);
    }

    public void setDefaultNodePainter(NodePainter painter) {
        nodePainters.clear();
        addNodePainter(painter);
    }

    public void removeNodePainter(NodePainter painter) {
        nodePainters.remove(painter);
    }

    /**
     * Disables the drawing of links and sensing radius (if any).
     */
    public void disableDrawings() {
        showDrawings = false;
    }


    public Matrix getTransformMatrix() {
        return transformMatrix;
    }

    public void redraw() {
        updateStatus();
        invalidate();
    }

    /**
     * Returns the coordinate the given point/coordinate on the screen represents
     * in the graph
     */
    public Point translateCoordinate(Point screenCoordinate) {
        float[] screenPoint = {(float) screenCoordinate.getX(), (float) screenCoordinate.getY()};
        Matrix invertedTransformMatrix = new Matrix();

        getTransformMatrix().invert(invertedTransformMatrix);
        invertedTransformMatrix.mapPoints(screenPoint);

        return new Point(screenPoint[0], screenPoint[1]);
    }


    void saveInitialTopologyDimensions() {
        if (initialWidth == null || initialHeight == null) {
            initialWidth = tp.getWidth();
            initialHeight = tp.getHeight();
        }
    }

    public void resetTopologySize() {
        saveInitialTopologyDimensions();
        resizeTopology(initialWidth, initialHeight);
    }

    public void resizeTopologyToScreen() {
        saveInitialTopologyDimensions();
        resizeTopology(getWidth(), getHeight());
    }

    private void resizeTopology(int width, int height) {
        tp.setDimensions(width, height);
        RectF src = new RectF(0.0f, 0.0f, width, height);
        RectF dst = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        transformMatrix = new Matrix();
        transformMatrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
        redraw();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (transformMatrix == null) {
            resetTopologySize();
        }

        Paint paint = new Paint();
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRect(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);

        if (deleteIcon == null) {
            deleteIcon = new DeleteIcon(canvas.getWidth() / 2f, canvas.getHeight() * 0.90f);
        }

        canvas.save();
        canvas.concat(transformMatrix);

        paint.setColor(android.graphics.Color.WHITE);
        canvas.drawRect(0.0f, 0.0f, tp.getWidth(), tp.getHeight(), paint);

        assert (tp != null);
        setBackgroundColor(android.graphics.Color.WHITE);

        for (BackgroundPainter bp : backgroundPainters) {
            bp.paintBackground(canvas, tp);
        }

        for (Node node : tp.getNodes()) {
            for (NodePainter np : nodePainters) {
                np.paintNode(canvas, node);
            }
        }

        for (Link link : tp.getLinks()) {
            linkPainter.paintLink(canvas, link);
        }
        canvas.restore();

        deleteIcon.draw(canvas);
        writeStatus(canvas);
    }

    public void updateStatus() {
        statusInfo = "# node : " + tp.getNodes().size() + "# nb links: " + tp.getLinks().size() +
                "# time: " + tp.getTime();
    }

    private void writeStatus(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(android.graphics.Color.BLACK);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        canvas.drawText(statusInfo,0, -fontMetrics.top, textPaint);
    }

    public void onNodeAdded(Node n) {
        n.addPropertyListener(this);
        if (n.getIcon() == null)
            n.setProperty("icon-bitmap", defaultNodeIcon);
        else
            n.setProperty("icon", n.getIcon());
        redraw();
    }

    public void onNodeRemoved(Node n) {
        redraw();
    }

    public void onLinkAdded(Link l) {
        l.addPropertyListener(this);
        redraw();
    }

    public void onLinkRemoved(Link l) {
        redraw();
    }

    public void onMove(Node n) {
        redraw();
    }

    public void propertyChanged(_Properties o, String property) {
        if (o instanceof Node) {
            Node n = (Node) o;
            redraw();
				/*if (property.equals("color")) {
					jn.updateUI();
				} else  else if (property.equals("size")) {
					jn.updateIcon();
				} */
            if (property.equals("icon")) {
                Resources rsrc = getResources();
                int id = rsrc.getIdentifier(n.getIcon(), "drawable", getContext().getPackageName());
                Bitmap icon = BitmapFactory.decodeResource(rsrc, id);
                if (Node.DEFAULT_DIRECTION != 0.0) {
                    Matrix mat = new Matrix();
                    double degrees = -180.0 * Node.DEFAULT_DIRECTION / Math.PI;
                    mat.setRotate((float) degrees);
                    icon = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), mat, true);
                }
                n.setProperty("icon-bitmap", icon);
                redraw();
            }
        } else if (property.equals("width") || property.equals("color")) {
            redraw();
        }
    }

    @Override
    public void onClock() {
    }


    private class EventHandler
            extends SimpleOnGestureListener
            implements View.OnTouchListener,
                       View.OnClickListener {
        GestureDetector gd;
        /**
         * This vertex was touch, e.g. for scrolling and moving purposes
         */
        private Node touchedVertex = null;
        /**
         * This is set to the coordinate of the vertex we started move
         */
        private Point startCoordinate = null;
        private int previousPointerCount = 0;
        private Point[] previousPointerCoords = null;
        private boolean popuprunning = false;

        public EventHandler() {
            gd = new GestureDetector(getContext(), this);
        }

        public boolean onDown(MotionEvent e) {
            deleteIcon.setVisible(false);
            Point sCoordinate = new Point(e.getX(), e.getY());
            Point gCoordinate = translateCoordinate(sCoordinate);
            previousPointerCount = -1; // make any ongoing scroll restart

            if (e.getPointerCount() == 1) {
                touchedVertex = getClosestVertex(gCoordinate, USER_MISS_RADIUS);
            } else {
                touchedVertex = null;
            }
            return super.onDown(e);
        }

        public Point clearCoordinate() {
            Point ret = startCoordinate;
            startCoordinate = null;
            return ret;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            deleteIcon.setVisible(false);
            Point sCoordinate = new Point(e2.getX(), e2.getY());
            Point gCoordinate = translateCoordinate(sCoordinate);
            Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);
            float dist = (float) Math.round(Math.sqrt((velocityX * velocityX) + (velocityY *
                    velocityY)));

            if (dist < 4000)
                return true;

            if (hit != null) {
                clearAll();
                getTopology().removeNode(hit);
                touchedVertex = null;
                redraw();
                return true;
            }

            return true;
        }


        private void popupNodeClasses(final double x, final double y) {
            if (popuprunning)
                return;

            final Dialog popupModels = new Dialog(getContext());
            popupModels.setContentView(R.layout.nodemodels_popup);
            popupModels.setTitle("Choose a model...");
            popupModels.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    popuprunning = false;
                }
            });
            ListView lv = (ListView) popupModels.findViewById(R.id.nodemodels_list);
            final List<String> models = new ArrayList<>(getTopology().getModelsNames());
            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout
                    .nodemodels_entry, models);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String modelName = models.get(position);
                    getTopology().addNode(x, y, getTopology().newInstanceOfModel(modelName));
                    popupModels.dismiss();
                }
            });
            popupModels.show();
            popuprunning = true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            deleteIcon.setVisible(false);
            if (EDGE_DRAW_MODE) {

                Point sCoordinate = new Point(e.getX(), e.getY());
                Point gCoordinate = translateCoordinate(sCoordinate);
                Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);

                if (hit == null) {
                    clearAll();
                    touchedVertex = null;
                    redraw();
                    return true;
                }
            } else {
                Point sCoordinate = new Point(e.getX(), e.getY());
                Point gCoordinate = translateCoordinate(sCoordinate);
                Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);
                Topology tp = getTopology();

                if (hit == null) {
                    Node node = null;
                    String modelName = "default";
                    if (tp.getModelsNames().size() > 1) {
                        popupNodeClasses(gCoordinate.getX(), gCoordinate.getY());
                        return true;
                    } else if (tp.getModelsNames().size() > 0) {
                        modelName = tp.getModelsNames().iterator().next();
                    }
                    tp.addNode(gCoordinate.getX(), gCoordinate.getY(), tp.newInstanceOfModel
                            (modelName));
                } else {
                    if (userSelectedVertices.contains(hit)) {
                        userSelectedVertices.remove(hit);
                    } else {
                        userSelectedVertices.add(hit);
                    }
                    tp.selectNode(hit);
                }
            }

            touchedVertex = null;

            redraw();
            return true;
        }

        public void onLongPress(MotionEvent e) {
            deleteIcon.setVisible(false);
            toggleEdgeDraw();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (e2.getPointerCount()) {
                case 2:
                    deleteIcon.setVisible(false);
                    if (previousPointerCoords == null || previousPointerCount != 2) {
                        previousPointerCoords = new Point[2];
                        previousPointerCoords[0] = new Point(e2.getX(0), e2.getY(0));
                        previousPointerCoords[1] = new Point(e2.getX(1), e2.getY(1));
                    } else {
                        Point[] newCoords = {
                                new Point(e2.getX(0), e2.getY(0)),
                                new Point(e2.getX(1), e2.getY(1))
                        };
                        Point VectorPrevious = previousPointerCoords[1].subtract
                                (previousPointerCoords[0]);
                        Point VectorNew = newCoords[1].subtract(newCoords[0]);
                        double diffAngle = VectorNew.angle() - VectorPrevious.angle();
                        double scale = VectorNew.length() / VectorPrevious.length();

                        // the transformations
                        getTransformMatrix().postTranslate((float) -previousPointerCoords[0].getX
                                (), (float) -previousPointerCoords[0].getY());
                        getTransformMatrix().postRotate((float) diffAngle);
                        getTransformMatrix().postScale((float) scale, (float) scale);
                        getTransformMatrix().postTranslate((float) newCoords[0].getX(), (float)
                                newCoords[0].getY());

                        previousPointerCoords = newCoords;
                    }
                    break;
                case 1:
                    if (EDGE_DRAW_MODE) {
                        Point sCoordinate = new Point(e2.getX(), e2.getY());
                        Point gCoordinate = translateCoordinate(sCoordinate);
                        Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);

                        if (hit != null) {
                            // System.out.println("HIT " + hit.getId());
                            if (touchedVertex != null && touchedVertex != hit) {
                                Link edge = toggleEdge(hit, touchedVertex);
                                userSelectedVertices.remove(touchedVertex);
                                userSelectedVertices.add(hit);
                                markedEdges.clear();
                                if (edge != null)
                                    markedEdges.add(edge);
                            }
                            touchedVertex = hit;
                        }

                    } else {

                        previousPointerCoords = null;
                        if (touchedVertex != null) {

                            if (startCoordinate == null) {
                                startCoordinate = new Point(touchedVertex.getX(), touchedVertex
                                        .getY());
                            }

                            deleteIcon.setVisible(true);

                            Point sCoordinate = new Point(e2.getX(), e2.getY());

                            if (deleteIcon.checkIsOver(sCoordinate)) {
                                deleteVertex = touchedVertex;
                            } else {
                                deleteVertex = null;

                            }

                            Point gCoordinate = translateCoordinate(sCoordinate);
                            touchedVertex.setLocation(gCoordinate.getX(), gCoordinate.getY());

                        } else {
                            deleteIcon.setVisible(false);
//                            if (previousPointerCount == 1)
//                                getTransformMatrix().postTranslate(-distanceX, -distanceY);
                        }

                    }
                    break;
                default: // 3 or more
                    deleteIcon.setVisible(false);
                    previousPointerCoords = null;
                    previousPointerCount = e2.getPointerCount();
                    return false;
            }
            previousPointerCount = e2.getPointerCount();
            redraw();
            return true;
        }

        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (deleteIcon.isOver()) {
                    if (deleteVertex != null) {
                        getTopology().removeNode(deleteVertex);
                        Point c = clearCoordinate();
                        if (c != null)
                            deleteVertex.setLocation(c.getX(), c.getY());
                        deleteVertex = null;
                    }
                }
                clearCoordinate();
                deleteIcon.setVisible(false);
            }
            return gd.onTouchEvent(event);
        }

        @Override
        public void onClick(View v) {
        }

    }

    private class DeleteIcon {
        private final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.trash64x64);
        private RectF loc = new RectF();
        private boolean isOver = false;
        private boolean visible = false;
        private final Paint bgPaint;

        DeleteIcon(float x, float y) {
            bgPaint = new Paint();
            bgPaint.setColor(Color.RED);
            bgPaint.setAlpha(0x55);
            setLocation(x, y);
        }

        public boolean isOver() {
            return isOver;
        }

        void setLocation(float x, float y) {
            float hw = bitmap.getWidth() / 2.0f;
            float hh = bitmap.getHeight() / 2.0f;
            loc.left = x - hw;
            loc.top = y - hh;
            loc.right = x + hw;
            loc.bottom = y + hh;
        }

        boolean checkIsOver(Point pt) {
            boolean val = loc.contains((float) pt.getX(), (float) pt.getY());
            if (val != isOver) {
                redraw();
                isOver = val;
            }
            return val;
        }

        void setVisible(boolean visible) {
            this.visible = visible;
            redraw();
        }

        void draw(Canvas canvas) {
            if (!visible)
                return;
            if (isOver) {
                canvas.drawBitmap(bitmap, loc.left, loc.top, null);
                float hw = bitmap.getWidth() / 4.0f;
                float hh = bitmap.getHeight() / 4.0f;

                canvas.drawOval(new RectF(loc.left - hw, loc.top - hh, loc.right + hw,
                        loc.bottom + hh), bgPaint);
                //canvas.drawBitmap(trashBgRedBitmap, trashX - 46, trashY - 46, trashPaint);
            } else {
                canvas.drawBitmap(bitmap, loc.left, loc.top, null);
            }
        }
    }
}