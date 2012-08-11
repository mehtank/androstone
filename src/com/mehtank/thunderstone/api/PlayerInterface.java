package com.mehtank.thunderstone.api;

import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.SelectCardOptions;

public interface PlayerInterface {
	public String getName();
	
	public Card[] getHand();
	public int getHandSize();
	public int getDiscardSize();
	public int getDeckSize();

	public void gain(Card card);
	public void discard(Card card);
	public void discardHand();

	public Card drawFromDeck();
	public void draw();
	public void draw(int numCards);

	public Card pickACard(SelectCardOptions sco, Card[] hand);
	
}
