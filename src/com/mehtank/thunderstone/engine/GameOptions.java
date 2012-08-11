package com.mehtank.thunderstone.engine;

public class GameOptions {
	public boolean keepUndefeatedMonster = false;

	public GameOptions keepUndefeatedMonster() {
		this.keepUndefeatedMonster = true; return this;
	}
	
}
