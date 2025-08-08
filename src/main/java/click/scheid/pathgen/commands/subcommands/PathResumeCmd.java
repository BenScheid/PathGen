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

public class PathResumeCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.runAsyncAndAccept(() -> {
			if(args.length != 2) {
				throw new CommandException(Utils.invalidArgs());
			}
			String pathName = getPathName(args);
			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if(path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			
			Coordinate coord = path.resume();
			
			if(coord == null) {
				throw new CommandException(ChatColor.YELLOW + "Cannot resume an unpaused path.");
			}
			
			PathGenPlugin.CHECKPOINTS.updateCheckpoint(id, coord);
			
			path.save();
			
			return path;
		}, path -> {
			PathGenPlugin.BOSS_BARS.showBossBar(path);
		});
		
		return true;
	}

	
}
