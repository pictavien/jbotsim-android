package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;
import io.jbotsim.ui.android.AndroidFileAccessor;
import io.jbotsim.ui.android.examples.R;

public class Garbage extends Node {
	public Garbage(){
		disableWireless();
		setIcon(AndroidFileAccessor.getResourceURI(R.drawable.gmgarbage));
		setIconSize(12);
	}
}
