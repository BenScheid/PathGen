package click.scheid.pathgen.commands.special;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.NoNameCommand;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.utils.Utils;

public class ListAllCmd implements NoNameCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		UUID playerId = player.getUniqueId();
		Utils.runAsyncAndAccept(() -> {
			if(args.length > 2) {
				throw new CommandException("Invalid Arguments!");
			}
			List<Path> paths = PathGenPlugin.PATH_REPO.loadByPlayerAndStatus(playerId, args.length == 2 ? args[1] : null);
			
			StringBuilder message = new StringBuilder("Your current paths:");
			paths.forEach(p -> {
				message.append("\n    "+p.getName() + " ["+p.getStatus().getKey().toUpperCase()+"]");
			});
			return message.toString();
		}, msg -> player.sendMessage(msg));
		
		
		
		
		return true;
	}
	
}
