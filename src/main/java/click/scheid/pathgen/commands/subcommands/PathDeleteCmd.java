package click.scheid.pathgen.commands.subcommands;

import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.SubCommand;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PathDeleteCmd implements SubCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID id = player.getUniqueId();
		
		Utils.runAsyncAndAccept(() -> {
			if(args.length > 2) {
				throw new CommandException(Utils.invalidArgs());
			}
			String pathName = getPathName(args);
			
			Path path = PathGenPlugin.PATH_REPO.load(id, pathName);
			if(path == null) {
				throw new CommandException(Utils.invalidName(pathName));
			}
			path.delete();
			return path;
		}, path -> {
			player.sendMessage(ChatColor.RED + "Deleted path: " + path.getName());
			PathGenPlugin.BOSS_BARS.hideBossBar(path);
		});
		
		
		
		// TODO remove bossbar if active ...
		
		return true;
	}

}
