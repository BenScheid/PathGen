package click.scheid.pathgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.types.Coordinate;
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
		// East
		assertEquals("E", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, 0.0)));
		// West
		assertEquals("W", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, 0.0)));
		// South
		assertEquals("S", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 10.0)));
		// North
		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, -10.0)));

		// Diagonals
		assertEquals("NE", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, -10.0)));
		assertEquals("NW", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, -10.0)));
		assertEquals("SE", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(10.0, 10.0)));
		assertEquals("SW", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-10.0, 10.0)));

		// Edge case: same coordinate
		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 0.0)));

		// Single-step axis-aligned
		assertEquals("N", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, -1.0)));
		assertEquals("E", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(1.0, 0.0)));
		assertEquals("S", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(0.0, 1.0)));
		assertEquals("W", Utils.getCardinalDirection(new Coordinate(0.0, 0.0), new Coordinate(-1.0, 0.0)));
	}

}
