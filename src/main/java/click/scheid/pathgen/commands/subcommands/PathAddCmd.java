package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Pair;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PathAddCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID playerId = player.getUniqueId();
		String worldName = player.getWorld().getName();
		Utils.runAsyncAndAccept(() -> {
			Pair<Coordinate, Integer> command = parseCommand(args, playerId, worldName);
			if(command == null) {
				throw new CommandException(Utils.invalidArgs());
			}
			Coordinate coords = command.getFirst();
			int index = command.getSecond();
			
			String pathName = getPathName(args);
			Path path = PathGenPlugin.PATH_REPO.load(playerId, pathName);
			if(path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			
			if(index < 0) {
				index = path.addPoint(coords) + 1;
			} else {
				path.addPoint(coords, index-1);
			}
			path.save();
			
			return command;
		}, data -> {
			Coordinate coords = data.getFirst();
			player.sendMessage(ChatColor.GREEN + "Successfully added point ["+ coords.getIntX()+ "," + coords.getIntZ() +"] at index " + data.getSecond());
		});
		
		return true;
	}
	
	public Pair<Coordinate, Integer> parseCommand(String[] args, UUID playerId, String worldName) {
		//args index: 0     1   2   3     4
		// /pathgen <name> add [x] [z] [index]
		// args.length == 2 => no coords and no index
		// args.length == 3 || 5 => no coords, but index
		// args.length == 4 => coords, but no index
		// args.length == 5 => coords and index
		
		try {
			if (args.length > 5) {
				return null;
			}
			int index = -1;
			Coordinate coords = PathGenPlugin.PLAYER_LOCATIONS.getLocation(playerId);
			if(args.length % 2 != 0) {
				index = Integer.parseInt(args[args.length-1]);
			}
			
			if(args.length >= 4) {
				double x = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				coords = new Coordinate(worldName, x, z);
			} 
			return new Pair<>(coords, index);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

}
