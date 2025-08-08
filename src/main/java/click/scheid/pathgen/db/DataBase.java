package click.scheid.pathgen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import click.scheid.pathgen.types.Pair;
import click.scheid.pathgen.types.Path;

public class DataBase {

	private static final String tableName = "paths";
	private static final List<Pair<String, String>> columnDefinitions = List.of(
			new Pair<>("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
			new Pair<>("name", "VARCHAR(50)"),
			new Pair<>("player_uuid", "VARCHAR(50)"),
			new Pair<>("points", "TEXT"),
			new Pair<>("current_position", "INTEGER"),
			new Pair<>("status", "VARCHAR(20)")
	);
	
	private Set<String> columnNames;
	private List<String> columnTypes;
	
	private Connection con;
	
	public DataBase() {
		try {
			//init();
			connect();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void init() {
		columnNames = columnDefinitions.stream().map(c -> c.getFirst()).collect(Collectors.toSet());
		columnTypes = columnDefinitions.stream().map(c -> c.getSecond()).toList();
	}
	
	public Connection connect() throws SQLException {
		return con == null || con.isClosed() ? con = DriverManager.getConnection("jdbc:sqlite:pathgen.db") : con;
	}
	
	public void close() {
		try {
			if(con != null) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void createTable() {
		try (Statement stmt = connect().createStatement()){
			final String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "("
					+ columnDefinitions.stream()
									 .map(e -> e.getFirst() + " " + e.getSecond())
									 .collect(Collectors.joining(", "))
					+ ");";
			
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Path insert(Path path) {
		try (PreparedStatement stmt = connect().prepareStatement(path.getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
			
			path.setInsertValues(stmt);
			int rs = stmt.executeUpdate();
			
			ResultSet keys = stmt.getGeneratedKeys();
			if(keys.next()) {
				path.setId(keys.getInt(1));
			}
			
			return rs == 1 ? path : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean update(Path path) {
		try(PreparedStatement stmt = connect().prepareStatement(path.getUpdateSql())) {
			
			path.setUpdateValues(stmt);
			
			int rs = stmt.executeUpdate();
			
			return rs == 1;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Path select(Path path) {
		try(PreparedStatement stmt = connect().prepareStatement(path.getUniqueSelectSql())) {
			
			path.setSelectValues(stmt);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				return Path.createPath(rs);
			}
			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Path delete(Path path) {
		try(PreparedStatement stmt = connect().prepareStatement(path.getDeleteSql())) {
			path.setDeleteValues(stmt);
			
			int rs = stmt.executeUpdate();
			
			return rs == 1 ? path : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Path> query(String preparedSql, Function<ResultSet, Path> pathCreator, Object... updateValues) {
		List<Path> output = new ArrayList<>();
		try(PreparedStatement stmt = connect().prepareStatement(preparedSql)) {
			for(int i = 0; updateValues != null && i < updateValues.length; i++) {
				if(updateValues[i] == null) {
					continue;
				}
				stmt.setObject(i+1, updateValues[i]);
			}
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				output.add(pathCreator.apply(rs));
			}
			return output;
		} catch(SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public int updateQuery(String preparedSql, Object... updateValues) {
		List<Path> output = new ArrayList<>();
		try(PreparedStatement stmt = connect().prepareStatement(preparedSql)) {
			for(int i = 0; updateValues != null && i < updateValues.length; i++) {
				if(updateValues[i] == null) {
					continue;
				}
				stmt.setObject(i+1, updateValues[i]);
			}
			int rs = stmt.executeUpdate();
			
			return rs;
		} catch(SQLException ex) {
			return -1;
		}
	}
	
	public List<Path> query(String preparedSql, Object... updateValues) {
		return query(preparedSql, Path::createPath, updateValues);
	}
}
