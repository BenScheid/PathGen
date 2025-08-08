package click.scheid.pathgen.types;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.boss.BossBar;

import com.google.gson.reflect.TypeToken;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.utils.BossBarManager;
import click.scheid.pathgen.utils.Utils;

public class Path {
	
	private static final Type COORDS_LIST_TYPE = new TypeToken<List<Coordinate>>() {}.getType();
	
	
	
	private int id;
	private String name;
	private UUID player;
	private List<Coordinate> allPoints = new LinkedList<>();
	private int currentPosition;
	private PathStatus status = PathStatus.INACTIVE;
	
	private BossBar bossbar;
	
	public Path() {}
	
	public Path(int id) {
		this.id = id;
	}

	public Path(String name, UUID player) {
		this.name = name;
		this.player = player;
	}
	
	public Path(String name, UUID player, double startX, double startZ) {
		this.name = name;
		this.player = player;
		allPoints.add(new Coordinate(startX, startZ));
	}
	
	public boolean hasNext() {
		return currentPosition + 1 < allPoints.size();
	}
	
	public Coordinate next() {
		return allPoints.get(++currentPosition);
	}
	
	public Coordinate start() {
		if(allPoints.isEmpty()) {
			return null;
		}
		status = PathStatus.ACTIVE;
		currentPosition = 0;
		return allPoints.get(currentPosition);
	}
	
	public void pause() {
		status = PathStatus.PAUSED;
	}
	
	public Coordinate resume() {
		if(status != PathStatus.PAUSED) {
			return null;
		}
		status = PathStatus.ACTIVE;
		return allPoints.get(currentPosition);
	}
	
	public void stop() {
		status = PathStatus.INACTIVE;
		currentPosition = -1;
	}
	
	public int addPoint(Coordinate point) {
		allPoints.add(point);
		return allPoints.size() - 1;
	}
	
	public void addPoint(Coordinate point, int index) {
		 allPoints.add(index, point);
	}
	
	public boolean remove(int index) {
		if(index < 0 || index >= allPoints.size()) {
			return false;
		}
		allPoints.remove(index);
		return true;
	}
	
	public Path delete() {
		status = PathStatus.INACTIVE;
		return PathGenPlugin.PATH_REPO.delete(this);
	}
	
	public void save() {
		boolean exists = PathGenPlugin.PATH_REPO.nameExists(player, name);
		if(exists) {
			PathGenPlugin.PATH_REPO.update(this);
		} else {
			PathGenPlugin.PATH_REPO.insert(this);
		}
	}
	
	public boolean isActive() {
		return status.equals(PathStatus.ACTIVE);
	}
	
	public float getProgress() {
		return (float) currentPosition / (allPoints.size()-1);
	}
	
	public Coordinate getCheckpoint() {
		return allPoints.get(currentPosition);
	}
	
	public Coordinate[] toArray(boolean remainingOnly) {
		if(remainingOnly) {
			return allPoints.subList(currentPosition >= 0 ? currentPosition : 0, allPoints.size()).toArray(Coordinate[]::new);
		} else {
			return allPoints.toArray(Coordinate[]::new);
		}
	}
	
	
	public String getUniqueValue() {
		return id + "";
	}
	
	
	
	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwner(UUID owner) {
		this.player = owner;
	}

	public void setAllPoints(List<Coordinate> allPoints) {
		this.allPoints = allPoints;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public void setStatus(PathStatus status) {
		this.status = status;
	}
	
	public void setPlayer(UUID player) {
		this.player = player;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public BossBar getBossbar() {
		return bossbar;
	}

	public void setBossbar(BossBar bossbar) {
		this.bossbar = bossbar;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other instanceof Path path) {
			return Objects.equals(this.name, path.name) && 
				   Objects.equals(this.player, path.player);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, player);
	}
	
	@Override
	public String toString() {
		return "Path [id=" + id + ", name=" + name + ", player=" + player + ", allPoints=" + allPoints
				+ ", currentPosition=" + currentPosition + ", status=" + status + "]";
	}

	public String getInsertSql() {
	    return "INSERT INTO paths (name, player_uuid, points, current_position, status) VALUES (?, ?, ?, ?, ?)";
	}

	public void setInsertValues(PreparedStatement stmt) throws SQLException {
	    stmt.setString(1, name);
	    stmt.setString(2, player.toString());
	    stmt.setString(3, pointsToJson());    
	    stmt.setInt(4, currentPosition);
	    stmt.setString(5, status.getKey());
	}

	public String getUniqueSelectSql() {
	    return "SELECT * FROM paths WHERE id = ?";
	}

	public void setSelectValues(PreparedStatement stmt) throws SQLException {
	    stmt.setInt(1, id);
	}
	
	public String getUpdateSql() {
	    return "UPDATE paths SET name = ?, player_uuid = ?, points = ?, current_position = ?, status = ? WHERE id = ?";
	}

	public void setUpdateValues(PreparedStatement stmt) throws SQLException {
	    stmt.setString(1, name);
	    stmt.setString(2, player.toString());
	    stmt.setString(3, pointsToJson());
	    stmt.setInt(4, currentPosition);
	    stmt.setString(5, status.getKey());
	    
	    stmt.setInt(6, id);
	}

	public String getDeleteSql() {
	    return "DELETE FROM paths WHERE id = ?";
	}

	public void setDeleteValues(PreparedStatement stmt) throws SQLException {
	    stmt.setInt(1, id);
	}
	
	public String pointsToJson() {
		return PathGenPlugin.SERIALIZER.toJson(allPoints);
	}
	
	public void pointsFromJson(String json) {
		allPoints = PathGenPlugin.SERIALIZER.fromJson(json, COORDS_LIST_TYPE);
	}
	
	public String getName() {
		return name;
	}

	public PathStatus getStatus() {
		return status;
	}
	
	public boolean isStatus(PathStatus required) {
		return status.equals(required);
	}
	
	public static Path createPath(ResultSet rs) {
		Path path = new Path();
		try {
			path.setId(rs.getInt("id"));
			path.setName(rs.getString("name"));
			path.setPlayer(UUID.fromString(rs.getString("player_uuid")));
			path.pointsFromJson(rs.getString("points"));
			path.setCurrentPosition(rs.getInt("current_position"));
			path.setStatus(PathStatus.byKey(rs.getString("status")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(path);
		return path;
	}
	
	public static Path createPathStatus(ResultSet rs) {
		Path path = new Path();
		try {
			path.setName(rs.getString("name"));
			path.setStatus(PathStatus.byKey(rs.getString("status")));
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		}
		return path;
	}
	
	public static BiConsumer<UUID, Coordinate> getCheckPointUpdater(){
		return (id, coord) -> {
			Utils.runAsyncAndAccept(() -> {
				Path path = PathGenPlugin.PATH_REPO.loadActiveByPlayer(id);
				if(path.hasNext()) {
					Coordinate newCp = path.next();
					PathGenPlugin.CHECKPOINTS.updateCheckpoint(id, newCp);
					BossBarManager.UPDATE_BOSS_BAR.remove(path);
					BossBarManager.UPDATE_BOSS_BAR.add(path);
					path.save();
				}
				return path;
			}, path -> {
				PathGenPlugin.BOSS_BARS.updateBossBar(path);
			});
		};
	}



	public static enum PathStatus {
		ACTIVE("active"), INACTIVE("inactive"), PAUSED("paused");
		
		private String key;
		
		private PathStatus(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		public static PathStatus byKey(String key) {
			Objects.requireNonNull(key);
			for(PathStatus status : values()) {
				if(key.equalsIgnoreCase(status.getKey())) {
					return status;
				}
			}
			return null;
		}
	}
}
