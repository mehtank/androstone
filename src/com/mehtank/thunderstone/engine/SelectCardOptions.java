package com.mehtank.thunderstone.engine;

import java.util.ArrayList;

public class SelectCardOptions {
	public String header = "";
	
	enum fromLocations {HAND, VILLAGE, DUNGEON, HEROES, ALL};
	
	fromLocations from = fromLocations.ALL;
	int maxCost = -1;
	int minCost = -1;
	int count = 1;
	boolean exactly = true;
	boolean isPassable = false;
	boolean ordered = false;
	ArrayList<String[]> parameters = new ArrayList<String[]> ();

	// Prompt
	public SelectCardOptions to(String s) {header = s; return this;}
	
	// Location of card
	public SelectCardOptions fromHand() {from = fromLocations.HAND; return this;}
	public SelectCardOptions fromVillage() {from = fromLocations.VILLAGE; return this;}
	
	// Number of cards
	public SelectCardOptions exactly(int c) {count = c; return this;}
	public SelectCardOptions atMost(int c) {count = c; exactly = false; return this;}

	// Return options
	public SelectCardOptions isPassable() {isPassable = true; return this;}
	public SelectCardOptions ordered() {ordered = true; return this;}

	// Filters
	public SelectCardOptions maxCost(int c) {maxCost = c; return this;}
	public SelectCardOptions minCost(int c) {minCost = c; return this;}
	public SelectCardOptions hasParameter(String key, String value) { parameters.add(new String[] {key, value}); return this; }

	public boolean isValid(Card c) {
		if ((maxCost >= 0) && (c.getCost() > maxCost)) return false;
		if ((minCost >= 0) && (c.getCost() < minCost)) return false;
		for (String[] param : parameters) {
			try {
				if (!c.getStr(param[0]).equals(param[1]))
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	public String toString() {
		// TODO: move strings to Strings
		
		String str = "Select ";

		if (exactly) str += "exactly ";
		else str += "up to ";
		
		str += count + " ";
		
		str += "cards ";
		
		switch (from) {
		case HAND:
			str += Strings.fromHand;
			break;
		case VILLAGE:
			str += Strings.fromVillage;
			break;
		}
		
		if (ordered) str += "in order ";
		
		if (minCost >= 0 || maxCost >= 0) {
			str += "that costs ";
			if (minCost >= 0) str += "at least �" + minCost + " ";
			if (maxCost >= 0) str += "at most �" + maxCost + " ";
		}
		
		str += "to " + header;
		if (isPassable) str += " (or pass)";
		return str;
	}
	
	public Card[] filter(Card[] input) {
		ArrayList<Card> output = new ArrayList<Card>();
		for (Card c : input)
			if (isValid(c)) 
				output.add(c);
		return output.toArray(new Card[0]);
	}
}