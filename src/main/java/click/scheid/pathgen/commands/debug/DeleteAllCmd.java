package click.scheid.pathgen.commands.debug;

import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.NoNameCommand;

public class DeleteAllCmd implements NoNameCommand{

	@Override
	public boolean execute(Player player, String[] args) {
		 return PathGenPlugin.PATH_REPO.updateQuery("DELETE FROM paths;") >= 0;
	}

}
