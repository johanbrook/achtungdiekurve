package util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import model.Coordinate;
import model.Player;
import model.World;

public class WorldUtils {

	public static List<Player> getRoundPlayers() {
		List<Player> l = new LinkedList<Player>();
		for (Player p : World.getInstance().getPlayers()) {
			if (p.isInRound()) {
				l.add(p);
			}
		}
		return l;
	}

	public static Coordinate generateHead() {
		double x = Math.random() * World.getInstance().getWorldSize().width;
		double y = Math.random() * World.getInstance().getWorldSize().height;
		while (containHead(x, y) || !spaceFromWall(x, y)) {
			x = Math.random() * World.getInstance().getWorldSize().width;
			y = Math.random() * World.getInstance().getWorldSize().height;
		}
		return new Coordinate(x, y, true);
	}

	public static boolean spaceFromWall(double x, double y) {
		double minX = Math.min(World.getInstance().getWorldSize().width - x, x);
		double minY = Math.min(World.getInstance().getWorldSize().height - y, y);
		return minX >= 50 && minY >= 50;
	}

	public static boolean containHead(double x, double y) {
		for (Player p : World.getInstance().getPlayers()) {
			double dX = p.getHead().x - x;
			double dY = p.getHead().y - y;
			if (Math.hypot(dX, dY) < 50) {
				return true;
			}
		}
		return false;
	}

	public static String generateName() {
		int i = 0;
		String prefix = "Guest";
		while (containName(prefix + i))
			i++;
		return prefix + i;
	}

	public static boolean containName(String name) {
		for (Player p : World.getInstance().getPlayers()) {
			if (p.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static String generateColor() {
		Color c = new Color((int) (Math.random() * 200) + 55, (int) (Math.random() * 200) + 55,
				(int) (Math.random() * 200) + 55);
		while (containsSimilarColor(c))
			c = new Color((int) (Math.random() * 200) + 55, (int) (Math.random() * 200) + 55,
					(int) (Math.random() * 200) + 55);
		String hex = Integer.toHexString(c.getRGB() & 0xffffff);
		if (hex.length() < 6) {
			hex = "0" + hex;
		}
		hex = "#" + hex;
		return hex;
	}

	public static boolean containsSimilarColor(Color c) {
		for (Player p : World.getInstance().getPlayers()) {
			if (isSimilarColor(hex2Rgb(p.getColor()), c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSimilarColor(Color c1, Color c2) {
		int dR = Math.abs(c1.getRed() - c2.getRed());
		int dG = Math.abs(c1.getGreen() - c2.getGreen());
		int dB = Math.abs(c1.getBlue() - c2.getBlue());
		return (dR + dG + dB) < 50;
	}

	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}

}
