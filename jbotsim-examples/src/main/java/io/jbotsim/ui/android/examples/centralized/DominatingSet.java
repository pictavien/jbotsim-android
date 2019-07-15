package io.jbotsim.ui.android.examples.centralized;

import io.jbotsim.core.Color;
import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.android.TopologyInitializer;

public class DominatingSet extends Node {

	public DominatingSet(){
		this.setColor(Color.black);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public void onLinkAdded(Link l) {
		update();
	}

	public void onLinkRemoved(Link l) {
		update();
	}
	
	public void update(){
		if (getColor() == Color.white){
			if (!isDominated(this))
				setColor(Color.black);
		}else{ 
			if (!isEssential(this))
				setColor(Color.red);
		}		
	}
	//////////////////////////////////////////////////////////////////////////
	
	public static boolean isDominated(Node v){
		return (getNumberOfConnectedDominators(v)!=0);
	}
	public static boolean isEssential(Node v){
		assert(v.getColor() == Color.black);
		if (getNumberOfConnectedDominators(v) == 1)
			return true;
		for (Node ng : v.getNeighbors())
			if (getNumberOfConnectedDominators(ng) == 1)
				return true;
		return false;
	}
	public static int getNumberOfConnectedDominators(Node v){
		int nbDom=0;
		if (v.getColor() == Color.black)
			nbDom++;
		for (Node ng : v.getNeighbors())
			if (ng.getColor() == Color.black)
				nbDom++;
		return nbDom;		
	}

	public static class Initializer implements TopologyInitializer {
		@Override
		public boolean initialize(Topology tp) {
			tp.setDefaultNodeModel(DominatingSet.class);
			return true;
		}
	}
}
