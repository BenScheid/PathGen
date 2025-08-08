 package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PathListCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.runAsyncAndAccept(() -> {
			if(args.length > 3) {
				throw new CommandException(Utils.invalidArgs());
			}
			boolean remainingOnly = false;
			if(args.length == 3) {
				switch(args[2].toLowerCase()) {
					case "":
					case "all": remainingOnly = false;
						break;
					case "remaining": remainingOnly = true;
						break;
					default: throw new CommandException(Utils.invalidArgs());
				}
			}
			String pathName = getPathName(args);
			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if(path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			Coordinate[] points = path.toArray(remainingOnly);
			
			String message = "" + ChatColor.YELLOW;
			if(remainingOnly) {
				message += "Remaining points";
			} else {
				message += "All points";
			}
			message += " (" + pathName + "):";
			
			
			for(int i = 0; i < points.length; i++) {
				Coordinate coord = points[i];
				String direction = "";
				if(i != 0) {
					Coordinate previous = points[i-1];
					direction = Utils.getCardinalDirection(coord, previous);
				}
				String toFormat = "    %d: [%d, %d]" + (!direction.isBlank() ?  " (%s)" : "");
				String formatted = "\n" + String.format(toFormat, i+1, coord.getIntX(), coord.getIntZ(), !direction.isBlank() ? direction : null);
				message += formatted;
			}
			return message;
		}, msg -> player.sendMessage(msg));

		return true;
	}
}