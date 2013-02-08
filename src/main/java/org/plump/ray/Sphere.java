package org.plump.ray;

public class Sphere {

	private static final double EP = 0.001;

	private static long intersections = 0;

	private Vector center;
	private Vector dcolor;
	private Vector scolor;
	private double radius;
	private double radius2;
	private double ka;
	private double kd;
	private double ks;
	private double n;
	private double reflect;
	private double transmit;
	private double ior;

	public Sphere(final Vector center, final double radius) {
		super();
		this.center = center;
		this.radius = radius;
		radius2 = radius * radius;
	}

	public Sphere(final Vector center, final double radius, final Vector dcolor,
			final Vector scolor, final double ka, final double kd, final double ks, final double n,
			final double reflect, final double transmit, final double ior) {
		super();
		this.center = center;
		this.dcolor = dcolor;
		this.scolor = scolor;
		this.radius = radius;
		radius2 = radius * radius;
		this.ka = ka;
		this.kd = kd;
		this.ks = ks;
		this.n = n;
		this.reflect = reflect;
		this.transmit = transmit;
		this.ior = ior;
	}

	public static long getIntersections() {
		return Sphere.intersections;
	}

	public double intersect(final Ray r) {

		Sphere.intersections++;

		final Vector q = Vector.sub(r.getBase(), center);
		final double b = 2.0 * Vector.dot(r.getDir(), q);
		final double c = Vector.dot(q, q) - radius2;

		final double d = b * b - 4.0 * c;

		if (d < 0.0) {
			return -1.0;
		}

		final double t1 = (-b + Math.sqrt(d)) / 2.0;
		final double t2 = (-b - Math.sqrt(d)) / 2.0;

		if (t1 < EP && t2 < EP) {
			return -1.0;
		}

		double t;
		if (t1 < t2) {
			t = t1;
			if (Math.abs(t) < EP) {
				t = t2;
				if (Math.abs(t) < EP) {
					return -1.0;
				}
			}
		} else {
			t = t2;
			if (Math.abs(t) < EP) {
				t = t1;
				if (Math.abs(t) < EP) {
					return -1.0;
				}
			}
		}

		if (t > EP) {
			return t;
		}

		return -1.0;
	}

	public Vector getCenter() {
		return center;
	}

	public Vector getDcolor() {
		return dcolor;
	}

	public Vector getScolor() {
		return scolor;
	}

	public double getRadius() {
		return radius;
	}

	public double getRadius2() {
		return radius2;
	}

	public double getKa() {
		return ka;
	}

	public double getKd() {
		return kd;
	}

	public double getKs() {
		return ks;
	}

	public double getN() {
		return n;
	}

	public double getReflect() {
		return reflect;
	}

	public double getTransmit() {
		return transmit;
	}

	public double getIor() {
		return ior;
	}

}
