package org.plump.ray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

public class RayToken {

	private List<Number> numbers = new ArrayList<Number>();
	private int index = 0;

	public RayToken(final String file) throws IOException {
		super();

		final StreamTokenizer in = new StreamTokenizer(new BufferedReader(new FileReader(file)));

		int token;
		while ((token = in.nextToken()) != StreamTokenizer.TT_EOF) {
			if (token != StreamTokenizer.TT_NUMBER) {
				throw new IllegalArgumentException("Error parsing input at line: " + in.lineno());
			}
			// System.out.println(in.nval);
			numbers.add(in.nval);
		}
	}

	public Sphere nextSphere() {

		final Vector pos = nextVector();
		final double r = nextDouble();
		final Vector dc = nextVector();
		final Vector sc = nextVector();
		final double ka = nextDouble();
		final double kd = nextDouble();
		final double ks = nextDouble();
		final double n = nextDouble();
		final double rf = nextDouble();
		final double tr = nextDouble();
		final double ior = nextDouble();
		return new Sphere(pos, r, dc, sc, ka, kd, ks, n, rf, tr, ior);
	}

	public Light nextLight() {
		final Vector pos = nextVector();
		final Vector col = nextVector();
		return new Light(pos, col);
	}

	public Vector nextVector() {
		final double x = nextDouble();
		final double y = nextDouble();
		final double z = nextDouble();
		return new Vector(x, y, z);
	}

	public int nextInt() {
		return numbers.get(index++).intValue();
	}

	public double nextDouble() {
		return numbers.get(index++).doubleValue();
	}
}
