package org.plump.ray;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RayTracer {

	private Vector3 background;
	private Vector3 ambientLight;
	private List<Sphere> objects = new ArrayList<Sphere>();
	private List<Light> lights = new ArrayList<Light>();
	private Vector3 eye;
	private Vector3 at;
	private Vector3 sky;
	private double dis;
	private double fov;
	private int width;
	private int height;
	private int maxDepth;
	private long totalrays;
	private long shadowrays;
	private long reflectionrays;
	private long refractionrays;

	public RayTracer(final Vector3 eye, final Vector3 at, final Vector3 sky, final double dis,
			final double fov, final int width, final int height, final int maxDepth,
			final Vector3 ambientLight, final Vector3 background) {
		super();
		this.background = background;
		this.ambientLight = ambientLight;
		this.eye = eye;
		this.at = at;
		this.sky = Vector3.norm(sky);
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

	public Vector3 traceRay(final Ray ray, final int depth) {

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

		final Ray r = new Ray(Vector3.add(ray.getBase(), Vector3.sMul(ray.getDir(), mt)), ray
				.getDir());
		final Vector3 normal = Vector3.norm(Vector3.sub(r.getBase(), o.getCenter()));

		return shade(o, r, normal, depth + 1);
	}

	private Vector3 shade(final Sphere object, final Ray ray, final Vector3 normal, final int depth) {

		// return object.getDcolor();
		Vector3 intensity = Vector3
				.mul(Vector3.sMul(object.getDcolor(), object.getKa()), ambientLight);

		for (final Light light : lights) {
			final Ray shad = new Ray(ray.getBase(), Vector3.norm(Vector3.sub(light.getPosition(), ray
					.getBase())));
			final double nl = Vector3.dot(normal, shad.getDir());
			if (nl > 0.0) {
				Vector3 inter = light.getColor();

				for (final Sphere s : objects) {
					shadowrays++;
					final double t = s.intersect(shad);
					if (t > 0.0) {
						if (s.getTransmit() > 0.0) {
							inter = Vector3.add(Vector3.sMul(inter, s.getTransmit()), Vector3.sMul(s
									.getDcolor(), 1.0 - s.getTransmit()));
						} else {
							inter = new Vector3(0.0, 0.0, 0.0);
							break;
						}
					}
				}

				intensity = Vector3.add(intensity, Vector3.sMul(
						Vector3.mul(inter, object.getDcolor()), object.getKd() * nl));

				if (object.getKs() > 0.0) {
					final Vector3 reflect = Vector3.norm(Vector3.sub(Vector3.sMul(normal, 2.0 * nl),
							shad.getDir()));
					final Vector3 view = Vector3.norm(Vector3.sub(eye, ray.getBase()));
					intensity = Vector3.add(intensity, Vector3.sMul(Vector3.mul(inter, object
							.getScolor()), object.getKs()
							* Math.pow(Vector3.dot(reflect, view), object.getN())));
				}
			}
		}

		if (depth < maxDepth) {

			if (object.getReflect() > 0.0) {
				reflectionrays++;
				final double nl = Vector3.dot(normal, ray.getDir());
				final Ray reflect = new Ray(ray.getBase(), Vector3.norm(Vector3.sub(ray.getDir(),
						Vector3.sMul(normal, 2.0 * nl))), object);
				intensity = Vector3.add(intensity, Vector3.sMul(traceRay(reflect, depth + 1), object
						.getKs()
						* object.getReflect()));
			}

			if (object.getTransmit() > 0.0) {
				refractionrays++;
				if (object.getIor() > 0.0) {
					final double n = 1.0 / object.getIor();
					final double c = Vector3.dot(Vector3.negate(ray.getDir()), normal);
					double nl = 1.0 - n * n * (1.0 - c * c);
					if (nl >= 0.0) {
						nl = n * c - Math.sqrt(nl);
						final Ray refract = new Ray(ray.getBase(), Vector3.norm(Vector3.sub(Vector3
								.sMul(normal, nl), Vector3.sMul(Vector3.negate(ray.getDir()), n))),
								object);
						intensity = Vector3.add(intensity, Vector3.sMul(traceRay(refract, depth + 1),
								object.getKs() * object.getTransmit()));
					}
				} else {
					final Ray refract = new Ray(ray.getBase(), ray.getDir(), object);
					intensity = Vector3.add(intensity, Vector3.sMul(traceRay(refract, depth + 1),
							object.getKs() * object.getTransmit()));
				}
			}
		}

		return intensity;
	}

	public void rayTrace(final OutputStream out) throws IOException {

		final Vector3 dir = Vector3.norm(Vector3.sub(at, eye));
		final Vector3 right = Vector3.norm(Vector3.cross(dir, sky));
		final Vector3 up = Vector3.norm(Vector3.cross(right, dir));

		final double w = dis * Math.tan(fov / 2.0 * Math.PI / 180.0);
		final double h = w * height / width;

		at = Vector3.add(eye, dir);
		final Vector3 ww = Vector3.sMul(right, w);
		final Vector3 hh = Vector3.sMul(up, h);
		final Vector3 tl = Vector3.add(Vector3.sub(at, ww), hh);
		final Vector3 bl = Vector3.sub(Vector3.sub(at, ww), hh);
		final Vector3 br = Vector3.sub(Vector3.add(at, ww), hh);
		final Vector3 tr = Vector3.add(Vector3.add(at, ww), hh);

		final Vector3 ld = Vector3.sDiv(Vector3.sub(bl, tl), height);
		final Vector3 rd = Vector3.sDiv(Vector3.sub(br, tr), height);

		Vector3 s = tl;
		Vector3 e = tr;
		// char *buf = malloc(sizeof(char)*width*3);

		for (int y = 0; y < height; y++) {
			final Vector3 d = Vector3.sDiv(Vector3.sub(e, s), width);
			Vector3 v = s;
			// char *p = buf;
			for (int x = 0; x < width; x++) {

				final Vector3 r = Vector3.norm(Vector3.sub(v, eye));
				final Ray ray = new Ray(eye, r);
				Vector3 c = traceRay(ray, 0);

				final double m = Math.max(Math.max(c.getX(), c.getY()), c.getZ());
				if (m > 1.0) {
					c = Vector3.sDiv(c, m);
				}

				out.write((byte) (c.getX() * 255.0));
				out.write((byte) (c.getY() * 255.0));
				out.write((byte) (c.getZ() * 255.0));

				v = Vector3.add(v, d);
			}
			s = Vector3.add(s, ld);
			e = Vector3.add(e, rd);
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

		final Vector3 eye = tok.nextVector();
		final Vector3 at = tok.nextVector();
		final Vector3 sky = tok.nextVector();

		final double fov = tok.nextDouble();
		final double d = tok.nextDouble();

		final int width = tok.nextInt();
		final int height = tok.nextInt();

		final int maxDepth = tok.nextInt();

		final Vector3 ambient = tok.nextVector();
		final Vector3 background = tok.nextVector();

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
