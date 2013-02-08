package org.plump.ray;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RayTracer {

	private Vector background;
	private Vector ambientLight;
	private List<Sphere> objects = new ArrayList<Sphere>();
	private List<Light> lights = new ArrayList<Light>();
	private Vector eye;
	private Vector at;
	private Vector sky;
	private double dis;
	private double fov;
	private int width;
	private int height;
	private int maxDepth;
	private long totalrays;
	private long shadowrays;
	private long reflectionrays;
	private long refractionrays;

	public RayTracer(final Vector eye, final Vector at, final Vector sky, final double dis,
			final double fov, final int width, final int height, final int maxDepth,
			final Vector ambientLight, final Vector background) {
		super();
		this.background = background;
		this.ambientLight = ambientLight;
		this.eye = eye;
		this.at = at;
		this.sky = Vector.norm(sky);
		this.dis = dis;
		this.fov = fov;
		this.width = width;
		this.height = height;
		this.maxDepth = maxDepth;
	}

	public int objectCount() {
		return objects.size();
	}

	public int lightCount() {
		return lights.size();
	}

	public void addObject(final Sphere object) {
		objects.add(object);
	}

	public void addLight(final Light light) {
		lights.add(light);
	}

	public Vector traceRay(final Ray ray, final int depth) {

		totalrays++;

		double mt = Double.MAX_VALUE;
		Sphere o = null;

		for (final Sphere s : objects) {
			if (ray.getObject() == s) {
				continue;
			}

			final double t = s.intersect(ray);
			if (t > 0.0 && t < mt) {
				mt = t;
				o = s;
			}
		}

		if (o == null) {
			return background;
		}

		final Ray r = new Ray(Vector.add(ray.getBase(), Vector.sMul(ray.getDir(), mt)), ray
				.getDir());
		final Vector normal = Vector.norm(Vector.sub(r.getBase(), o.getCenter()));

		return shade(o, r, normal, depth + 1);
	}

	private Vector shade(final Sphere object, final Ray ray, final Vector normal, final int depth) {

		// return object.getDcolor();
		Vector intensity = Vector
				.mul(Vector.sMul(object.getDcolor(), object.getKa()), ambientLight);

		for (final Light light : lights) {
			final Ray shad = new Ray(ray.getBase(), Vector.norm(Vector.sub(light.getPosition(), ray
					.getBase())));
			final double nl = Vector.dot(normal, shad.getDir());
			if (nl > 0.0) {
				Vector inter = light.getColor();

				for (final Sphere s : objects) {
					shadowrays++;
					final double t = s.intersect(shad);
					if (t > 0.0) {
						if (s.getTransmit() > 0.0) {
							inter = Vector.add(Vector.sMul(inter, s.getTransmit()), Vector.sMul(s
									.getDcolor(), 1.0 - s.getTransmit()));
						} else {
							inter = new Vector(0.0, 0.0, 0.0);
							break;
						}
					}
				}

				intensity = Vector.add(intensity, Vector.sMul(
						Vector.mul(inter, object.getDcolor()), object.getKd() * nl));

				if (object.getKs() > 0.0) {
					final Vector reflect = Vector.norm(Vector.sub(Vector.sMul(normal, 2.0 * nl),
							shad.getDir()));
					final Vector view = Vector.norm(Vector.sub(eye, ray.getBase()));
					intensity = Vector.add(intensity, Vector.sMul(Vector.mul(inter, object
							.getScolor()), object.getKs()
							* Math.pow(Vector.dot(reflect, view), object.getN())));
				}
			}
		}

		if (depth < maxDepth) {

			if (object.getReflect() > 0.0) {
				reflectionrays++;
				final double nl = Vector.dot(normal, ray.getDir());
				final Ray reflect = new Ray(ray.getBase(), Vector.norm(Vector.sub(ray.getDir(),
						Vector.sMul(normal, 2.0 * nl))), object);
				intensity = Vector.add(intensity, Vector.sMul(traceRay(reflect, depth + 1), object
						.getKs()
						* object.getReflect()));
			}

			if (object.getTransmit() > 0.0) {
				refractionrays++;
				if (object.getIor() > 0.0) {
					final double n = 1.0 / object.getIor();
					final double c = Vector.dot(Vector.negate(ray.getDir()), normal);
					double nl = 1.0 - n * n * (1.0 - c * c);
					if (nl >= 0.0) {
						nl = n * c - Math.sqrt(nl);
						final Ray refract = new Ray(ray.getBase(), Vector.norm(Vector.sub(Vector
								.sMul(normal, nl), Vector.sMul(Vector.negate(ray.getDir()), n))),
								object);
						intensity = Vector.add(intensity, Vector.sMul(traceRay(refract, depth + 1),
								object.getKs() * object.getTransmit()));
					}
				} else {
					final Ray refract = new Ray(ray.getBase(), ray.getDir(), object);
					intensity = Vector.add(intensity, Vector.sMul(traceRay(refract, depth + 1),
							object.getKs() * object.getTransmit()));
				}
			}
		}

		return intensity;
	}

	public void rayTrace(final OutputStream out) throws IOException {

		final Vector dir = Vector.norm(Vector.sub(at, eye));
		final Vector right = Vector.norm(Vector.cross(dir, sky));
		final Vector up = Vector.norm(Vector.cross(right, dir));

		final double w = dis * Math.tan(fov / 2.0 * Math.PI / 180.0);
		final double h = w * height / width;

		at = Vector.add(eye, dir);
		final Vector ww = Vector.sMul(right, w);
		final Vector hh = Vector.sMul(up, h);
		final Vector tl = Vector.add(Vector.sub(at, ww), hh);
		final Vector bl = Vector.sub(Vector.sub(at, ww), hh);
		final Vector br = Vector.sub(Vector.add(at, ww), hh);
		final Vector tr = Vector.add(Vector.add(at, ww), hh);

		final Vector ld = Vector.sDiv(Vector.sub(bl, tl), height);
		final Vector rd = Vector.sDiv(Vector.sub(br, tr), height);

		Vector s = tl;
		Vector e = tr;
		// char *buf = malloc(sizeof(char)*width*3);

		for (int y = 0; y < height; y++) {
			final Vector d = Vector.sDiv(Vector.sub(e, s), width);
			Vector v = s;
			// char *p = buf;
			for (int x = 0; x < width; x++) {

				final Vector r = Vector.norm(Vector.sub(v, eye));
				final Ray ray = new Ray(eye, r);
				Vector c = traceRay(ray, 0);

				final double m = Math.max(Math.max(c.getX(), c.getY()), c.getZ());
				if (m > 1.0) {
					c = Vector.sDiv(c, m);
				}

				out.write((byte) (c.getX() * 255.0));
				out.write((byte) (c.getY() * 255.0));
				out.write((byte) (c.getZ() * 255.0));

				v = Vector.add(v, d);
			}
			s = Vector.add(s, ld);
			e = Vector.add(e, rd);
			// fwrite(buf, 1, width * 3, OUT);
			// putc('.', stderr);
			System.out.print('.');
		}

		// free(buf);
	}

	public long getTotalrays() {
		return totalrays;
	}

	public long getShadowrays() {
		return shadowrays;
	}

	public long getReflectionrays() {
		return reflectionrays;
	}

	public long getRefractionrays() {
		return refractionrays;
	}

	public static void main(final String[] args) throws IOException {

		if (args.length != 2) {
			throw new IllegalArgumentException("usage: jray input output");
		}

		final RayToken tok = new RayToken(args[0]);

		final Vector eye = tok.nextVector();
		final Vector at = tok.nextVector();
		final Vector sky = tok.nextVector();

		final double fov = tok.nextDouble();
		final double d = tok.nextDouble();

		final int width = tok.nextInt();
		final int height = tok.nextInt();

		final int maxDepth = tok.nextInt();

		final Vector ambient = tok.nextVector();
		final Vector background = tok.nextVector();

		final RayTracer rt = new RayTracer(eye, at, sky, d, fov, width, height, maxDepth, ambient,
				background);

		int n = tok.nextInt();
		for (int i = 0; i < n; i++) {
			rt.addLight(tok.nextLight());
		}

		n = tok.nextInt();
		for (int i = 0; i < n; i++) {
			rt.addObject(tok.nextSphere());
		}

		// final Vector eye = new Vector(0.0, -3.0, -10.0);
		// final Vector at = new Vector(0.0, 0.0, 0.0);
		// final Vector sky = new Vector(0.0, -1.0, 0.0);
		//
		// final double fov = 50.0;
		// final double d = 1.0;
		//
		// final int width = 160;
		// final int height = 120;
		//
		// final int maxDepth = 25;
		//
		// final Vector ambient = new Vector(0.2, 0.2, 0.2);
		// final Vector background = new Vector(0.0, 0.0, 0.0);
		//
		// final RayTracer rt = new RayTracer(eye, at, sky, d, fov, width, height, maxDepth,
		// ambient,
		// background);
		//
		// rt.addLight(new Light(new Vector(-5.0, -5.0, 0.0), new Vector(1.0, 1.0, 1.0)));
		// rt.addLight(new Light(new Vector(5.0, -5.0, -5.0), new Vector(0.5, 0.5, 0.5)));
		// rt.addLight(new Light(new Vector(5.0, -5.0, 5.0), new Vector(0.8, 0.8, 0.8)));
		//
		// rt.addObject(new Sphere(new Vector(1.5, 0.5, -2.0), 0.5, new Vector(0.8, 0.8, 0.0),
		// new Vector(1.0, 1.0, 0.0), 0.1, 0.4, 0.8, 50.0, 0.8, 0.0, 1.0));
		// rt.addObject(new Sphere(new Vector(-3.0, 0.0, 0.0), 1.0, new Vector(1.0, 0.0, 0.0),
		// new Vector(1.0, 1.0, 1.0), 0.1, 0.5, 0.7, 60.0, 0.0, 0.0, 1.0));
		// rt.addObject(new Sphere(new Vector(1.0, 0.0, 3.0), 1.0, new Vector(0.0, 1.0, 0.0),
		// new Vector(1.0, 1.0, 1.0), 0.1, 0.5, 0.7, 60.0, 0.5, 0.0, 1.0));
		// rt.addObject(new Sphere(new Vector(-1.0, 0.0, -1.0), 1.0, new Vector(0.0, 0.0, 1.0),
		// new Vector(1.0, 1.0, 1.0), 0.0, 0.0, 1.0, 100.0, 0.5, 0.5, 2.4));
		// rt.addObject(new Sphere(new Vector(3.0, 0.0, 1.0), 1.0, new Vector(1.0, 1.0, 1.0),
		// new Vector(1.0, 1.0, 1.0), 0.0, 0.0, 1.0, 100.0, 1.0, 0.0, 1.0));
		// rt.addObject(new Sphere(new Vector(0.0, 200.0, 0.0), 199.0, new Vector(1.0, 1.0, 1.0),
		// new Vector(1.0, 1.0, 1.0), 0.1, 0.5, 0.5, 20.0, 0.6, 0.0, 1.0));

		final OutputStream out = new BufferedOutputStream(new FileOutputStream(args[1]));
		out.write(String.format("P6\n%d %d\n255\n", width, height).getBytes());

		final long start = System.currentTimeMillis();
		rt.rayTrace(out);
		final long end = System.currentTimeMillis();

		out.close();

		System.out.format("\nNumber of objects     : %d\n", rt.objectCount());
		System.out.format("Number of lights      : %d\n", rt.lightCount());
		System.out.format("Width                 : %d\n", width);
		System.out.format("Height                : %d\n", height);
		System.out.format("Max Depth             : %d\n", maxDepth);

		System.out.format("\nShadow rays cast      : %d\n", rt.getShadowrays());
		System.out.format("Reflection rays cast  : %d\n", rt.getReflectionrays());
		System.out.format("Refraction rays cast  : %d\n", rt.getRefractionrays());
		System.out.format("Total rays cast       : %d\n", rt.getTotalrays());
		System.out.format("Intersection tests    : %d\n", Sphere.getIntersections());

		System.out.format("Trace time            : %f seconds\n", (end - start) / 1000.0);

		// Number of objects : 6
		// Number of lights : 3
		// Width : 1600
		// Height : 1200
		// Max Depth : 25
		//
		// Shadow rays cast : 27832644
		// Reflection rays cast : 1899769
		// Refraction rays cast : 226160
		// Total rays cast : 4045929
		// Intersection tests : 49982289
		//
		// Trace time : 2.340000 seconds

	}
}
