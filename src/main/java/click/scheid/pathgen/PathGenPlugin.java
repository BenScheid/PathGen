package click.scheid.pathgen;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import click.scheid.pathgen.commands.PathGenCommand;
import click.scheid.pathgen.config.DefaultPathGenConfig;
import click.scheid.pathgen.db.PathRepository;
import click.scheid.pathgen.events.PathGenEvents;
import click.scheid.pathgen.types.Coordinate;
import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.types.Path.PathStatus;
import click.scheid.pathgen.utils.BossBarManager;
import click.scheid.pathgen.utils.CheckPointReachedListener;
import click.scheid.pathgen.utils.LocationManager;
import click.scheid.pathgen.utils.Utils;

public class PathGenPlugin extends JavaPlugin {

	public static boolean DEBUG = true;
	
	public static DefaultPathGenConfig CONFIG;

	public static PathGenPlugin INSTANCE;
	public static PathRepository PATH_REPO;
	public static Gson SERIALIZER;
	public static LocationManager PLAYER_LOCATIONS;
	public static BossBarManager BOSS_BARS;
	public static CheckPointReachedListener CHECKPOINTS;

	public static int tick = 0;

	@Override
	public void onEnable() {
		getLogger().info("------------Enable pathgen");
		init();
		Bukkit.getPluginManager().registerEvents(new PathGenEvents(), INSTANCE);
		getCommand("pathgen").setExecutor(new PathGenCommand());

	}

	public void init() {
		INSTANCE = this;
		PathGenCommand.setCommand(getCommand("pathgen"));
		/*
		try {
			
		} catch (IOException e) {
			getLogger().severe("Couldnt read config! Shutting down!");
			getServer().shutdown();
			
		}
		*/
		PATH_REPO = new PathRepository();
		SERIALIZER = new Gson();
		PATH_REPO.createTable();
		PLAYER_LOCATIONS = new LocationManager();
		ConcurrentMap<UUID, Coordinate> checkpoints = PATH_REPO.loadByStatus(PathStatus.ACTIVE.getKey()).stream()
				.collect(Collectors.toConcurrentMap(Path::getPlayer, Path::getCheckpoint));
		CHECKPOINTS = new CheckPointReachedListener(Path.getCheckPointUpdater(), checkpoints);
		BOSS_BARS = new BossBarManager();
		// Bukkit.getScheduler().runTaskTimer(INSTANCE, () -> DEBUG = true, 0, 40);
		Utils.createDistanceUpdater();

	}

	@Override
	public void onDisable() {
		PATH_REPO.close();
		getLogger().info("--------------- Disable pathgen");
	}
}
