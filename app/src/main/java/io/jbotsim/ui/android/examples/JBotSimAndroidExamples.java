package io.jbotsim.ui.android.examples;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.jbotsim.io.serialization.topology.FileTopologySerializer;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.examples.fancy.angularforces.AngularForcesExample;
import io.jbotsim.ui.android.examples.fancy.canadairs.CanadairsExample;
import io.jbotsim.ui.android.examples.fancy.parkcleaning.ParkCleaningExample;
import io.jbotsim.ui.android.examples.fancy.vectorracer.VectorRacerExample;
import io.jbotsim.ui.android.examples.funny.soccer.SoccerExample;
import io.jbotsim.ui.android.examples.funny.wolfsheep.WolfSheepExample;
import io.jbotsim.ui.android.examples.misc.randomwalks.RandomWalkNode;
import io.jbotsim.ui.android.examples.misc.spanningforest.SpanningForestExample;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.WayPointMeetingNode;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.WayPointBasicNode;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.CircleMovement;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.DavidNode;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.GlobalRWP;
import io.jbotsim.ui.android.examples.misc.mobilitymodels.SimpleHighway;
import io.jbotsim.ui.android.AndroidViewerActivity;
import android.os.Bundle;
import android.widget.Toast;

import io.jbotsim.ui.android.examples.basic.broadcasting.BroadcastingNode;
import io.jbotsim.ui.android.examples.basic.mobilebroadcast.MobileBroadcastNode;
import io.jbotsim.ui.android.examples.basic.moving.MovingNode;
import io.jbotsim.ui.android.examples.basic.spanningtree.SpanningTreeNode;
import io.jbotsim.ui.android.examples.funny.cowboy.CowboyExample;

import java.util.HashMap;

public class JBotSimAndroidExamples extends Activity {
    private static HashMap<Integer,Object> EXAMPLES;
    static {
        EXAMPLES = new HashMap<>();
        EXAMPLES.put(R.id.movingnodes, new MovingNode.Initializer());
        EXAMPLES.put(R.id.broadcastingnodes, new BroadcastingNode.Initializer());
        EXAMPLES.put(R.id.mobilebroadcastnodes, new MobileBroadcastNode.Initializer());
        EXAMPLES.put(R.id.spanningtreenodes , new SpanningTreeNode.Initializer());
        EXAMPLES.put(R.id.cowboy , new CowboyExample());
        EXAMPLES.put(R.id.soccer , new SoccerExample());
        EXAMPLES.put(R.id.wolfsheep , new WolfSheepExample());
        EXAMPLES.put(R.id.angularforces , new AngularForcesExample());
        EXAMPLES.put(R.id.canadairs, new CanadairsExample());
        EXAMPLES.put(R.id.vectorracer, new VectorRacerExample());
        EXAMPLES.put(R.id.spanningforest, new SpanningForestExample());
        EXAMPLES.put(R.id.circlemovement, new CircleMovement());
        EXAMPLES.put(R.id.davidnode, new DavidNode());
        EXAMPLES.put(R.id.globalrwp, new GlobalRWP());
        EXAMPLES.put(R.id.simplehighway, new SimpleHighway());
        EXAMPLES.put(R.id.wpbasic, new WayPointBasicNode());
        EXAMPLES.put(R.id.wpmeeting, new WayPointMeetingNode());
        EXAMPLES.put(R.id.randomwalk, new RandomWalkNode());
        EXAMPLES.put(R.id.parkcleaning, new ParkCleaningExample());
    }

    public JBotSimAndroidExamples() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(this, R.layout.examples, null));

        for(int id : EXAMPLES.keySet()) {
            View button = findViewById(id);
            final int ident = id;
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(JBotSimAndroidExamples.this, AndroidViewerActivity.class);
                        String className = EXAMPLES.get(ident).getClass().getName();
                        intent.putExtra(getPackageName()+".EXTRA_INIT_CLASS", className);
                        intent.putExtra(getPackageName()+".EXTRA_NAME", ((Button) v).getText());

                        startActivity(intent);
                        System.out.println("Invoking "+ className);
                    }
                });
            }
        }
    }

    // Initiating Menu XML file (menu.xml)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.examples, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.exit) {
            finish();
        } else if (itemId == R.id.load) {
            load();
        }
        return true;
    }

    private static final int CHOOSE_FILENAME_REQUEST_CODE = 12345;

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == CHOOSE_FILENAME_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Intent intent = new Intent(JBotSimAndroidExamples.this, AndroidViewerActivity.class);
                intent.putExtra(getPackageName()+".EXTRA_URI", uri);
                startActivity(intent);
            }
        }
    }

    public void load() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, CHOOSE_FILENAME_REQUEST_CODE);
    }
}
