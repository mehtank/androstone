package com.mehtank.thunderstone.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.mehtank.thunderstone.api.GameEventListener;
import com.mehtank.thunderstone.api.GameInterface;
import com.mehtank.thunderstone.api.LogReader;
import com.mehtank.thunderstone.api.PlayHandler;
import com.mehtank.thunderstone.cards.Cards;
import com.mehtank.thunderstone.engine.Effect.EffectType;
import com.mehtank.thunderstone.engine.Logger.LogLevel;
import com.mehtank.thunderstone.engine.Player.PlayerState;
import com.mehtank.thunderstone.engine.Tableau.TableauState;

public class Game implements GameInterface, GameEventListener, LogReader {
	private long randSeed;
	private Random rand;
	private Logger logger = new Logger();

//	private GameOptions options;
	private Tableau table;

	private static Cards common = new com.mehtank.thunderstone.cards.common.Cards();	
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private int currentPlayerIndex = 0;
	private Player currentPlayer;
	private int turnCount = 0;
	
	private Context context;

	private boolean gameOver = false;
	private ArrayList<GameEventListener> listeners = new ArrayList<GameEventListener>();

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Setup and initialization
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Game(Tableau t, PlayHandler[] ps) {
		newGame(t, ps, new GameOptions());
	}
	public Game(Tableau t, PlayHandler[] ps, GameOptions o) {
		newGame(t, ps, o);
	}
	
	private void newGame(Tableau t, PlayHandler[] ps, GameOptions o) {
		logger.addReader(LogLevel.GAME, this);
		logger.addReader(LogLevel.INFO, this);
		logger.addReader(LogLevel.DEBUG, this);
		listeners.add(this);
		
		randSeed = System.currentTimeMillis();
		debug("Starting seed = " + randSeed);
		
		rand = new Random(randSeed);
		
//		this.options = o;
		this.table = t;
		
		// TODO: validate t, ps
		setupTable();
		setupPlayers(ps);
	}

	private void setupTable() {
		info("Setting up table");
		table.addVillagePile(common.getCardPile("Dagger"));
		table.addVillagePile(common.getCardPile("IronRations"));
		table.addVillagePile(common.getCardPile("Torch"));
		table.addVillagePile(common.getCardPile("Militia"));
		
		table.fillDungeon();
	}
	private void setupPlayers(PlayHandler[] ps) {
		info("Setting up players");		
		logger.indent();
		for (PlayHandler p : ps) {
			info("Creating player " + p.getName());
			logger.indent();
			Player player = new Player(p, this);
			listeners.add(player);
			
			info("Dealing cards");
			for (int i = 0; i < 2; i++) {
				player.gain(table.getCardFromPile("Dagger"));
				player.gain(table.getCardFromPile("IronRations"));
				player.gain(table.getCardFromPile("Torch"));
			}
			for (int i = 0; i < 6; i++) {
				player.gain(table.getCardFromPile("Militia"));
			}
			info("Drawing starting hand");
			player.draw(6);
			
			players.add(player);
			logger.unindent();
		}
		info("Seating players");
		Collections.shuffle(players);
		logger.unindent();
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Play order
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void play() {
		broadcastEvent(new GameEvent(GameEvent.Type.STARTGAME, null));
		while (!gameOver ) {
			currentPlayer = players.get(currentPlayerIndex);
			broadcastEvent(new GameEvent(GameEvent.Type.STARTTURN, currentPlayer));
			context = new Context(this, currentPlayer);
			
			// TODO - Reveal Hand
			
			Integer i = currentPlayer.selectOption(Strings.chooseAction, new String[] {Strings.visitVillage, Strings.enterDungeon, Strings.rest});
			if (i == null) i = 2;
			if (i.equals(0)) { // visit village
				broadcastEvent(new GameEvent(GameEvent.Type.VISITSVILLAGE, currentPlayer));
				logger.indent();
				doVillage();
			} else if (i.equals(1)) { // enter dungeon
				broadcastEvent(new GameEvent(GameEvent.Type.ENTERSDUNGEON, currentPlayer));
				logger.indent();
				doDungeon();
			} else { // rest or error
				broadcastEvent(new GameEvent(GameEvent.Type.RESTS, currentPlayer));
				logger.indent();
				doRest();
			}
			logger.unindent();
			
			info("Cleaning up");
			logger.indent();
			currentPlayer.discardHand();
			currentPlayer.draw(6);
			logger.unindent();

			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			turnCount ++;
		}
	}
	
	private void doVillage() {
		// Optional village effects
		pickEffects(EffectType.VILLAGE, EffectType.REPEATVILLAGE);
		
		// Mandatory village effects
		mandatoryEffects(EffectType.ALWAYSVILLAGE);

		// Tally money
		for (Card c: currentPlayer.getHand())
			if (c.getGold() != null)
				context.addGold(c.getGold());
	
		// Pick cards to buy
		while (context.getBuys() > 0) {
			int maxCost = context.getGold();
			SelectCardOptions sco = new SelectCardOptions()
				.fromVillage()
				.isPassable()
				.maxCost(maxCost)
				.to(Strings.toBuy);
			
			Card c = currentPlayer.pickACard(sco, table.getBuyableCards(maxCost)); 
			
			if (c == null) 
				break;

			Card bought = table.getCardFromPile(c);

			// TODO: handle errors better
			if (bought == null)
				continue;
			if (bought.getCost() > maxCost)
				continue;

			broadcastEvent(new GameEvent(GameEvent.Type.BUYS, currentPlayer).setCard(bought));
			currentPlayer.gain(bought);
			
			context.addGold(-bought.getCost());
			context.addBuys(-1);
		}

		// TODO - Upgrade heroes
	}

	private void doDungeon() {
		// Optional dungeon effects
		pickEffects(EffectType.DUNGEON, EffectType.REPEATDUNGEON);

		// TODO - Equip heroes
		// Mandatory dungeon effects
		mandatoryEffects(EffectType.ALWAYSDUNGEON);

		// Select monster
		Card[] monsters = table.getMonstersToFight();
		int monsterRank = pickMonster(monsters); 
		Card monster = monsters[monsterRank]; 
		
		// TODO - Calculate total attack and defense
		
		// Resolve battle effects
		for (Effect e : monster.getEffects(EffectType.BATTLE))  // XXX choose order?
			doEffect(e);

		// TODO - Determine winner and move monster 
		// TODO - Receive spoils
		// Shift monster cards / refill hall and resolve breach
		for (Effect e : table.fillDungeon())  // XXX choose order?
			doEffect(e);
	}
	
	int pickMonster(Card[] monsters) {
		ArrayList<String> opts = new ArrayList<String>();
		for (int i = 0; i < monsters.length; i++)
			if (monsters[i] != null) 
				opts.add(Strings.tableauStateDungeonRank + " " + i + ": " + monsters[i].getTitle());
		Integer i = currentPlayer.selectOption(Strings.chooseMonster, opts.toArray(new String[0]));

		if (i == null || i.intValue() < 0 || i.intValue() >= monsters.length || monsters[i.intValue()] == null)
			return 1; // XXX error!
		
		return i.intValue();
	}
	
	private void doRest() {
		// pick a card to destroy
		SelectCardOptions sco = new SelectCardOptions()
			.fromHand()
			.isPassable()
			.to(Strings.toDestroy);
		
		Card c = currentPlayer.pickACard(sco, currentPlayer.getHand()); 
		
		if (c != null) {
			broadcastEvent(new GameEvent(GameEvent.Type.TRASHES, currentPlayer).setCard(c));
			trash(c);
		}
	}
	
	void pickEffects(EffectType once, EffectType repeat) {
		ArrayList<Effect> effects = new ArrayList<Effect>();
		for (Card c: currentPlayer.getHand())
			effects.addAll(c.getEffects(once));
		
		ArrayList<Effect> repeatEffects = new ArrayList<Effect>();
		for (Card c: currentPlayer.getHand())
			repeatEffects.addAll(c.getEffects(repeat));

		while (effects.size() + repeatEffects.size() > 0) {
			ArrayList<String> opts = new ArrayList<String>();
			for (Effect e : effects)
				opts.add(e.getText());
			for (Effect e : repeatEffects)
				opts.add(e.getText());

			Integer i = currentPlayer.selectOption(Strings.chooseEffect, opts.toArray(new String[0]));
			if (i == null) break;
			
			Effect e;
			if (i < effects.size())
				e = effects.remove(i.intValue());
			else
				e = repeatEffects.get(i - effects.size());
			doEffect(e);
		}
	}
	
	void mandatoryEffects(EffectType type) {
		ArrayList<Effect> mandatoryEffects = new ArrayList<Effect>();
		for (Card c: currentPlayer.getHand())
			mandatoryEffects.addAll(c.getEffects(type));
		for (Effect e : mandatoryEffects)  // XXX choose order?
			doEffect(e);
	}

	void doEffect(Effect e) {
		e.effect(context);
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Game Mechanics [ GameInterface ]
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	@Override
	public void broadcastEvent(GameEvent event) {
		for (GameEventListener l : listeners )
			l.gameEvent(event);
	}

	public void trash(Card c) {
		table.trash(c);
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Debug and logging [ GameInterface, GameEventListener, LogReader ]
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Override
	public Random getRand() {
		return rand;
	}
	@Override
	public void info(String s) {
		logger.log(LogLevel.INFO, s);
	}
	@Override
	public void debug(String s) {
		logger.log(LogLevel.DEBUG, s);
	}

	@Override
	public void gameEvent(GameEvent event) {
		String str = "";
		if (event.getPlayer() != null) str += "<" + event.getPlayer().getName() + "> ";
		str += event.getType().toString();
		if (event.getCard() != null) str += " (" + event.getPlayer().getName() + ")";
		if (event.getStr() != null) str += ": " + event.getStr();
		
		logger.log(LogLevel.GAME, str);
	}
	
	@Override
	public void log(String s) {
		System.out.println(s);
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// State
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public class GameState {
		PlayerState[] playerStates;
		TableauState tableauState;
		Card[] hand;
		
		int turnCount;
		int currentPlayer;
		
		public GameState(Game g) {
			playerStates = new PlayerState[g.players.size()];
			int i=0;
			for (Player p : g.players) 
				playerStates[i++] = p.new PlayerState(p);
			currentPlayer = g.currentPlayerIndex;
			turnCount = g.turnCount;
			tableauState = g.table.getState();
		}
		
		public GameState setHand(Card[] hand) {
			this.hand = hand; return this;
		}
		
		public String toString() {
			String s = tableauState.toString() + "\n";
			
			s += Strings.playerStateHeader + "\n";
			int i=0;
			for (PlayerState ps : playerStates) {
				if (i++ == currentPlayer)
					s += "->";
				else
					s += "  ";
				s += ps.toString() + "\n";
			}
			if (hand != null) {
				s += "\n" + Strings.currentHand + "\n";
				for (Card c : hand) 
					s += c.getTitle() + "\n";
			}
			
			return s;
		}
	}
	
	public GameState getState() {
		return new GameState(this);
	}

}
