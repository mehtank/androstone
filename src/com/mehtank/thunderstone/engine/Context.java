package com.mehtank.thunderstone.engine;

import com.mehtank.thunderstone.api.GameInterface;
import com.mehtank.thunderstone.api.PlayerInterface;

public class Context {

	private Game game;
	private Player currentPlayer;
	private int gold = 0;
	private int attack = 0;
	private int magic = 0;
	private int buys = 1;

	public Context(Game game, Player currentPlayer) {
		this.game = game;
		this.currentPlayer = currentPlayer;
	}

	public GameInterface getGame() { return game; }
	public PlayerInterface getCurrentPlayer() { return currentPlayer; }
	public int getGold() { return gold; }
	public int getBuys() { return buys; }
	public int getAttack() { return attack; }
	public int getMagic() { return magic; }

	public void addGold(int gold) {
		this.gold += gold;
	}
	public void addBuys(int buys) {
		this.buys += buys;
	}
	public void addAttack(int attack) {
		this.attack += attack;
	}
	public void addMagic(int magic) {
		this.magic += magic;
	}

}
