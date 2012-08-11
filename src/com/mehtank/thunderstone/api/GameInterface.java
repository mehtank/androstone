package com.mehtank.thunderstone.api;

import java.util.Random;

import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.GameEvent;

public interface GameInterface {
	public Random getRand();

	public void trash(Card c);
	public void broadcastEvent(GameEvent event);
	
	public void info(String s);
	public void debug(String s);
}
