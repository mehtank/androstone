package com.mehtank.thunderstone.engine;

public class Strings {
	public static final String chooseAction = "Choose your action";
	
	public static final String visitVillage = "Visit the village";
	public static final String enterDungeon = "Enter the dungeon";
	public static final String rest = "Rest";
	
	public static final String pickOneCard = "Select a card ";
	
	public static final String fromHand = "from your hand ";
	public static final String fromVillage = "from the village ";
	
	public static final String toDestroy = "to destroy.";
	public static final String toBuy = "to buy.";	
	
	public static final String playerHandPile = "Hand";
	public static final String playerDeckPile = "Deck";
	public static final String playerDiscardPile = "Discard";
	public static final String trashPile = "Trash";
	
	public static final String tableauStateHeader = "*** Tableau ***";
	public static final String tableauStateDungeon = "Dungeon: ";
	public static final String tableauStateDungeonRank = "Rank";
	public static final String tableauStateDungeonDeck = "Deck";
	public static final String tableauStateHeroes = "Heroes: ";
	public static final String tableauStateVillage = "Village: ";
	public static final String tableauStateTrash = "Trash";
	
	public static final String playerStateHeader = "*** Players ***";
	public static final String playerHandSize = "*";
	public static final String playerDeckSize = "=";
	public static final String playerDiscardSize = "#";
	
	public static final String currentHand = "*** Current Hand ***";

	public static final String chooseEffect = "Choose an effect";
	
	public static final String chooseMonster = "Choose a monster to fight";

	
	public static String getCanonicalName(String orig) {
		String s = new String(orig);
		return s.replaceAll("\\W","").toLowerCase();
	}
}
