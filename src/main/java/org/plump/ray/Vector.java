package org.plump.ray;

public class Vector {

	private final double x;
	private final double y;
	private final double z;

	public Vector() {
		super();
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}

	public Vector(final double x, final double y, final double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector negate(final Vector v) {
		return new Vector(-v.getX(), -v.getY(), -v.getZ());
	}

	public static Vector add(final Vector a, final Vector b) {
		return new Vector(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
	}

	public static Vector sub(final Vector a, final Vector b) {
		return new Vector(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	public static Vector sMul(final Vector v, final double s) {
		return new Vector(v.getX() * s, v.getY() * s, v.getZ() * s);
	}

	public static Vector sDiv(final Vector v, final double s) {
		return new Vector(v.getX() / s, v.getY() / s, v.getZ() / s);
	}

	public static Vector mul(final Vector a, final Vector b) {
		return new Vector(a.getX() * b.getX(), a.getY() * b.getY(), a.getZ() * b.getZ());
	}

	public static Vector div(final Vector a, final Vector b) {
		return new Vector(a.getX() / b.getX(), a.getY() / b.getY(), a.getZ() / b.getZ());
	}

	public static double dot(final Vector a, final Vector b) {
		return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
	}

	public static Vector norm(final Vector v) {

		final double d = Vector.dot(v, v);
		if (d > 0.0) {
			return Vector.sDiv(v, Math.sqrt(d));
		}

		return new Vector(v.getX(), v.getY(), v.getZ());
	}

	public static Vector cross(final Vector a, final Vector b) {

		return new Vector(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX()
				* b.getZ(), a.getX() * b.getY() - a.getY() * b.getX());
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	@Override
	public String toString() {
		return "[ " + x + ", " + y + ", " + z + " ]";
	}
}
