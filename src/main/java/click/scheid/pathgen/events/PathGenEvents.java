package click.scheid.pathgen.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.types.Path.PathStatus;
import click.scheid.pathgen.utils.Utils;

public class PathGenEvents implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		Player p = evt.getPlayer();
		UUID id = p.getUniqueId();
		PathGenPlugin.PLAYER_LOCATIONS.update(p.getUniqueId(), Utils.locationToCoord(p.getLocation()));
		
		Utils.runAsyncAndAccept(() -> {
			List<Path> paths = PathGenPlugin.PATH_REPO.loadByPlayerAndStatus(id, PathStatus.ACTIVE.getKey());
			if(paths.size() > 1) {
				throw new RuntimeException("More than 1 active path;");
			}
			return paths;
		}, paths -> {
			if(paths.size() == 1) {
				Utils.log("player join path: " + paths.get(0));
				PathGenPlugin.BOSS_BARS.showBossBar(paths.get(0));
			}
		});
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent evt) {
		// TODO remove bossbars, etc
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		
		Coordinate oldLoc = Utils.locationToCoord(evt.getFrom());
		Coordinate newLoc = Utils.locationToCoord(evt.getTo());

		if(Utils.differentLocation(oldLoc, newLoc)) {
			PathGenPlugin.PLAYER_LOCATIONS.update(evt.getPlayer().getUniqueId(), newLoc);
			PathGenPlugin.CHECKPOINTS.playerMove(evt.getPlayer().getUniqueId(), newLoc);
		}
	}
}
