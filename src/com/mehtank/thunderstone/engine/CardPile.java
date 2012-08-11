package com.mehtank.thunderstone.engine;

import java.util.ArrayList;

public class CardPile extends ArrayList<Card> {
	private static final long serialVersionUID = 6916512102368602030L;

	private String name;

    String getName() {
		return name;
	}

	public CardPile(String name) {
    	super();
        this.name = name;
    }

    public Card[] toArray() {
        return toArray(new Card[0]);
    }

    public Card top() {
    	if (size() > 0)
    		return get(0);
    	return null;
    }
}
