package io.jbotsim.ui.android.examples.fancy.parkcleaning;

import io.jbotsim.core.Node;

public class Garbage extends Node{
	public Garbage(){
		disableWireless();
		setIcon("gmgarbage");
		setSize(12);
	}
}
