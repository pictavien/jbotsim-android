package io.jbotsim.ui.android;

import io.jbotsim.core.Point;

public class Coordinate extends Point {
	public Coordinate(float x, float y) {
		super(x, y);
	}

	public Coordinate(double x, double y) {
		super (x, y);
	}

	public double angle() {
		double angle = Math.atan2(y, x);
		return angle * (180.0 / Math.PI);
	}
	
	public double length() {
		return distance(0.0,0.0);
	}

	public Coordinate subtract(Coordinate c) {
		return new Coordinate(x - c.x, y - c.y);
	}
}
