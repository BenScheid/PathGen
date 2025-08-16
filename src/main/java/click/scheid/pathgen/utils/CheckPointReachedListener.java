package click.scheid.pathgen.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.types.Coordinate;

public class CheckPointReachedListener {

	private final ConcurrentMap<UUID, Coordinate> playerCheckpoints = new ConcurrentHashMap<>();
	private final BiConsumer<UUID, Coordinate> callback;
	private final int triggerTresholdExp2;
	
	
	public CheckPointReachedListener(BiConsumer<UUID, Coordinate> callback, ConcurrentMap<UUID, Coordinate> cps) {
		triggerTresholdExp2 = (int) Math.pow(PathGenPlugin.CONFIG.checkPointDistance, 2);
		this.callback = callback;
		this.playerCheckpoints.putAll(cps);
	}
	
	public void playerMove(UUID id, Coordinate newLoc) {
		Coordinate checkPoint = playerCheckpoints.get(id);
		if(checkPoint == null) {
			return;
		}
		if(Utils.distanceToSurfaceSquared(newLoc, checkPoint) <= triggerTresholdExp2) {
			Utils.sync(() -> {
				callback.accept(id, checkPoint);
			});
		}
	}
	
	public void updateCheckpoint(UUID id, Coordinate newCp) {
		playerCheckpoints.put(id, newCp);
	}
	
	public void removeCheckPoint(UUID id) {
		playerCheckpoints.remove(id);
	}
	
}
