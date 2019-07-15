package io.jbotsim.ui.android.simpleviewer;

import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.CommandListener;
import io.jbotsim.gen.basic.TopologyLayouts;
import io.jbotsim.ui.android.AndroidFileAccessor;
import io.jbotsim.ui.android.AndroidViewerActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Random;

public class SimpleViewerActivity extends AndroidViewerActivity
        implements CommandListener {

    static final String CMD_CIRCLE_LAYOUT = "Circle layout";
    static final String CMD_LINE_LAYOUT = "Line layout";
    static final String CMD_SAY_YEAH = "Say yeah! ";

    public SimpleViewerActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Topology topology = getTopology();

        // Register models of nodes
        topology.setDefaultNodeModel(NodeModel1.class);
        topology.setNodeModel(NodeModel2.getModelName(), NodeModel2.class);
        topology.setNodeModel(NodeModel3.getModelName(), NodeModel3.class);

        // Command registration
        topology.addCommandListener(this);
        registerCommands();

        // Set an initial topology, if needed.
        initializeTopology();
    }

    /**
     * If necessary one can setup an initial topology.
     * Here we generate a random topology wrt to declared models of nodes
     */
    void initializeTopology() {
        Topology tp = getTopology();
        String[] models = new String[tp.getModelsNames().size()];
        models = tp.getModelsNames().toArray(models);
        Random rnd = new Random();

        // Generate first nodes ...
        for (int i = 0; i < 10; i++) {
            int ni = rnd.nextInt(models.length);
            Node n = tp.newInstanceOfModel(models[ni]);
            tp.addNode(-1.0, -1.0, n);
        }
    }

    // region Command handling
    HashMap<String, CommandListener> commands = new HashMap<>();

    /**
     * One may add callbacks attached to entries of the "Commands..." sub-menu.
     * Commands are just strings displayed in the menu. They are passed to the
     * callback on invocation.
     */
    private void registerCommands() {
        addCommand(CMD_CIRCLE_LAYOUT, s -> TopologyLayouts.circle(getTopology()));
        addCommand(CMD_LINE_LAYOUT, s -> TopologyLayouts.line(getTopology(), 10.0 / 100.0));
        addCommand(CMD_SAY_YEAH, s -> shortToast("Yeah !"));
    }

    private void addCommand(String cmd, CommandListener cl) {
        if (commands.containsKey(cmd))
            throw new RuntimeException("command '" + cmd + "' is already  defined.");
        getTopology().addCommand(cmd);
        commands.put(cmd, cl);
    }

    @Override
    public void onCommand(String command) {
        if (commands.containsKey(command)) {
            CommandListener cl = commands.get(command);
            cl.onCommand(command);
        } else {
            shortToast("Unregistered command");
        }
    }
    // endregion

    // region Models of nodes
    public static class NodeModel1 extends Node {
        protected int getIconResourceID() {
            return R.drawable.bicycle;
        }

        public void onStart() {
            super.onStart();
            setDirection(Math.random()*2*Math.PI);
            setIcon(AndroidFileAccessor.getResourceURI(getIconResourceID()));
            setIconSize(16);
        }

        @Override
        public void onClock() {
            super.onClock();
            move();
            wrapLocation();
        }
    }

    public static class NodeModel2 extends NodeModel1 {
        public static String getModelName() {
            return "Car";
        }

        @Override
        protected int getIconResourceID() {
            return R.drawable.ouioui;
        }
    }

    public static class NodeModel3 extends NodeModel1 {
        public static String getModelName() {
            return "Walker";
        }

        @Override
        protected int getIconResourceID() {
            return R.raw.walker;
        }
    }
    // endregion
}
