package click.scheid.pathgen.commands;

import org.bukkit.entity.Player;

interface Command {

	boolean execute(Player player, String[] args);
	
	default String getUsage() {
		return "/pathgen [path_name] [method] [args]";
	}
	
}
