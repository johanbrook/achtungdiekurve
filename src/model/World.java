package model;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

import util.WorldUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class World implements ActionListener {

	@Expose
	private List<Player> players;
	@Expose
	private Dimension worldSize;
	private static World instance;
	@Expose
	private int round;
	private List<Player> roundPlayers;
	private Timer t;
	private Gson gson;

	private World() {
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		t = new Timer(40, this);
		players = new ArrayList<Player>();
		worldSize = new Dimension(800, 600);
	}

	public static World getInstance() {
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Dimension getWorldSize() {
		return worldSize;
	}

	private void newRound() {
		round++;
		for (int i = 0;i<players.size();i++) {
			players.get(i).newRound();
		}
		roundPlayers = WorldUtils.getRoundPlayers();
		send(new JsonObject("newRound", this));
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		send(new JsonObject("start", this));
	}

	private void endRound(Player winner, boolean send) {
		for (int i = 0; i< players.size();i++) {
			players.get(i).endRound();
		}
		if (send) {
			send(new JsonObject("end", winner));
		}
	}
	public void send(JsonObject o) {
		String s = gson.toJson(o);
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			try {
				p.send(s);
			} catch (IOException e) {
				players.remove(i);
				i--;
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		Player alive = getIfAllButOneDead();
		if (alive != null) {
			endRound(alive, true);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			newRound();
		} else {
			for (int i = 0;i<roundPlayers.size();i++) {
				Player p = roundPlayers.get(i);
				if (p.isAlive()) {
					p.updateMove();
					if (p.isOutsideRange(worldSize)) {
						p.setAlive(false);
						givePointsToAllBut(p);
						send(new JsonObject("score", this));
					} else {
						for (int j =0;j<roundPlayers.size();j++) {
							if (p.collideWith(roundPlayers.get(j))) {
								p.setAlive(false);
								givePointsToAllBut(p);
								send(new JsonObject("score", this));
							}
						}
					}
				}
			}
		}
		send(new JsonObject("world", this));
	}

	public void givePointsToAllBut(Player p) {
		for (int i = 0;i<roundPlayers.size();i++) {
			if (!p.equals(roundPlayers.get(i)) && roundPlayers.get(i).isAlive()) {
				roundPlayers.get(i).addPoint();
			}
		}
		Collections.sort(players);
	}

	public void playerConnect(Player p) {
		players.add(p);
		if (players.size() == 2) {
			endRound(null, false);
			newRound();
			t.start();
		} else if (players.size() < 2) {
			send(new JsonObject("init", this));
		}
	}

	public void playerDisconnect(Player p) {
		p.setAlive(false);
		players.remove(p);
		if (players.size() == 1) {
			t.stop();
			players.get(0).addPoint();
			send(new JsonObject("init", this));
		}
	}

	private int numbersAlive() {
		int alive = 0;
		for (int i = 0;i <roundPlayers.size();i++) {
			if (roundPlayers.get(i).isAlive()) {
				alive++;
			}
		}
		return alive;
	}

	private Player getIfAllButOneDead() {
		if (numbersAlive() == 1) {
			for (int i = 0;i<roundPlayers.size();i++) {
				if (roundPlayers.get(i).isAlive()) {
					return roundPlayers.get(i);
				}
			}
		}
		return null;
	}
}
