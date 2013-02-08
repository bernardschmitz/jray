package org.plump.ray;

public class Ray {

	private Vector3 base;
	private Vector3 dir;
	private Sphere object;

	public Ray(final Vector3 base, final Vector3 dir) {
		this(base, dir, null);
	}

	public Ray(final Vector3 base, final Vector3 dir, final Sphere object) {
		super();
		this.base = base;
		this.dir = dir;
		this.object = object;
	}

	public Vector3 getBase() {
		return base;
	}

	public Vector3 getDir() {
		return dir;
	}

	public Sphere getObject() {
		return object;
	}
}
