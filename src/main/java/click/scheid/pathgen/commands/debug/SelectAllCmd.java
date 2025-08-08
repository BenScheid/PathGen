package click.scheid.pathgen.commands.debug;

import java.util.List;

import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.NoNameCommand;
import click.scheid.pathgen.types.Path;
import net.md_5.bungee.api.ChatColor;

public class SelectAllCmd implements NoNameCommand {

	@Override
	public boolean execute(Player player, String[] args) {
		List<Path> paths = PathGenPlugin.PATH_REPO.query("SELECT * FROM paths;");
		
		StringBuilder message = new StringBuilder();
		
		
		paths.forEach(p -> {
			message.append(p.toString());
		});
		
		player.sendMessage(ChatColor.BLUE + message.toString());
		return true;
	}

}
