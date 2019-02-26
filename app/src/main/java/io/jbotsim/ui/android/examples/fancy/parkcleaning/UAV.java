package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

public class UAV extends Node {
	public UAV(){
		this.setCommunicationRange(100);
		this.setSensingRange(40);
		this.setIcon(Icon.getResourceURI(R.drawable.gmuav));
		this.setIconSize(20);
		this.setProperty("type", "rwp");
		this.setDirection(2);
	}

	@Override
	public void onSensingIn(Node node) {
		if (node instanceof Garbage && getColor()==null) {
			setProperty("examples/n_parc", node);
			setColor(node.getColor());
		}
		if (node instanceof Robot &&
				node.getColor()==this.getColor() && ((Robot)node).target==null){
			((Robot)node).target = ((Node)this.getProperty("examples/n_parc")).getLocation();
			setColor(null);
		}
	}

}
