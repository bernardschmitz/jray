package org.plump.ray;

public class Ray {

	private Vector base;
	private Vector dir;
	private Sphere object;

	public Ray(final Vector base, final Vector dir) {
		this(base, dir, null);
	}

	public Ray(final Vector base, final Vector dir, final Sphere object) {
		super();
		this.base = base;
		this.dir = dir;
		this.object = object;
	}

	public Vector getBase() {
		return base;
	}

	public Vector getDir() {
		return dir;
	}

	public Sphere getObject() {
		return object;
	}
}
