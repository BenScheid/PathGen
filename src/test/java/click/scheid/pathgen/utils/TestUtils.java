package click.scheid.pathgen.utils;

import static org.junit.jupiter.api.Assertions.*;


public final class TestUtils {

	private TestUtils() {}
	
	
	public static <T extends Comparable<T>> void assertGreaterThan(T threshold, T value) {
		assertTrue(value.compareTo(threshold) > 0);
	}
	
	public static <T extends Comparable<T>> void assertGreaterEqualsThan(T threshold, T value) {
		assertTrue(value.compareTo(threshold) >= 0);
	}
	
	public static <T extends Comparable<T>> void assertSmallerThan(T threshold, T value) {
		assertTrue(value.compareTo(threshold) < 0);
	}
	
	public static <T extends Comparable<T>> void assertSmallerEqualsThan(T threshold, T value) {
		assertTrue(value.compareTo(threshold) <= 0);
	}
}
