package click.scheid.pathgen.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;


public class ConfigLoader<T extends Configuration> {

	private final String filePath;
	private final Class<T> configClazz;
	private final ObjectMapper mapper;
	
	@SuppressWarnings("unchecked")
	public ConfigLoader(T config) {
		this.filePath = config.getPath();
		this.configClazz = (Class<T>) config.getClass();
		mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		try {
			init(config);
		} catch(IOException ioe) {
			throw new RuntimeException("Couldn't create configuration file!"); 
		}
	}

	public T load() throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			throw new FileNotFoundException(filePath + "doesn't exist!");
		}
		return mapper.readValue(file, configClazz);
	}
	
	public void init(Configuration config) throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			file.createNewFile();
			String yml = mapper.writeValueAsString(config);
			if(!file.canWrite()) {
				file.setWritable(true);
			}
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))){
				out.write(yml);
			}
		}
	}
}
