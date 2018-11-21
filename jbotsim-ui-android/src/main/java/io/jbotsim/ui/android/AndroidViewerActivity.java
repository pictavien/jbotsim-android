package io.jbotsim.ui.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import io.jbotsim.core.Topology;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.jbotsim.io.serialization.topology.FileTopologySerializer;
import io.jbotsim.io.serialization.topology.string.xml.XMLTopologySerializer;
import io.jbotsim.ui.android.event.CommandListener;

public class AndroidViewerActivity
        extends Activity {
    private Topology topology;
    private AndroidTopologyViewer controller;

    private enum SeekBarMode {
        NONE(-1),
        COMM_RANGE(R.drawable.comm_range_thumb),
        SENSING_RANGE(R.drawable.sensing_range_thumb),
        CLOCKSPEED(R.drawable.clock_speed_thumb);

        private int mode;
        SeekBarMode(int m) {
            mode = m;
        }

        public int getIntValue() {
            return mode;
        }
    };

    private static final int SEEKBAR_MAX_PERIOD = 40;
    private SeekBar seekBar = null;
    private SeekBarMode seekBarMode;
    private HashMap<SeekBarMode, Drawable> bmpCache = new HashMap<>();

    private volatile boolean saveOnExit = false;
    protected ArrayList<CommandListener> commandListeners = new ArrayList<>();
    protected ArrayList<String> commands = new ArrayList<String>();

    public AndroidViewerActivity() {
        this(new Topology());
    }

    public AndroidViewerActivity(Topology topology) {
        this.topology = topology;
        topology.setFileAccessor(new AndroidFileAccessor(this));
        topology.setClockModel(AndroidClock.class);
        topology.setTopologySerializer(new XMLTopologySerializer());
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

        setContentView(R.layout.topology_viewer);

        controller = findViewById(R.id.topologyview);
        controller.setTopology(topology);

        setupSimulationButtons();
        setupSeekBar();

        AndroidTopologyViewer.EDGE_DRAW_MODE = false;

        /**
         *  Welcome Dialog!

         String title = "Grapher";
         String message = "Tap to create vertices." + "\nHold to toggle between vertex creation and edge drawing mode.";

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(message).setTitle("AbtoSimtitle);

         builder.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        shortToast("Go ahead and graph!");
        }
        });
         builder.setNeutralButton("Load", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        load();
        }
        });

         AlertDialog dialog = builder.create();
         dialog.show();
         */
    }

    private void setupSimulationButtons() {
        Button startButton = findViewById(R.id.startbutton);
        final Button resetButton = findViewById(R.id.resetbutton);
        final Button stepButton = findViewById(R.id.stepbutton);
        resetButton.setVisibility(View.GONE);
        stepButton.setVisibility(View.GONE);

        if (topology.isStarted()) {
            startButton.setText(R.string.pause);
        } else {
            startButton.setText(R.string.start);
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                Topology tp = controller.getTopology();

                if (tp.isRunning()) {
                    tp.pause();
                    b.setText(R.string.resume);
                    resetButton.setVisibility(View.VISIBLE);
                    stepButton.setVisibility(View.VISIBLE);
                } else {
                    ((Button) v).setText(R.string.pause);
                    if (tp.isStarted())
                        tp.resume();
                    else
                        tp.start();
                    resetButton.setVisibility(View.GONE);
                    stepButton.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.stepbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topology tp = getTopology();

                if (!tp.isRunning() && tp.isStarted())
                    tp.step();
            }
        });

        findViewById(R.id.stepbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topology tp = getTopology();

                if (!tp.isRunning() && tp.isStarted())
                    tp.step();
            }
        });
    }

    private Drawable getDrawableForSeekBarMode (SeekBarMode mode) {
        if (mode == SeekBarMode.NONE)
            return null;
        Drawable result = null;

        if (bmpCache.containsKey(mode)) {
            result = bmpCache.get(mode);
        } else {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), mode.getIntValue());
            Matrix m = new Matrix();
            float ratio = (float) (0.5f*seekBar.getHeight())/ (float) bmp.getHeight();
            m.setScale(ratio, ratio);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
            result = new BitmapDrawable(getResources(), bmp);
            bmpCache.put(mode, result);
        }

        return result;
    }

    private void setupSeekBar() {
        seekBar = findViewById(R.id.seekbar);
        setSeekBarMode(SeekBarMode.NONE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBarMode) {
                    case SENSING_RANGE:
                        getTopology().setSensingRange(progress);
                        break;
                    case CLOCKSPEED:
                        if (SEEKBAR_MAX_PERIOD - progress > 0)
                            getTopology().setClockSpeed(SEEKBAR_MAX_PERIOD - progress);
                        break;
                    case COMM_RANGE:
                        getTopology().setCommunicationRange(progress);
                        break;
                    default:
                        assert (seekBarMode != SeekBarMode.NONE);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSeekBarMode(SeekBarMode mode) {
        Drawable bmp = getDrawableForSeekBarMode(mode);
        seekBarMode = mode;
        int value = 0;
        int maxvalue = 0;

        Topology tp = getTopology();
        switch (mode) {
            case SENSING_RANGE:
                value = (int) tp.getSensingRange();
                maxvalue = Math.max(tp.getWidth(), tp.getHeight());
                break;
            case CLOCKSPEED:
                value = SEEKBAR_MAX_PERIOD - tp.getClockSpeed();
                maxvalue = SEEKBAR_MAX_PERIOD - 1;
                break;
            case COMM_RANGE:
                value = (int) tp.getCommunicationRange();
                maxvalue = Math.max(tp.getWidth(), tp.getHeight());
                break;
            default:
                assert (mode == SeekBarMode.NONE);
                break;
        }
        if (bmp == null)
            seekBar.setVisibility(View.INVISIBLE);
        else {

            seekBar.setThumb(bmp);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(value);
            seekBar.setMax(maxvalue);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        controller.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                controller.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                try {
                    Intent intent = getIntent();
                    Bundle extras = intent.getExtras();
                    if (extras.containsKey(getPackageName() + ".EXTRA_URI")) {
                        load((Uri) extras.get(getPackageName() + ".EXTRA_URI"));
                    } else {
                        String title = extras.getString(getPackageName() + ".EXTRA_NAME");
                        setTitle(title);
                        String initClassName = extras.getString(getPackageName() + ".EXTRA_INIT_CLASS");
                        Class initClass = Class.forName(initClassName);
                        Object init = null;
                        init = initClass.newInstance();
                        if (init instanceof TopologyInitializer) {
                            ((TopologyInitializer) init).initialize(topology);
                        } else if (init instanceof ViewerActivityInitializer) {
                            ((ViewerActivityInitializer) init).initialize(AndroidViewerActivity.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mi = menu.findItem(R.id.commandmenu);
        Menu commandsMenu = mi.getSubMenu();
        commandsMenu.clear();
        for (String c : commands) {
            MenuItem mic = commandsMenu.add(c);
            mic.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    notifyCommandListeners(item.getTitle().toString());
                    return true;
                }
            });
        }
        return true;
    }

    protected void notifyCommandListeners(String command) {
        for (CommandListener cl : commandListeners) {
            cl.onCommand(command);
        }
    }

    /**
     * returns a string containing date and graph info
     */
    private String createFileName() {
        return new Date().toGMTString() + " ";
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
        System.out.println(toast);
        Toast.makeText(AndroidViewerActivity.this, toast, Toast.LENGTH_LONG).show();
    }

    public void shortToast(String toast) {
        System.out.println(toast);
        Toast.makeText(AndroidViewerActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu item
     * by it's id
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        boolean result = true;

        if (i == R.id.quit) {
            saveOnExit = false;
            finish();
        } else if (i == R.id.save) {
            save();
        } else if (i == R.id.load) {
            load();
        } else if (i == R.id.reset_size) {
            controller.resetTopologySize();
        } else if (i == R.id.set_size_of_screen) {
            controller.resizeTopologyToScreen();
        } else if (i == R.id.set_clock_speed || i == R.id.set_sensing_range
                || i == R.id.set_comm_range) {
            SeekBarMode newMode;

            if (i == R.id.set_clock_speed) {
                newMode = SeekBarMode.CLOCKSPEED;
            } else if (i == R.id.set_sensing_range) {
                newMode = SeekBarMode.SENSING_RANGE;
            } else {
                assert (i == R.id.set_comm_range);
                newMode = SeekBarMode.COMM_RANGE;
            }
            if (newMode == seekBarMode)
                newMode = SeekBarMode.NONE;
            setSeekBarMode(newMode);
        } else {
            result = super.onOptionsItemSelected(item);
        }

        return result;
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


    public static final int CHOOSE_FILENAME_REQUEST_CODE = 12345;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == CHOOSE_FILENAME_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                load(resultData.getData());
            }
        }
    }

    private void load(Uri uri) {
        Topology tp = getTopology();
        boolean restart = tp.isRunning();
        if (restart)
            tp.pause();
        tp.clear();
        setTitle(R.string.jbotsim);
        new FileTopologySerializer().importFromFile(tp, uri.toString());
        shortToast("Loading "+ uri);
        if (restart)
            tp.resume();
    }

    public void load() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, CHOOSE_FILENAME_REQUEST_CODE);
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

    public List<String> getCommands() {
        return commands.subList(0, commands.size() - 1);
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
}