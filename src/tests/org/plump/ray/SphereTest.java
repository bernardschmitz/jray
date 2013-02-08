package org.plump.ray;

import junit.framework.TestCase;

public class SphereTest extends TestCase {

	private static final double DELTA = 0.0001;

	public void testIntersect() throws Exception {

		final Sphere s = new Sphere(new Vector(0, 0, 0), 0.5);
		Ray r = new Ray(new Vector(1, 0, 0), new Vector(-1, 0, 0));

		double t = s.intersect(r);

		assertEquals(0.5, t, DELTA);

		final Vector base = new Vector(1, 1, 0);
		final Vector dir = Vector.norm(Vector.sub(new Vector(0, 0, 0), base));

		r = new Ray(base, dir);

		t = s.intersect(r);

		assertEquals(0.9142, t, DELTA);
	}
}
