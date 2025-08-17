package click.scheid.pathgen.types;

import java.util.Objects;

import javax.print.attribute.standard.MediaSize.Other;

public class Coordinate implements Cloneable{
	
	private String worldName;
	private Double x;
	private Double y;
	private Double z;
	
	
	
	public Coordinate(String worldName, Double x, Double y, Double z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coordinate(Double x, Double y, Double z) {
		this("", x, y, z);
	}
	
	public Coordinate(Double x, Double z) {
		this(x, null, z);
	}
	
	public Coordinate(String worldName, Double x, Double z) {
		this(worldName, x, null, z);
	}

	public boolean is2D() {
		return y == null && x != null && z != null;
	}
	
	public boolean areEmpty() {
		return x == null && y == null && z == null;
	}
	
	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Integer getIntX() {
		return (int) Math.floor(x);
	}
	
	public Integer getIntY() {
		return (int) Math.floor(y);
	}
	
	public Integer getIntZ() {
		return (int) Math.floor(z);
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getZ() {
		return z;
	}

	public void setZ(Double z) {
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}
		return o instanceof Coordinate other 
				&& Objects.equals(worldName, other.worldName)
				&& Objects.equals(x, other.x)
				&& Objects.equals(y, other.y)
				&& Objects.equals(z, other.z);
	}	
	
	@Override
	public int hashCode() {
		return Objects.hash(worldName, x, y, z);
	}
	
	@Override
	public String toString() {
		//return "(" + x + ", " + y + ", " + z + ")    ";
		return "Coordinate [worldName=" + worldName + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Coordinate(worldName, x, y, z);
		
	}
	
	
}
