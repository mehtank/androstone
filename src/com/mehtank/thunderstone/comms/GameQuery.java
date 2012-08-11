package com.mehtank.thunderstone.comms;

import com.mehtank.thunderstone.engine.Game.GameState;

public class GameQuery {
	public enum QueryType {
		GETNAME, NAME,
		GETCARD, CARD,
		GETOPTION, OPTION, 
		ORDERCARDS, CARDORDER,
		
		GAMEEVENT,
	}

	public QueryType t; // type of query
	public QueryType r; // type of response requested; null if no response necessary

	public String s;
	public boolean b;
	public int i;
	public Object o;
	public GameState gs;
	
	public GameQuery(QueryType t, QueryType r) {
		this.t = t;
		this.r = r;
	}
	
	public GameQuery setState(GameState gs) {
		this.gs = gs;
		return this;
	}
	
	public GameQuery setType(QueryType r) {
		this.t = r;
		return this;
	}
	public GameQuery setString(String s) {
		this.s = s;
		return this;
	}
	public GameQuery setBoolean(boolean b) {
		this.b = b;
		return this;
	}
	public GameQuery setInteger(int i) {
		this.i = i;
		return this;
	}
	public GameQuery setObject (Object o) {
		this.o = o;
		return this;
	}
}
