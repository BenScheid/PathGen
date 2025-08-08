package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;

public class PathNewCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.async(() -> {
			String pathName = getPathName(args);
			Coordinate coords = parseCommand(args, PathGenPlugin.PLAYER_LOCATIONS.getLocation(player.getUniqueId()));
			Path path;
			
			Utils.log("new coords: " + coords);
			if(coords == null) {
				throw new CommandException(Utils.invalidArgs());
			}
			if (coords.areEmpty()) {
				path = new Path(pathName, id);
			} else {
				path = new Path(pathName, id, coords.getX(), coords.getZ());
			}
			path.save();
		
		});
		return true;
	}

	public Coordinate parseCommand(String[] args, Coordinate playerLoc) {
		if(args.length == 2) {
			return new Coordinate(null, null);
		}
		if(args.length == 4) {
			try {
				double x = Double.parseDouble(args[2]);
				double z = Double.parseDouble(args[3]);
				
				return new Coordinate(playerLoc.getWorldName(), x, z);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return null;
	}
	
	

}
