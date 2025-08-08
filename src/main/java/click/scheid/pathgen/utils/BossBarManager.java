package click.scheid.pathgen.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;

public class BossBarManager {
	
	public static final CopyOnWriteArrayList<Path> UPDATE_BOSS_BAR = new CopyOnWriteArrayList<>();
	private Map<UUID, BossBar> playerBossBars = new HashMap<>();
	
	public BossBar showBossBar(Path path) {
		BossBar bar = Bukkit.createBossBar(getBossBarTitle(path), BarColor.GREEN, BarStyle.SOLID);
		path.setBossbar(bar);
		bar.addPlayer(Utils.getPlayer(path.getPlayer()));
		bar.setProgress(path.getProgress());
		bar.setVisible(true);
		
		playerBossBars.put(path.getPlayer(), bar);
		UPDATE_BOSS_BAR.add(path);
		return bar;
	}
	
	public void hideBossBar(Path path) {
		UPDATE_BOSS_BAR.remove(path);
		BossBar bar = playerBossBars.remove(path.getPlayer());
		if(bar == null) {
			Utils.log("null bossbar");
			return;
		}
		bar.removePlayer(Utils.getPlayer(path.getPlayer()));
		bar.setVisible(false);
	}
	
	public void debug(Object... os) {
		if(!PathGenPlugin.DEBUG) {
			return;
		}
		Utils.log("object input: " + List.of(os));
		Utils.log("\n\n");
		Utils.log("update boss bar: " + UPDATE_BOSS_BAR);
		Utils.log("playerbossbar: "+playerBossBars);
	}
	
	public String getBossBarTitle(Path path) {
		try {
			Coordinate player = (Coordinate) PathGenPlugin.PLAYER_LOCATIONS.getLocation(path.getPlayer()).clone();
			Coordinate checkpoint = (Coordinate) path.getCheckpoint().clone();
			double distance = Utils.fastSqrt(Utils.distanceToSurfaceSquared(player, checkpoint));
			String direction = Utils.getCardinalDirection(player, checkpoint);
			String template = "Next checkpoint: %d,%d (%.1fm) | %s";
			return String.format(template, checkpoint.getIntX(), checkpoint.getIntZ(), distance, direction);
		} catch (CloneNotSupportedException ex) { 
			Utils.defaultErrorHandler(ex);
		}
		return "";
	}
	
	public void updateBossBar(Path path) {
		BossBar bar = playerBossBars.get(path.getPlayer());
		if(bar == null) {
			return;
		}
		bar.setProgress(path.getProgress());
		bar.setTitle(getBossBarTitle(path));
	}
}
