package com.mehtank.thunderstone.engine;

import com.mehtank.thunderstone.api.GameEventListener;
import com.mehtank.thunderstone.api.PlayHandler;
import com.mehtank.thunderstone.api.PlayerInterface;
import com.mehtank.thunderstone.comms.GameQuery;

public class Player extends PlayerQuerys implements PlayerInterface, GameEventListener {

	private PlayHandler playHandler;
	private Game game;
	
	private CardPile hand = new CardPile(Strings.playerHandPile);
	private CardPile discard = new CardPile(Strings.playerDiscardPile);
	private CardPile deck = new CardPile(Strings.playerDeckPile);

	public Player(PlayHandler p, Game game) {
		this.playHandler = p;
		this.game = game;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Internal actions
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void replenishDeck() {
        while (discard.size() > 0) {
        	int i = game.getRand().nextInt(discard.size());
            discard.get(i).moveTo(deck);
        }

        GameEvent event = new GameEvent(GameEvent.Type.RESHUFFLES, this);
        game.broadcastEvent(event);
    }

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Game event notifications [ GameEventListener ]
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public void gameEvent(GameEvent event) {
		// TODO Auto-generated method stub
	}
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Player interactions [ PlayerInterface ]
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public String getName() {
		return playHandler.getName();
	}

	@Override
    public Card[] getHand() {
        return hand.toArray();
    }
	@Override
    public int getHandSize() { return hand.size(); }
	@Override
    public int getDiscardSize() { return discard.size(); }
	@Override
    public int getDeckSize() { return deck.size(); }

	@Override
	public void gain(Card card) {
		discard(card);
	}
	@Override
	public void discard(Card card) {
		card.moveTo(discard);
	}
	@Override
	public void discardHand() {
		while (hand.size() > 0)
			discard(hand.get(0));
	}

	@Override
	public Card drawFromDeck() {
    	if (getDeckSize() == 0) 
    		replenishDeck();
    	if (getDeckSize() == 0)
    		return null;

    	return deck.get(0);
    }

	@Override
    public void draw() {
    	Card c = drawFromDeck();
    	if (c != null)
    		c.moveTo(hand);
    }
	@Override
	public void draw(int numCards) {
		for (int i = 0; i < numCards; i++) 
			draw();
	}

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Player queries
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public GameQuery query(GameQuery q) {
		return playHandler.query(q.setState(game.getState().setHand(getHand())));
	}


	public class PlayerState {
		String name = "";
		int handSize = 0;
		int deckSize = 0;
		int discardSize = 0;
		
		public PlayerState(Player p) {
			name = p.getName();
			handSize = p.getHandSize();
			deckSize = p.getDeckSize();
			discardSize = p.getDiscardSize();
		}
		
		public String toString() {
			String s = name;
			s += " < ";
			s += Strings.playerDeckSize + deckSize + " , ";
			s += Strings.playerHandSize + handSize + " , ";
			s += Strings.playerDiscardSize + discardSize + " >";
			
			return s;
		}
	}

}
