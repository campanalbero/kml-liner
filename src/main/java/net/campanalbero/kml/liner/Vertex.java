package net.campanalbero.kml.liner;

public class Vertex {
	private double x;
	private double y;
	private double z;
	
	public Vertex(String[] xyz) {
		if (xyz.length != 3) {
			throw new IllegalArgumentException("xyz.length must be 3: " + xyz.length);
		}
		
		this.x = Double.parseDouble(xyz[0]);
		this.y = Double.parseDouble(xyz[1]);
		this.z = Double.parseDouble(xyz[2]);
	}
	
	public Vertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Vertex)) {
			return false;
		}
		
		if (this.x != ((Vertex)o).x) {
			return false;
		}
		
		if (this.y != ((Vertex)o).y) {
			return false;
		}
		
		if (this.z != ((Vertex)o).z) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return "x = " + x + ", y = " + y + ", z = " + z;
	}
}
