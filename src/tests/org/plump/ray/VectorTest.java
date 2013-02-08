package org.plump.ray;

import junit.framework.TestCase;

public class VectorTest extends TestCase {

	private static final double DELTA = 0.0001;

	public void testNegate() throws Exception {

		final Vector v = new Vector(1, 2, 3);
		final Vector nv = Vector.negate(v);

		assertEquals(-1.0, nv.getX(), DELTA);
		assertEquals(-2.0, nv.getY(), DELTA);
		assertEquals(-3.0, nv.getZ(), DELTA);
	}

	public void testAdd() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final Vector c = Vector.add(a, b);

		assertEquals(4.0, c.getX(), DELTA);
		assertEquals(11.0, c.getY(), DELTA);
		assertEquals(-2.0, c.getZ(), DELTA);
	}

	public void testSub() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final Vector c = Vector.sub(a, b);

		assertEquals(-2.0, c.getX(), DELTA);
		assertEquals(-7.0, c.getY(), DELTA);
		assertEquals(8.0, c.getZ(), DELTA);
	}

	public void testSMul() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector c = Vector.sMul(a, 5);

		assertEquals(5.0, c.getX(), DELTA);
		assertEquals(10.0, c.getY(), DELTA);
		assertEquals(15.0, c.getZ(), DELTA);
	}

	public void testSDiv() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector c = Vector.sDiv(a, 5);

		assertEquals(0.2, c.getX(), DELTA);
		assertEquals(0.4, c.getY(), DELTA);
		assertEquals(0.6, c.getZ(), DELTA);
	}

	public void testMul() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final Vector c = Vector.mul(a, b);

		assertEquals(3.0, c.getX(), DELTA);
		assertEquals(18.0, c.getY(), DELTA);
		assertEquals(-15.0, c.getZ(), DELTA);
	}

	public void testDiv() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final Vector c = Vector.div(a, b);

		assertEquals(0.3333, c.getX(), DELTA);
		assertEquals(0.2222, c.getY(), DELTA);
		assertEquals(-0.6, c.getZ(), DELTA);
	}

	public void testDot() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final double c = Vector.dot(a, b);

		assertEquals(6, c, DELTA);
	}

	public void testNorm() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector c = Vector.norm(a);

		assertEquals(0.2672, c.getX(), DELTA);
		assertEquals(0.5345, c.getY(), DELTA);
		assertEquals(0.8017, c.getZ(), DELTA);
	}

	public void testCross() throws Exception {

		final Vector a = new Vector(1, 2, 3);
		final Vector b = new Vector(3, 9, -5);
		final Vector c = Vector.cross(a, b);

		assertEquals(-37, c.getX(), DELTA);
		assertEquals(14, c.getY(), DELTA);
		assertEquals(3, c.getZ(), DELTA);
	}

}
