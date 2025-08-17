package click.scheid.pathgen.config;

import click.scheid.pathgen.utils.Utils;

public class DefaultPathGenConfig extends Configuration {
	
	public int checkPointDistance = 10;
	public int maxPathsPerPlayer = 5;
	public int maxThreads = Utils.clampLower(1, Runtime.getRuntime().availableProcessors()/2);
	
	public DefaultPathGenConfig() {
		this("pathgen.yml");
	}
	
	public DefaultPathGenConfig(String file) {
		super(file);
	}
}
