package click.scheid.pathgen.db;

import java.util.List;
import java.util.UUID;

import click.scheid.pathgen.types.Path;
import click.scheid.pathgen.types.Path.PathStatus;

public class PathRepository extends DataBase {
	
	
	public Path load(UUID playerId, String name) {
		final String sql = "SELECT * FROM paths WHERE player_uuid = ? AND name = ?;";
		List<Path> paths = super.query(sql, playerId.toString(), name);
		return paths.size() > 0 ? paths.get(0) : null;
	}
	
	public List<Path> loadByPlayerAndStatus(UUID playerId, String status) {
		final String sql = "SELECT * FROM paths WHERE player_uuid = ?" + (status == null ? ";" : " AND status = ?;");
		return super.query(sql, Path::createPath, playerId.toString(), status);
	}
	
	public Path loadActiveByPlayer(UUID id) {
		List<Path> path = loadByPlayerAndStatus(id, PathStatus.ACTIVE.getKey());
		return path.isEmpty() ? null : path.get(0);
	}
	
	public List<Path> loadByStatus(String status) {
		final String sql = "SELECT * FROM paths WHERE status = ?;";
		return super.query(sql, status);
	}
	
	public List<Path> load(UUID playerId) {
		final String sql = "SELECT * FROM paths WHERE player_uuid = ?;";
		return super.query(sql, playerId.toString());
	}
	
	public boolean nameExists(UUID id, String name) {
		return load(id, name) != null;
	}
}
