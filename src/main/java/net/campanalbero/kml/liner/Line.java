package net.campanalbero.kml.liner;

public class Line {
	private final Vertex v0;
	private final Vertex v1;
	
	public Line(Vertex v0, Vertex v1) {
		this.v0 = v0;
		this.v1 = v1;
	}
	
	public Vertex getV0() {
		return v0;
	}
	
	public Vertex getV1() {
		return v1;
	}
	
	public double getLength() {
		double vx = v1.getX() - v0.getX();
		double vy = v1.getY() - v0.getY();
//		double vz = v1.getZ() - v0.getZ();
				
		return Math.sqrt(vx * vx + vy * vy);
	}
}
