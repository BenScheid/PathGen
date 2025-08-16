package click.scheid.pathgen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.config.ConfigLoader;
import click.scheid.pathgen.config.DefaultPathGenConfig;

public class ConfigTest {

	
	@Test
	public void configFileLoaderTest() throws IOException {
		File file = new File("configtest.yml");
		DefaultPathGenConfig config = new ConfigLoader<>(new DefaultPathGenConfig("configtest.yml")).load();
		if(file.exists()) {
			configFileExistsTest(config);
		} else {
			configFileDoesntExistTest(config);
		}
	}
	
	public void configFileExistsTest(DefaultPathGenConfig config) throws IOException {
		assertEquals(8, config.checkPointDistance);
		assertEquals(7, config.maxPathsPerPlayer);
	}
	
	
	public void configFileDoesntExistTest(DefaultPathGenConfig config) throws IOException {
		assertEquals(10, config.checkPointDistance);
		assertEquals(5, config.maxPathsPerPlayer);
	}
}
