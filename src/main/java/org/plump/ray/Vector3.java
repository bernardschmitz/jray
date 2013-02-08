package org.plump.ray;

public class Vector3 {

	private final double x;
	private final double y;
	private final double z;

	public Vector3() {
		super();
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}

	public Vector3(final double x, final double y, final double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector3 negate(final Vector3 v) {
		return new Vector3(-v.getX(), -v.getY(), -v.getZ());
	}

	public static Vector3 add(final Vector3 a, final Vector3 b) {
		return new Vector3(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
	}

	public static Vector3 sub(final Vector3 a, final Vector3 b) {
		return new Vector3(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	public static Vector3 sMul(final Vector3 v, final double s) {
		return new Vector3(v.getX() * s, v.getY() * s, v.getZ() * s);
	}

	public static Vector3 sDiv(final Vector3 v, final double s) {
		return new Vector3(v.getX() / s, v.getY() / s, v.getZ() / s);
	}

	public static Vector3 mul(final Vector3 a, final Vector3 b) {
		return new Vector3(a.getX() * b.getX(), a.getY() * b.getY(), a.getZ() * b.getZ());
	}

	public static Vector3 div(final Vector3 a, final Vector3 b) {
		return new Vector3(a.getX() / b.getX(), a.getY() / b.getY(), a.getZ() / b.getZ());
	}

	public static double dot(final Vector3 a, final Vector3 b) {
		return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
	}

	public static Vector3 norm(final Vector3 v) {

		final double d = Vector3.dot(v, v);
		if (d > 0.0) {
			return Vector3.sDiv(v, Math.sqrt(d));
		}

		return new Vector3(v.getX(), v.getY(), v.getZ());
	}

	public static Vector3 cross(final Vector3 a, final Vector3 b) {

		return new Vector3(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX()
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
