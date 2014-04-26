package util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import model.Coordinate;
import model.Player;
import model.World;

public class WorldUtils {

	public static List<Player> getRoundPlayers() {
		List<Player> l = new LinkedList<>();
		for (Player p : World.getInstance().getPlayers()) {
			if (p.isInRound()) {
				l.add(p);
			}
		}
		return l;
	}

	
	public static Coordinate generateHead() {
		int x = (int) (Math.random() * World.getInstance().getWorldSize().width);
		int y = (int) (Math.random() * World.getInstance().getWorldSize().height);
		while (containHead(x, y)) {
			x = (int) (Math.random() * World.getInstance().getWorldSize().width);
			y = (int) (Math.random() * World.getInstance().getWorldSize().height);
		}
		return new Coordinate(x, y, true);
	}

	private static boolean containHead(int x, int y) {
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

	private static boolean containName(String name) {
		for (Player p : World.getInstance().getPlayers()) {
			if (p.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static String generateColor() {
		Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
		while (containsSimilarColor(c))
			c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
		String hex = Integer.toHexString(c.getRGB() & 0xffffff);
		if (hex.length() < 6) {
			hex = "0" + hex;
		}
		hex = "#" + hex;
		return hex;
	}

	private static boolean containsSimilarColor(Color c) {
		for (Player p : World.getInstance().getPlayers()) {
			if (p.getColor().equals(c)) {
				return true;
			}
		}
		return false;
	}

}
