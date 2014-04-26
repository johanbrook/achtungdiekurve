package model;
import java.awt.geom.Line2D;

import com.google.gson.annotations.Expose;

public class Coordinate {
	@Expose
	public double x;
	@Expose
	public double y;
	@Expose
	public boolean isVisible;
	
	public Coordinate(double x, double y, boolean isVisible) {
		this.x = x;
		this.y = y;
		this.isVisible = isVisible;
	}
	
	public Line2D toLine(Coordinate second){
		return new Line2D.Double(x, y, second.x, second.y);
	}
	
}
