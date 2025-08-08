package click.scheid.pathgen.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.commands.debug.DeleteAllCmd;
import click.scheid.pathgen.commands.debug.SelectAllCmd;
import click.scheid.pathgen.commands.special.HelpCmd;
import click.scheid.pathgen.commands.special.ListAllCmd;
import click.scheid.pathgen.commands.subcommands.PathAddCmd;
import click.scheid.pathgen.commands.subcommands.PathDeleteCmd;
import click.scheid.pathgen.commands.subcommands.PathListCmd;
import click.scheid.pathgen.commands.subcommands.PathLoadCmd;
import click.scheid.pathgen.commands.subcommands.PathNewCmd;
import click.scheid.pathgen.commands.subcommands.PathPauseCmd;
import click.scheid.pathgen.commands.subcommands.PathRemoveCmd;
import click.scheid.pathgen.commands.subcommands.PathResumeCmd;
import click.scheid.pathgen.commands.subcommands.PathStartCmd;
import click.scheid.pathgen.commands.subcommands.PathStopCmd;
import click.scheid.pathgen.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PathGenCommand implements CommandExecutor, TabExecutor {
	
	public static PluginCommand COMMAND;
	private static final Map<String, SubCommand> subCommands = new HashMap<>();
	private static final Map<String, NoNameCommand> specialCommands = new HashMap<>();

	static {
		subCommands.put("new", new PathNewCmd());
		subCommands.put("add", new PathAddCmd());
		subCommands.put("remove", new PathRemoveCmd());
		subCommands.put("list", new PathListCmd());
		subCommands.put("delete", new PathDeleteCmd());
		subCommands.put("start", new PathStartCmd());
		subCommands.put("stop", new PathStopCmd());
		subCommands.put("pause", new PathPauseCmd());
		subCommands.put("resume", new PathResumeCmd());
		subCommands.put("load", new PathLoadCmd());
		
		specialCommands.put("list", new ListAllCmd());
		specialCommands.put("help", new HelpCmd());
		specialCommands.put("", new HelpCmd());
		specialCommands.put("delete", new DeleteAllCmd());
		specialCommands.put("select", new SelectAllCmd());
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		click.scheid.pathgen.commands.Command cmd;
		
		if(sender instanceof Player player) {
			String name = args.length != 0 ? args[0] : "";
			
			NoNameCommand nncmd;
			if((nncmd = isNoNameCommand(name)) != null) {
				cmd = nncmd;
				
			} else {
				if(args.length < 2) {
					return false;
				}
				String cmdStr = args[1];
				if(!isValidName(name)) {
					player.sendMessage(Utils.invalidCommand("Pathname cannot be \"" + name + "\"!"));
					return true;
				}
				
				SubCommand subCmd = subCommands.get(cmdStr.toLowerCase());
				
				if(subCmd == null) {
					player.sendMessage(Utils.invalidCommand("Invalid method!"));;
					return true;
				}
				cmd = subCmd;
			}
			
			try {
				return cmd.execute(player, args);
			} catch (Exception e) {
				Utils.defaultErrorHandler(e);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Only players can use this command.");
			return true;
		}
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return new ArrayList<>(0);
	}
	
	
	public static NoNameCommand isNoNameCommand(String name) {
		Set<String> invalidNames = specialCommands.keySet();
		boolean noName =  invalidNames.stream().anyMatch(invalid -> invalid.equalsIgnoreCase(name));
		return noName ? specialCommands.get(name) : null;
	}
	
	public static boolean isValidName(String name) {
		Set<String> invalidNames = subCommands.keySet();
		return !invalidNames.stream().anyMatch(invalid -> invalid.equalsIgnoreCase(name));
	}
	
	public static void setCommand(PluginCommand cmd) {
		COMMAND = cmd;
	}
}
