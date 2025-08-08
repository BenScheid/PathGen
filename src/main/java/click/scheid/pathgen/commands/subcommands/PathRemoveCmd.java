package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PathRemoveCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.runAsyncAndAccept(() -> {
			Integer index = parseCommand(args);
			if(index == null) {
				throw new CommandException(Utils.invalidArgs());
			}
			String pathName = getPathName(args);
			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if(path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			boolean result = path.remove(index);
			if(!result) {
				throw new CommandException(Utils.invalidCommand("The specified index isn't valid!"));
			}
			path.save();
			
			return index;
		}, index -> player.sendMessage(ChatColor.RED + "Successfully removed point at index " + index));
		
		return true;
	}
	
	public Integer parseCommand(String[] args) {
		if(args.length != 3) {
			return null;
		}
		try {
			int index = Integer.parseInt(args[2]) - 1;
			return index > 0 ? index : null;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

}
