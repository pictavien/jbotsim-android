package io.jbotsim.ui.android;

public class Point extends io.jbotsim.core.Point {
	public Point(float x, float y) {
		super(x, y);
	}

	public Point(double x, double y) {
		super (x, y);
	}

	public double angle() {
		double angle = Math.atan2(y, x);
		return angle * (180.0 / Math.PI);
	}
	
	public double length() {
		return distance(0.0,0.0);
	}

	public Point subtract(Point c) {
		return new Point(x - c.x, y - c.y);
	}
}
