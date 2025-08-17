package click.scheid.pathgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import click.scheid.pathgen.config.ConfigLoader;
import click.scheid.pathgen.config.DefaultPathGenConfig;

public class ConfigTest {

	
	@Test
	public void configFileLoaderTest() throws IOException {
		File file = new File("configtest.yml");
		if(file.exists()) {
			DefaultPathGenConfig config = new ConfigLoader<>(new DefaultPathGenConfig("configtest.yml")).load();
			configFileExistsTest(config);
		} else {
			DefaultPathGenConfig config = new ConfigLoader<>(new DefaultPathGenConfig("configtest.yml")).load();
			configFileDoesntExistTest(config);
		}
		
		file.delete();
	}
	
	public void configFileExistsTest(DefaultPathGenConfig config) throws IOException {
		assertNotNull(config.checkPointDistance);
		assertNotNull(config.maxPathsPerPlayer);
	}
	
	
	public void configFileDoesntExistTest(DefaultPathGenConfig config) throws IOException {
		assertEquals(10, config.checkPointDistance);
		assertEquals(5, config.maxPathsPerPlayer);
	}
}
