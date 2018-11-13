package io.jbotsim.ui.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import io.jbotsim.core.Topology;
import org.json.JSONException;

import java.io.*;
import java.util.Date;

import static io.jbotsim.ui.android.R.*;

public class AndroidViewerActivity extends Activity {
    private Topology topology;
    private AndroidTopologyViewer controller;
    private volatile boolean saveOnExit = false;

    public AndroidViewerActivity() {
        this(new Topology());
    }

    public AndroidViewerActivity(Topology topology) {
        this.topology = topology;
        topology.setClockModel(AndroidClock.class);
    }

    public Topology getTopology() {
        return topology;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("start AndoidViewerActivity");

//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int width = displaymetrics.widthPixels;

        controller = new AndroidTopologyViewer(this,topology);

        setContentView(controller.getView());

        AndroidTopologyViewer.EDGE_DRAW_MODE = false;

        /**
         *  Welcome Dialog!

        String title = "Grapher";
        String message = "Tap to create vertices." + "\nHold to toggle between vertex creation and edge drawing mode.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("AbtoSimtitle);

        builder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shortToast("Go ahead and graph!");
            }
        });
        builder.setNeutralButton("Load", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                load();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
         */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        controller.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                controller.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = controller.getView().getHeight();
                int width = controller.getView().getWidth();
                topology.setDimensions(width, height);

                try {
                    Intent intent = getIntent();
                    String title = intent.getExtras().getString(getPackageName() + ".EXTRA_NAME");
                    setTitle(title);
                    String initClassName = intent.getExtras().getString(getPackageName() + ".EXTRA_INIT_CLASS");
                    Class initClass = Class.forName(initClassName);
                    Object init = null;
                    init = initClass.newInstance();
                    if (init instanceof TopologyInitializer) {
                        ((TopologyInitializer)init).initialize(topology);
                    } else if (init instanceof ViewerActivityInitializer) {
                        ((ViewerActivityInitializer)init).initialize(AndroidViewerActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getTopology().start();
            }
        });
    }

    public AndroidTopologyViewer getViewer() {
        return controller;
    }

    // Initiating Menu XML file (menu.xml)
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.topology_viewer, menu);
        return true;
    }

    /**
     * returns a string containing date and graph info
     */
    private String createFileName() {
        return new Date().toGMTString() + " " + controller.graphInfo();
    }

    @Override
    @SuppressLint("WorldReadableFiles")
    protected void onDestroy() {
        super.onDestroy();

        if (controller.getTopology().getNodes().size() > 0 && saveOnExit) {
            try {
                String json = new FileAccess().save(controller.getTopology());
                FileOutputStream fOut = openFileOutput(createFileName() + ".json", MODE_WORLD_READABLE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                // Write the string to the file
                osw.write(json);

                /*
                 * ensure that everything is really written out and close
                 */
                osw.flush();
                osw.close();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void longToast(String toast) {
        Toast.makeText(AndroidViewerActivity.this, toast, Toast.LENGTH_LONG).show();
    }

    public void shortToast(String toast) {
        Toast.makeText(AndroidViewerActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu item
     * by it's id
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // System.out.println("MenuItem      \t" + item.getTitle());
        // System.out.println(" > Condensed  \t" + item.getTitleCondensed());
        // System.out.println(" > numeric id \t" + item.getItemId());
        // System.out.println();

        int i = item.getItemId();
        if (i == id.finish) {
            saveOnExit = false;
            finish();
            return true;
        } else if (i == id.finish_and_save) {
            saveOnExit = true;
            finish();
            return true;
        } else if (i == id.new_graph) {
            return true;
        } else if (i == id.clear_colours) {
            controller.clearAll();
            return true;
        } else if (i == id.centralize) {
            controller.centralize();
            return true;
        } else if (i == id.snap || i == id.threshold || i == id.compute_maximum_independent_set ||
                i == id.compute_chromatic_number || i == id.compute_colouring ||
                i == id.compute_maximum_clique || i == id.vertex_integrity ||
                i == id.compute_minimal_triangulation || i == id.compute_steiner_tree ||
                i == id.compute_treewidth || i == id.compute_simplicial_vertices ||
                i == id.compute_chordality || i == id.compute_claw_deletion
                || i == id.compute_perfect_code || i == id.compute_claws || i == id.compute_cycle_4
                || i == id.compute_regularity_deletion_set || i == id.compute_odd_cycle_transversal
                || i == id.compute_feedback_vertex_set
                || i == id.compute_connected_feedback_vertex_set || i == id.compute_vertex_cover
                || i == id.compute_connected_vertex_cover || i == id.compute_minimum_dominating_set
                || i == id.spring || i == id.hamiltonian_path || i == id.hamiltonian_cycle
                || i == id.flow || i == id.path || i == id.power || i == id.compute_mst
                || i == id.compute_balanced_separator || i == id.compute_diameter
                || i == id.compute_girth || i == id.bipartition || i == id.compute_all_cuts
                || i == id.compute_all_bridges || i == id.test_eulerian || i == id.show_center
                || i == id.add_universal_vertex || i == id.compute_bandwidth
                || i == id.metapost_to_clipboard || i == id.tikz_to_clipboard
                || i == id.share_tikz || i == id.share_interval || i == id.interval
                || i == id.share_metapost || i == id.graph_complement || i == id.local_complement
                || i == id.contract || i == id.complement_selected) {
            return true;
        } else if (i == id.select_all) {
            controller.selectAll();
            return true;
        } else if (i == id.deselect_all) {
            controller.deselectAll();
            return true;
        } else if (i == id.select_all_highlighted_vertices) {
            return true;
        } else if (i == id.invert_selected) {
            controller.invertSelectedVertices();
            return true;
        } else if (i == id.select_reachable) {
            return true;
        } else if (i == id.complete_selected) {
            return true;
        } else if (i == id.delete_selected) {
            int deleted = controller.deleteSelectedVertices();
            if (deleted == 0) {
                shortToast("No vertices selected");
            } else {
                shortToast("Deleted " + deleted + " vertices");
            }
            return true;
        } else if (i == id.induce_subgraph) {
            return true;
        } else if (i == id.toggle_edge_edit) {
            boolean edgedraw = controller.toggleEdgeDraw();
            shortToast(edgedraw ? "Edge draw mode" : "Vertex move mode");
            return true;
        } else if (i == id.save) {
            save();
            return true;
        } else if (i == id.load) {
            load();
            return true;
        } else if (i == id.delete) {
            delete();
            return true;
        } else if (i == id.toggle_label_drawing) {
            boolean doShow = !AndroidTopologyViewer.DO_SHOW_LABELS;
            AndroidTopologyViewer.DO_SHOW_LABELS = doShow;
            if (doShow)
                shortToast("Showing labels");
            else
                shortToast("Not showing labels");
            controller.redraw();
            return true;
        } else {
            System.out.println("Option item selected, " + item.getTitle());
            return super.onOptionsItemSelected(item);
        }
    }

    public void save() {

        System.out.println("save");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("WorldReadableFiles")
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                try {
                    String json = new FileAccess().save(controller.getTopology());
                    FileOutputStream fOut = openFileOutput(value + ".json", MODE_WORLD_READABLE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    // Write the string to the file
                    osw.write(json);

                    /*
                     * ensure that everything is really written out and close
                     */
                    osw.flush();
                    osw.close();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void delete() {
        final String[] files = fileList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete file");
        builder.setItems(files, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                System.out.println("DELETE REQUEST " + item + " -- " + files[item]);

                if (deleteFile(files[item]))
                    shortToast("Deleted file " + files[item]);
                else
                    shortToast("Unable to delete file!");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void load() {
        System.out.println("load");
        final String[] files = fileList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a file");
        builder.setItems(files, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), files[item], Toast.LENGTH_SHORT).show();
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    String inputLine = "";
                    FileInputStream input = openFileInput(files[item].toString());
                    InputStreamReader isr = new InputStreamReader(input);
                    BufferedReader bufferedReader = new BufferedReader(isr);

                    while ((inputLine = bufferedReader.readLine()) != null) {
                        stringBuffer.append(inputLine);
                        stringBuffer.append("\n");
                    }

                    bufferedReader.close();
                    String json = stringBuffer.toString();
                    System.out.println(json);

                    new FileAccess().load(controller.getTopology(), json);

                    controller.clearMemory();

                    controller.makeInfo();
                    controller.redraw();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

}