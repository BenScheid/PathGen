package click.scheid.pathgen;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.types.Coordinate;

public class POJOTest {
	
	@Test
	public void coordinateEqualsTest() {
		Coordinate c1 = new Coordinate(0d, 0d);
		Coordinate c2 = new Coordinate(0d, 0d);
		
		
		assertTrue(c1.equals(c2));
	}
}
