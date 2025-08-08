package click.scheid.pathgen.commands.subcommands;

import static click.scheid.pathgen.types.Path.PathStatus.ACTIVE;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;

public class PathStartCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();

		Utils.runAsyncAndAccept(() -> {
			if (args.length != 2) {
				throw new CommandException(Utils.invalidArgs());
			}
			String pathName = getPathName(args);

			boolean hasActivePath = PathGenPlugin.PATH_REPO.loadByPlayerAndStatus(id, ACTIVE.getKey()).size() > 0;
			if (hasActivePath) {
				throw new CommandException(ChatColor.RED + "You already have another active path! Not starting!");
			}

			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if (path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			if (path.isStatus(ACTIVE)) {
				throw new CommandException(ChatColor.YELLOW + "This path is already active!");
			}

			Coordinate coord = path.start();
			path.save();
			
			if(coord == null) {
				throw new CommandException(ChatColor.RED + "This path is empty. Cannot start!");
			}
			
			PathGenPlugin.CHECKPOINTS.updateCheckpoint(id, coord);
			
			return path;
		}, path -> {
			if(path != null) {
				PathGenPlugin.BOSS_BARS.showBossBar(path);
			}
		});

		return true;
	}

}
