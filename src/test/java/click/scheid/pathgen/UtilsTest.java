package click.scheid.pathgen;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.utils.TestUtils;
import click.scheid.pathgen.utils.Utils;

public class UtilsTest {

	@Test
	public void fastSqrtTest() {
		double[] inputs = { -3.72, -1, 0, 1, 2, 3, 5, 7.73, 9, 17, 8.6, 25, 154.2, 148218 };
		for (double d : inputs) {
			calcSqrt(d);
		}
	}

	public void calcSqrt(double sqrtOf) {
		if (sqrtOf >= 0) {
			double sqrt = Math.sqrt(sqrtOf);
			double fastSqrt = Utils.fastSqrt(sqrtOf);
			assertEquals(sqrt, fastSqrt, 0.01);
		} else {
			assertThrows(ArithmeticException.class, () -> Utils.fastSqrt(sqrtOf));
		}
	}

	@Test
	public void cardinalDirectionTest() {
		assertEquals("E", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, 0.0)));
		assertEquals("W", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, 0.0)));
		assertEquals("S", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 10.0)));
		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, -10.0)));

		assertEquals("NE", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, -10.0)));
		assertEquals("NW", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, -10.0)));
		assertEquals("SE", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, 10.0)));
		assertEquals("SW", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, 10.0)));

		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 0.0)));

		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, -1.0)));
		assertEquals("E", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(1.0, 0.0)));
		assertEquals("S", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 1.0)));
		assertEquals("W", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-1.0, 0.0)));
	}
	
	@Test
	public void clampTest() {
		final int[] values = {0,1,2,3,4,5,6,7,8,9};
		final int threshold = 5;
		
		for (int value : values) {
			TestUtils.assertGreaterEqualsThan(threshold, Utils.clampLower(threshold, value));
			TestUtils.assertSmallerEqualsThan(threshold, Utils.clampUpper(threshold, value));
		}
	}

}
