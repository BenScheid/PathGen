package click.scheid.pathgen.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import click.scheid.pathgen.types.Coordinate;

public class LocationManager {

	private Map<UUID, Coordinate> playerLocations = new ConcurrentHashMap<>();
	
	
	public void update(UUID id, Coordinate loc) {
		playerLocations.put(id, loc);
	}
	
	public void remove(UUID id) {
		playerLocations.remove(id);
	}
	
	public Coordinate getLocation(UUID id) {
		return playerLocations.get(id);
	}
	
}
