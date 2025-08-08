package click.scheid.pathgen.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;


public class ConfigLoader {

	private final String filePath;
	private final Configuration config;
	private final ObjectMapper mapper;
	
	public ConfigLoader(Configuration config) {
		this.filePath = config.getPath();
		this.config = config;
		mapper = new ObjectMapper(new YAMLFactory());
	}

	public void load() throws IOException {
		
		File file = new File(filePath);
		if(!file.exists()) {
			throw new FileNotFoundException(filePath + "doesn't exist!");
		}
	}
	
	public void init() throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			file.createNewFile();
		}
		String yml = mapper.writeValueAsString(config);
		if(!file.canWrite()) {
			file.setWritable(true);
		}
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		out.pr
	}
}
