package io.jbotsim.ui.android;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.*;
import io.jbotsim.core.*;
import io.jbotsim.core.event.*;
import io.jbotsim.ui.android.painting.DefaultBackgroundPainter;
import io.jbotsim.ui.android.painting.DefaultLinkPainter;
import io.jbotsim.ui.android.painting.DefaultNodePainter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AndroidTopologyViewer {
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
    public static int TRASH_CAN = 0;
    private static Bitmap defaultNodeIcon = null;
    private final AndroidViewerActivity activity;
    private String info = "";
    private AndroidTopology view;
    private Set<Node> userSelectedVertices = new HashSet<>();
    private Set<Link> markedEdges = new HashSet<>();
    private Node deleteVertex = null;

    public AndroidTopologyViewer(AndroidViewerActivity activity, Topology topology) {
        this.activity = activity;

        view = new AndroidTopology(activity, topology);
        EventHandler evh = new EventHandler();
        view.setOnClickListener(evh);
        view.setOnTouchListener(evh);
        view.setDefaultBackgroundPainter(new DefaultBackgroundPainter());
        view.setDefaultNodePainter(new DefaultNodePainter());
        view.setLinkPainter(new DefaultLinkPainter());

        defaultNodeIcon = BitmapFactory.decodeResource(view.getResources(), R.drawable.nodeicon);
        Topology tp = view.getTopology();
        tp.addConnectivityListener(evh);
        tp.addTopologyListener(evh);
        tp.addMovementListener(evh);
        tp.addPropertyListener(evh);
        tp.addClockListener(evh);

        redraw();
    }

    public boolean toggleEdgeDraw() {
        EDGE_DRAW_MODE = !EDGE_DRAW_MODE;

        activity.shortToast(EDGE_DRAW_MODE ? "Draw edges." : "Tap to create vertices.");

        clearAll();
        redraw();

        return EDGE_DRAW_MODE;

    }

    public Topology getTopology() {
        return view.getTopology();
    }

    public void clearMemory() {
        // TODO something
    }

    public void setEdgeDrawMode(boolean mode) {
        if (EDGE_DRAW_MODE != mode)
            toggleEdgeDraw();
    }

    public void trashCan(int mode) {
        TRASH_CAN = mode;
        redraw();
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
    public Node getClosestVertex(Coordinate coordinate, double radius) {
        List<Node> vertices = getTopology().getNodes();
        if (vertices.isEmpty())
            return null;

        double bestDistance = radius;
        Node bestVertex = null;

        for (Node currentVertex : vertices) {
            Point pos = currentVertex.getLocation();
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

    public AndroidTopology getView() {
        return view;
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

    public String makeInfo() {
        return info;
    }

    public String graphInfo() {
        return info;
    }

    public void redraw() {
		/*
		if (graph == null) {
			return;
		}

		for (DefaultVertex v : graph.vertexSet()) {
			v.setLabel(""); // todo fix
		}
		for (DefaultEdge<DefaultVertex> e : graph.edgeSet()) {
			e.setStyle(EdgeStyle.SOLID); // todo fix
		}

		for (DefaultVertex v : graph.vertexSet()) {
			v.setColor(EDGE_DRAW_MODE ? DEFAULT_VERTEX_COLOR : TOUCHED_VERTEX_COLOR);
		}

		for (DefaultVertex v : graph.vertexSet()) {
			v.setColor(EDGE_DRAW_MODE ? DEFAULT_VERTEX_COLOR : TOUCHED_VERTEX_COLOR);
			if (userSelectedVertices.contains(v)) {
				v.setColor(USERSELECTED_VERTEX_COLOR);
			}
		}

		for (DefaultEdge<DefaultVertex> e : graph.edgeSet()) {
			e.setColor(DEFAULT_EDGE_COLOR);
			if (markedEdges.contains(e)) {
				e.setStyle(EdgeStyle.BOLD);
			}
		}
*/
        view.redraw(graphInfo());
    }

    private class EventHandler extends SimpleOnGestureListener implements View.OnTouchListener, View.OnClickListener,
            TopologyListener, MovementListener, ConnectivityListener,
            PropertyListener, ClockListener {
        GestureDetector gd;
        /**
         * This vertex was touch, e.g. for scrolling and moving purposes
         */
        private Node touchedVertex = null;
        /**
         * This is set to the coordinate of the vertex we started move
         */
        private Coordinate startCoordinate = null;
        private int previousPointerCount = 0;
        private Coordinate[] previousPointerCoords = null;
        private boolean popuprunning = false;

        public EventHandler() {
            gd = new GestureDetector(this); // TODO deprecated!
        }

        public boolean onDown(MotionEvent e) {
            trashCan(0);
            Coordinate sCoordinate = new Coordinate(e.getX(), e.getY());
            Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
            previousPointerCount = -1; // make any ongoing scroll restart

            if (e.getPointerCount() == 1) {
                touchedVertex = getClosestVertex(gCoordinate, USER_MISS_RADIUS);
            } else {
                touchedVertex = null;
            }
            return super.onDown(e);
        }

        public Coordinate clearCoordinate() {
            Coordinate ret = startCoordinate;
            startCoordinate = null;
            return ret;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            trashCan(0);
            Coordinate sCoordinate = new Coordinate(e2.getX(), e2.getY());
            Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
            Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);
            float dist = (float) Math.round(Math.sqrt((velocityX * velocityX) + (velocityY * velocityY)));

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


        private void popupNodeClasses (final double x, final double y) {
            if (popuprunning)
                return;

            final Dialog popupModels = new Dialog(activity);
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
            ArrayAdapter<String> adapter = new ArrayAdapter(activity, R.layout.nodemodels_entry, models);
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
            trashCan(0);
            if (EDGE_DRAW_MODE) {

                Coordinate sCoordinate = new Coordinate(e.getX(), e.getY());
                Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
                Node hit = getClosestVertex(gCoordinate, USER_MISS_RADIUS);

                if (hit == null) {
                    clearAll();
                    touchedVertex = null;
                    redraw();
                    return true;
                }
            } else {
                Coordinate sCoordinate = new Coordinate(e.getX(), e.getY());
                Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
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
                    tp.addNode(gCoordinate.getX(), gCoordinate.getY(), tp.newInstanceOfModel(modelName));
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
            trashCan(0);
            //vibrator.vibrate(50);
            toggleEdgeDraw();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (e2.getPointerCount()) {
                case 2:
                    trashCan(0);
                    if (previousPointerCoords == null || previousPointerCount != 2) {
                        previousPointerCoords = new Coordinate[2];
                        previousPointerCoords[0] = new Coordinate(e2.getX(0), e2.getY(0));
                        previousPointerCoords[1] = new Coordinate(e2.getX(1), e2.getY(1));
                    } else {
                        Coordinate[] newCoords = {
                                new Coordinate(e2.getX(0), e2.getY(0)),
                                new Coordinate(e2.getX(1), e2.getY(1))
                        };
                        Coordinate VectorPrevious = previousPointerCoords[1].subtract(previousPointerCoords[0]);
                        Coordinate VectorNew = newCoords[1].subtract(newCoords[0]);
                        double diffAngle = VectorNew.angle() - VectorPrevious.angle();
                        double scale = VectorNew.length() / VectorPrevious.length();

                        // the transformations
                        view.getTransformMatrix().postTranslate((float) -previousPointerCoords[0].getX(), (float) -previousPointerCoords[0].getY());
                        view.getTransformMatrix().postRotate((float) diffAngle);
                        view.getTransformMatrix().postScale((float) scale, (float) scale);
                        view.getTransformMatrix().postTranslate((float) newCoords[0].getX(), (float) newCoords[0].getY());

                        previousPointerCoords = newCoords;
                    }
                    break;
                case 1:
                    if (EDGE_DRAW_MODE) {
                        Coordinate sCoordinate = new Coordinate(e2.getX(), e2.getY());
                        Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
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
                                startCoordinate = new Coordinate(touchedVertex.getX(), touchedVertex.getY());
                            }

                            trashCan(1);

                            Coordinate sCoordinate = new Coordinate(e2.getX(), e2.getY());

                            if (view.isOnTrashCan(sCoordinate)) {
                                trashCan(2);
                                deleteVertex = touchedVertex;
                            } else {
                                trashCan(1);
                                deleteVertex = null;

                            }

                            Coordinate gCoordinate = view.translateCoordinate(sCoordinate);
                            touchedVertex.setLocation(gCoordinate.getX(), gCoordinate.getY());

                        } else {
                            trashCan(0);
                            if (previousPointerCount == 1)
                                view.getTransformMatrix().postTranslate(-distanceX, -distanceY);
                        }

                    }
                    break;
                default: // 3 or more
                    trashCan(0);
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
                if (TRASH_CAN == 2) {
                    if (deleteVertex != null) {
                        getTopology().removeNode(deleteVertex);
                        Coordinate c = clearCoordinate();
                        if (c != null)
                            deleteVertex.setLocation(c.getX(), c.getY());
                        deleteVertex = null;
                    }
                }
                clearCoordinate();
                trashCan(0);
            }
            return gd.onTouchEvent(event);
        }

        @Override
        public void onClick(View v) {
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
                    Resources rsrc = activity.getResources();
                    int id = rsrc.getIdentifier(n.getIcon(), "drawable", activity.getPackageName());
                    Bitmap icon = BitmapFactory.decodeResource(rsrc, id);
                    if (Node.DEFAULT_DIRECTION != 0.0) {
                        Matrix mat = new Matrix();
                        double degrees = -180.0 * Node.DEFAULT_DIRECTION / Math.PI;
                        mat.setRotate((float)degrees);
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
    }



}