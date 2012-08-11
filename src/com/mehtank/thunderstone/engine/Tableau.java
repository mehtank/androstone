package com.mehtank.thunderstone.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.mehtank.thunderstone.engine.Effect.EffectType;

public class Tableau {
	private static final int dungeonDeck = 4;

	private CardPile[] dungeon = new CardPile[dungeonDeck + 1];
	
	private ArrayList<CardPile> heroes = new ArrayList<CardPile>();
	private ArrayList<CardPile> village = new ArrayList<CardPile>();
	private ArrayList<CardPile> other = new ArrayList<CardPile>();
	private CardPile trash = new CardPile(Strings.trashPile);
	
	private ArrayList<CardPile> allPiles = new ArrayList<CardPile>();
	
	public Tableau() {
		for (int i = 0; i < dungeonDeck; i++) {
			dungeon[i] = new CardPile("Dungeon rank " + i);
			allPiles.add(dungeon[i]);
		}
		dungeon[dungeonDeck] = new CardPile("Dungeon deck");		
	}
	
	public Card[] getBuyableCards() {
		return getBuyableCards(Integer.MAX_VALUE);
	}
	
	public Card[] getBuyableCards(int maxCost) {
		ArrayList<Card> all = new ArrayList<Card>();
		
		all.addAll(getBuyableCards(heroes, maxCost));
		all.addAll(getBuyableCards(village, maxCost));		
		return all.toArray(new Card[0]);
	}

	public ArrayList<Card> getBuyableCards(ArrayList<CardPile> piles, int maxCost) {
		ArrayList<Card> all = new ArrayList<Card>();
		
		for (CardPile p : piles)
			if (p.top() != null)
				if (p.top().getCost() <= maxCost)
					all.add(p.top());
		
		return all;
	}
	
	public void addHeroPile(CardPile p) {
		if (p == null) return;
		heroes.add(p);
		allPiles.add(p);
	}
	public void addVillagePile(CardPile p) {
		if (p == null) return;
		village.add(p);
		allPiles.add(p);
	}
	public void addOtherPile(CardPile p) {
		if (p == null) return;
		other.add(p);
		allPiles.add(p);
	}
	public void addDungeonPile(CardPile monsterPile) {
		while (monsterPile.size() > 0)
			monsterPile.get(0).moveTo(dungeon[dungeonDeck]);
		Collections.shuffle(dungeon[dungeonDeck]);
	}

	private boolean advanceDungeon() {
		for (int i = 1; i < dungeonDeck; i++) {
			if (dungeon[i].size() > 0) 
				continue;
			boolean changed = false;
			for (int j = i; j < dungeonDeck; j++) {
				if (dungeon[j+1].size() > 0) {
					dungeon[j+1].get(0).moveTo(dungeon[j]);
					changed = true;
				}
			}
			return changed;
		}
		return false;
	}
	
	public ArrayList<Effect> fillDungeon() {
		boolean breached = (dungeon[1].size() == 0);
		
		while (advanceDungeon());
		
		if (breached && dungeon[1].size() > 0)
			return dungeon[1].get(0).getEffects(EffectType.BREACH);

		return new ArrayList<Effect>();
	}
	
	public Card[] getMonstersToFight() {
		Card[] monsters = new Card[dungeonDeck];

		for (int i = 0; i < dungeonDeck; i++)
			if (dungeon[i].size() > 0) 
				monsters[i] = dungeon[i].get(0);
		
		return monsters;
	}
	
	public Card getCardFromPile(String s) {
		for (CardPile p : allPiles)
			if (p.size() > 0)
				if (p.getName().equals(s))
					return p.top();
		return null;
	}
	
	public Card getCardFromPile(Card c) {
		for (CardPile p : allPiles)
			if (p.size() > 0)
				if (c.equals(p.top()))
					return p.top();
		return null;
	}
	
	public void trash(Card c) {
		c.moveTo(trash);
	}

	public static class TableauState {
		public Card[] dungeon;
		public int dungeonDeck;
		LinkedHashMap<Card, TreeMap<Integer, Integer>> heroes = new LinkedHashMap<Card, TreeMap<Integer, Integer>>();
		LinkedHashMap<Card, Integer> village = new LinkedHashMap<Card, Integer>();
		public int trash;
		
		public TableauState (Tableau t) {
			dungeon = new Card[t.dungeon.length - 1];
			for (int i=0; i < dungeon.length; i++) {
				dungeon[i] = t.dungeon[i].top();
			}
			dungeonDeck = t.dungeon[t.dungeon.length - 1].size();
			for (CardPile p : t.heroes) {
				if (p == null)
					continue;
				if (p.size() == 0)
					continue;
				TreeMap<Integer, Integer> hero = new TreeMap<Integer, Integer>();
				for (Card c : p) {
					if (hero.containsKey(c.getLevel()))
						hero.put(c.getLevel(), 1 + hero.get(c.getLevel()));
					else
						hero.put(c.getLevel(), 1);
				}
				heroes.put(p.top(), hero);
			}
			for (CardPile p : t.village) 
				if (p != null && p.size() > 0)
					village.put(p.top(), p.size());

			trash = t.trash.size();
		}
		
		public String toString() {
			String state = Strings.tableauStateHeader + "\n";

			state += Strings.tableauStateDungeon + "\n";
			for (int i=0; i < dungeon.length; i++) {
				state += "  " + Strings.tableauStateDungeonRank + " " + i + ": ";
				if (dungeon[i] == null)
					state += "\n";
				else
					state += dungeon[i].getTitle() + "\n";
			}
			state += "  " + Strings.tableauStateDungeonDeck + " (";
			state += dungeonDeck + ")\n";
			
			state += Strings.tableauStateHeroes + "\n";
			for (Card c : heroes.keySet()) {
				state += "  " + c.getTitle() + " ( ";
				for (Integer i : heroes.get(c).values()) 
					state += i + " ";
				state += ")\n";
			}

			state += Strings.tableauStateVillage + "\n";
			for (Card c : village.keySet()) {
				state += "  " + c.getTitle() + " (";
				state += village.get(c) + ")\n";
			}
			
			state += Strings.tableauStateTrash + " (";
			state += trash + ")\n";	
			
			return state;
		}
	}
	
	public TableauState getState() {
		return new TableauState(this);
	}

}
