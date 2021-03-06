package io.jbotsim.ui.android.examples.centralized;

import io.jbotsim.core.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.core.event.ConnectivityListener;
import io.jbotsim.ui.android.TopologyInitializer;

public class Coloring implements ConnectivityListener{

    Topology tp;
    List<Color> usedColors;

    public Coloring(Topology tp){
        this.tp = tp;
        tp.addConnectivityListener(this);
    }
    public void onLinkAdded(Link link) {
        updateColoring();
    }

    public void onLinkRemoved(Link link) {
        updateColoring();
    }

    private Color nextColor(){
        if (usedColors.size()<8){
            Color color;
            do{
                color = getColor((new Random()).nextInt(8));
            }while (usedColors.contains(color));
            usedColors.add(color);
            return color;
        }else
            return null;
    }
    private void updateColoring(){
        usedColors = new ArrayList<Color>();
        for (Node node : tp.getNodes())
            node.setColor(null);
        for (Node node : tp.getNodes()){
            for (Color color : usedColors){
                if (!getNeighboringColors(node).contains(color)){
                    node.setColor(color);
                    break;
                }
            }
            if (node.getColor() == null){
                node.setColor(nextColor());
            }
        }
    }
    protected Color getColor(int value){
        switch(value){
            case 0: return Color.black;
            case 1: return Color.magenta;
            case 2: return Color.red;
            case 3: return Color.blue;
            case 4: return Color.green;
            case 5: return Color.pink;
            case 6: return Color.orange;
            case 7: return Color.yellow;
            default: return Color.gray;
        }
    }

    private Set<Color> getNeighboringColors(Node node){
        Set<Color> colors = new HashSet<Color>();
        for (Node ng : node.getNeighbors())
            colors.add(ng.getColor());
        return colors;
    }

    public static class Initializer implements TopologyInitializer {
        @Override
        public boolean initialize(Topology tp) {
            new Coloring(tp);
            return true;
        }
    }
}
