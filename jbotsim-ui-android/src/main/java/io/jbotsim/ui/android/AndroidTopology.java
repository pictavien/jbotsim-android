package io.jbotsim.ui.android;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.event.CommandListener;
import io.jbotsim.ui.android.painting.BackgroundPainter;
import io.jbotsim.ui.android.painting.LinkPainter;
import io.jbotsim.ui.android.painting.NodePainter;

import java.util.ArrayList;

public class AndroidTopology extends View {
    private final Bitmap trashBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trash64x64);
    private final Bitmap trashRedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trash_red64x64);
    private final Bitmap trashBgRedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_bg);
    private final Paint trashPaint = new Paint();
    protected ArrayList<BackgroundPainter> backgroundPainters = new ArrayList<>();
    protected LinkPainter linkPainter;
    protected ArrayList<NodePainter> nodePainters = new ArrayList<>();
    protected ArrayList<CommandListener> commandListeners = new ArrayList<>();
    protected ArrayList<String> commands = new ArrayList<String>();
    protected boolean showDrawings = true;
    private Topology topology;
    private String info = "ZOBZOB";
    private Matrix transformMatrix = new Matrix();
    private boolean trashCoordinatesSet = false;
    private int trashX = -1;
    private int trashY = -1;

    public AndroidTopology(Context context, Topology topology) {
        super(context);
        this.topology = topology;
        setFocusable(true);

        System.out.println("done!?!");
        System.out.println("GraphView initialized");
    }

    public Topology getTopology() {
        return topology;
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
     * Registers the specified action listener to this JTopology.
     *
     * @param al The listener to add.
     */
    public void addCommandListener(CommandListener al) {
        commandListeners.add(al);
    }

    /**
     * Unregisters the specified action listener to this JTopology.
     *
     * @param al The listener to remove.
     */
    public void removeCommandListener(CommandListener al) {
        commandListeners.remove(al);
    }

    /**
     * Adds the specified action command to this JTopology.
     *
     * @param command The command name to add.
     */
    public void addCommand(String command) {
        commands.add(command);
    }

    /**
     * Removes the specified action command from this JTopology.
     *
     * @param command The command name to remove.
     */
    public void removeCommand(String command) {
        commands.remove(command);
    }

    /**
     * Removes all commands from this JTopology.
     */
    public void removeAllCommands() {
        commands.clear();
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

    public void redraw(String info) {
        this.info = info;
        invalidate();
    }

    /**
     * Returns the coordinate the given point/coordinate on the screen represents
     * in the graph
     */
    public Coordinate translateCoordinate(Coordinate screenCoordinate) {
        float[] screenPoint = {(float) screenCoordinate.getX(), (float) screenCoordinate.getY()};
        Matrix invertedTransformMatrix = new Matrix();

        getTransformMatrix().invert(invertedTransformMatrix);
        invertedTransformMatrix.mapPoints(screenPoint);

        return new Coordinate(screenPoint[0], screenPoint[1]);
    }

    public boolean isOnTrashCan(Coordinate c) {
        int x = (int) c.getX();
        int y = (int) c.getY();

        return (x >= trashX && x <= trashX + trashBitmap.getWidth() && y >= trashY && y <= trashY + trashBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /** TODO fix scaling of topology.
         *  It makes problems for the selection of nodes.
         *
        if (getWidth() != topology.getWidth() || getHeight() != topology.getHeight()) {
            Matrix m = new Matrix();
            RectF src = new RectF(0f, 0f, topology.getWidth(), topology.getHeight());
            RectF dst = new RectF(0f, 0f, getWidth(), getHeight());
            m.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
            canvas.concat(m);
        }
        */
        assert (topology != null);

        if (!trashCoordinatesSet) {
            if (getWidth() > 0 && getHeight() > 0) {
                trashX = (getWidth() - trashBitmap.getWidth()) / 2;
                trashY = getHeight() - trashBitmap.getHeight() - 10;
                trashCoordinatesSet = true;
            }
        }

        setBackgroundColor(Color.WHITE);

        for (BackgroundPainter bp : backgroundPainters) {
            bp.paintBackground(canvas, topology);
        }

        for (Node node : topology.getNodes()) {
            for (NodePainter np : nodePainters) {
                np.paintNode(canvas, node);
            }
        }

        for (Link link : topology.getLinks()) {
            linkPainter.paintLink(canvas, link);
        }
        displayTrash(canvas);
        writeInfo(canvas);
    }

    private void displayTrash(Canvas canvas) {
        if (AndroidTopologyViewer.TRASH_CAN == 1) {
            canvas.drawBitmap(trashBitmap, trashX, trashY, trashPaint);
        } else if (AndroidTopologyViewer.TRASH_CAN == 2) {
            canvas.drawBitmap(trashBgRedBitmap, trashX - 46, trashY - 46, trashPaint);
            canvas.drawBitmap(trashRedBitmap, trashX, trashY, trashPaint);
        }
    }

    private void writeInfo(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        canvas.drawText(info, 100, 100, textPaint);
    }
}
