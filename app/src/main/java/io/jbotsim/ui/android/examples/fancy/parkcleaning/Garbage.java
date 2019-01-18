package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.examples.R;
import io.jbotsim.ui.android.utils.Icon;

public class Garbage extends Node {
	public Garbage(){
		disableWireless();
		setIcon(Icon.getResourceURI(R.drawable.gmgarbage));
		setSize(12);
	}
}
