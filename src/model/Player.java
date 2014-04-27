package model;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.jetty.websocket.WebSocket;

import util.WorldUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Player implements WebSocket.OnTextMessage, Comparable<Player> {

	@Expose
	private String color;
	private LinkedList<Coordinate> path;
	@Expose
	private String name;
	@Expose
	private int points;
	@Expose
	private Coordinate head;
	@Expose
	private Coordinate lasthead;
	@Expose
	private boolean inRound;
	@Expose
	private boolean isAlive;
	private Connection con;
	@Expose
	private double direction;
	private double speed = 3;
	private PlayerCommand pc;
	private boolean paintTail;
	private int stepsToChangePaint;
	private double dirChange = 10;
	
	public Player() {
		paintTail = true;
		this.path = new LinkedList<Coordinate>();
		this.name = WorldUtils.generateName();
		this.color = WorldUtils.generateColor();
		this.head = new Coordinate(-100, -100, false);
		this.inRound = false;
		this.isAlive = false;
	}

	public LinkedList<Coordinate> getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}

	public void setPath(LinkedList<Coordinate> path) {
		this.path = path;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPoint() {
		points++;
	}

	public void onClose(int arg0, String arg1) {
		World.getInstance().playerDisconnect(this);
	}

	public void onOpen(Connection arg0) {
		this.con = arg0;
		World.getInstance().playerConnect(this);
	}

	public void onMessage(String msg) {
		try {
			PlayerCommand c = new Gson().fromJson(msg, PlayerCommand.class);
			this.pc = c;
		} catch (Exception e) {
		}
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isInRound() {
		return inRound;
	}

	public void updateMove() {
		if (pc != null) {
			if (pc.getDirection() != null && pc.getDirection().equals("left")) {
				direction -= dirChange + 360;
			} else if (pc.getDirection() != null && pc.getDirection().equals("right")) {
				direction += dirChange;
			} else if (pc.getName() != null) {
				if (!WorldUtils.containName(pc.getName())) {
					setName(pc.getName());
				}
			}
		}

		direction %= 360;
		double x = speed * Math.cos(Math.toRadians(direction));
		double y = speed * Math.sin(Math.toRadians(direction));
		path.add(head);
		lasthead = head;
		head = new Coordinate(head.x + x, head.y + y, paintTail);
		pc = null;
		if (stepsToChangePaint-- == 0) {
			paintTail = !paintTail;
			if (paintTail) {
				stepsToChangePaint = (int) (Math.random() * 200);
			} else {
				stepsToChangePaint = 5;
			}
		}
	}

	public void send(final String s) throws IOException {
		if (con != null && con.isOpen()) {
			new Thread(new Runnable() {
				public void run() {
					try {
						con.sendMessage(s);
					} catch (IOException e) {
					}
				}
			}).start();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == Player.class) {
			return name.equals(((Player) obj).name);
		}
		return false;
	}

	public Coordinate getHead() {
		return head;
	}

	public void newRound() {
		stepsToChangePaint = (int) (Math.random() * 200);
		paintTail = true;
		direction = Math.random() * 360;
		path = new LinkedList<Coordinate>();
		head = WorldUtils.generateHead();
		inRound = true;
		isAlive = true;
	}

	public void endRound() {
		isAlive = false;
		inRound = false;
		path = new LinkedList<Coordinate>();
		head = new Coordinate(-100, -100, false);
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public void setInRound(boolean inRound) {
		this.inRound = inRound;
	}

	public boolean isOutsideRange(Dimension d) {
		if (head.x > d.width || head.x < 0) {
			return true;
		} else if (head.y > d.height || head.y < 0) {
			return true;
		}
		return false;
	}

	public boolean collideWith(Player p) {
		if (path.size() > 1) {
			LinkedList<Coordinate> op = new LinkedList<Coordinate>(p.path);
			if (p != this) {
				op.add(p.head);
			} else {
				op.removeLast();
			}
			Line2D lastPath = path.getLast().toLine(head);
			for (int i = 0; i < op.size() - 1; i++) {
				Coordinate first = op.get(i);
				Coordinate second = op.get(i + 1);
				if (first.isVisible && second.isVisible) {
					Line2D l2 = first.toLine(second);
					if (l2.intersectsLine(lastPath)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public int compareTo(Player o) {
		return o.points-points;
	}
}
