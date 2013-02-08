package org.plump.ray;

public class Light {

	private Vector position;
	private Vector color;

	public Light(final Vector position, final Vector color) {
		super();
		this.position = position;
		this.color = color;
	}

	public Vector getPosition() {
		return position;
	}

	public Vector getColor() {
		return color;
	}

}
