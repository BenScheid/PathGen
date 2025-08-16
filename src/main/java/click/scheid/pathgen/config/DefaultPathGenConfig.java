package click.scheid.pathgen.config;

public class DefaultPathGenConfig extends Configuration {
	
	public int checkPointDistance = 10;
	public int maxPathsPerPlayer = 5;
	
	public DefaultPathGenConfig() {
		this("pathgen.yml");
	}
	
	public DefaultPathGenConfig(String file) {
		super(file);
	}
}
