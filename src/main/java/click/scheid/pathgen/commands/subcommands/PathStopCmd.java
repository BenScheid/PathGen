package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;

public class PathStopCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();

		Utils.runAsyncAndAccept(() -> {
			if (args.length != 2) {
				throw new CommandException(Utils.invalidArgs());
			}
			String pathName = getPathName(args);

			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);

			if (path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			path.stop();
			PathGenPlugin.CHECKPOINTS.removeCheckPoint(id);
			
			path.save();
			return path;
		}, path -> {
			PathGenPlugin.BOSS_BARS.hideBossBar(path);
		});

		return true;
	}

}
