package model;
import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.jetty.websocket.WebSocket;

import util.WorldUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Player implements WebSocket.OnTextMessage {

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
	private boolean inRound;
	@Expose
	private boolean isAlive;
	private Connection con;
	private int direction;
	private int speed = 2;
	private PlayerCommand pc;
	private boolean paintTail;

	public Player() {
		paintTail = true;
		this.path = new LinkedList<>();
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

	@Override
	public void onClose(int arg0, String arg1) {
		World.getInstance().playerDisconnect(this);
	}

	@Override
	public void onOpen(Connection arg0) {
		this.con = arg0;
		World.getInstance().playerConnect(this);
	}

	@Override
	public void onMessage(String msg) {
		if (msg.contains("direction")) {
			try {
				PlayerCommand c = new Gson().fromJson(msg, PlayerCommand.class);
				this.pc = c;
			} catch (Exception e) {
			}
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
				direction -= 10 + 360;
			} else if (pc.getDirection() != null && pc.getDirection().equals("right")) {
				direction += 10;
			} else if (pc.getName() != null) {
				setName(pc.getName());
			}
		}

		direction %= 360;
		double x = speed * Math.cos(Math.toRadians(direction));
		double y = speed * Math.sin(Math.toRadians(direction));
		path.add(head);
		head = new Coordinate(head.x + x, head.y + y, paintTail);
		pc = null;
	}

	public void send(String s) throws IOException {
		if (con != null) {
			con.sendMessage(s);
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
			LinkedList<Coordinate> op = new LinkedList<>(p.path);
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
}
