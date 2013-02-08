package org.plump.ray;

public class Light {

	private Vector3 position;
	private Vector3 color;

	public Light(final Vector3 position, final Vector3 color) {
		super();
		this.position = position;
		this.color = color;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getColor() {
		return color;
	}

}
