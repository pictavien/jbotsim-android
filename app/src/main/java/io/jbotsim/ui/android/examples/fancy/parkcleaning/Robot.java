package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;
import io.jbotsim.core.event.ClockListener;

import io.jbotsim.core.Point;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

import java.util.List;

public class Robot extends Node implements ClockListener{
	protected Point target;
	double step = 1;
	
	public Robot(){
	    setIcon(Icon.getResourceURI(R.drawable.gmrobot));
		setSize(10);
		setSensingRange(30);
		disableWireless();
	}		

	public void onClock() {
		if (target != null){
			if (distance(target) > step){
				setDirection(target);
				move(step);
			}else target=null;
		}
		List<Node> sensedObjects = this.getSensedNodes();
		for (Node thing : sensedObjects)
			if (thing instanceof Garbage && thing.getColor()==getColor())
				getTopology().removeNode(thing);
	}
}
