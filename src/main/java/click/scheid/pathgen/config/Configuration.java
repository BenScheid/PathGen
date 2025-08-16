package click.scheid.pathgen.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Configuration {
	
	@JsonIgnore
	private String fileName;
	
	public Configuration(String fileName) {
		this.fileName = fileName;
	}
	
	@JsonIgnore
	String getPath() {
		return fileName;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [fileName=" + fileName + "]";
	}
}
