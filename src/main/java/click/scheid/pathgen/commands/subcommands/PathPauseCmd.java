package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;

public class PathPauseCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.<Path>runAsyncAndAccept(() -> {
			if(args.length != 2) {
				throw new CommandException("Invalid Args.");
			}
			String pathName = getPathName(args);
			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if(path == null) {
				throw new CommandException("You don't have a path with the following name: " + pathName + " !");
			}

			PathGenPlugin.CHECKPOINTS.removeCheckPoint(id);
			
			path.pause();
			
			path.save();
			
			return path;
		}, path -> {
			PathGenPlugin.BOSS_BARS.hideBossBar(path);
		}, ex -> {
			Utils.playerErrorHandler(ex, player);
			return null;
		});
		
		return true;
	}

}
