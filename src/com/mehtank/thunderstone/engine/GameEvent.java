package com.mehtank.thunderstone.engine;

import com.mehtank.thunderstone.api.PlayerInterface;

public class GameEvent {
    public enum Type {
        STARTGAME, // A new game is starting, called at the start of each game when multiple are played

        STARTTURN, // Player begins a turn
        
        VISITSVILLAGE, // Player visits village
        ENTERSDUNGEON, // Player enters dungeon
        RESTS,		  // Player rests

        BUYS, // Buying a card in the buy phase.

        RESHUFFLES, // Discard pile shuffled to create a new deck for one of the players

        OBTAINS, // Card was obtained by a player through an effect of an action
        TRASHES, // Card removed from the game
        REVEALS, // Card revealed by an action

        ENDTURN, // Player's turn ends
        
        GAMEOVER, // Game completed
    }

    private Type type;  // type of event
    private PlayerInterface who; // Player who generated the event
    private Card card;  // optional, depending on event type
    private String str; // optional, depending on event type

	public GameEvent(Type type, PlayerInterface currentPlayer) {
        this.type = type;
        this.who = currentPlayer;
    }
	public GameEvent setString(String str) {
		this.str = str; return this;
	}
	public GameEvent setCard(Card card) {
		this.card = card; return this;
	}

    public Type getType() {
        return type;
    }
    public PlayerInterface getPlayer() {
        return who;
    }
    public Card getCard() {
        return card;
    }
    public String getStr() {
    	return str;
    }
}
