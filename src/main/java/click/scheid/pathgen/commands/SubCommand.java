package click.scheid.pathgen.commands;

public interface SubCommand extends Command {
	
	default String getPathName(String[] args) {
		return args[0];
	}
	
}
