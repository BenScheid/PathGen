package click.scheid.pathgen;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.utils.Utils;

public class UtilsTest {
	
	@Test
	public void test() {
		Coordinate c1 = new Coordinate(1.4d,2d,3.2d);
		Coordinate c2 = new Coordinate(1.7d,2d,3.1d);
		System.out.println(Utils.differentLocation(c1, c2));
	}
	
	public void fastSqrtTest() {
		double num = 1070342.0;
		System.out.println(Utils.fastSqrt(num));
		System.out.println(Math.sqrt(num));
	}
	
	public void distSqTest() {
		Coordinate from = new Coordinate(10.1, 69d, 10.1);
		Coordinate to = new Coordinate(10.2, 69d, 10.2);
		double dist = Utils.distanceSquared(from, to);
		System.out.println("----------distsq: " + dist);
		System.out.println("dist: " + Math.sqrt(dist));
	}
}
