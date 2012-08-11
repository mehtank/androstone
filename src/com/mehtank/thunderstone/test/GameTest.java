package com.mehtank.thunderstone.test;

import com.mehtank.thunderstone.api.PlayHandler;
import com.mehtank.thunderstone.cards.Cards;
import com.mehtank.thunderstone.engine.Game;
import com.mehtank.thunderstone.engine.Tableau;

public class GameTest {

	public static void main(String[] args) {

		Cards baseCards = new com.mehtank.thunderstone.cards.base.Cards();
		
		Tableau t = new Tableau();
		
		t.addHeroPile(baseCards.getHeroPile("Thyrian"));
		t.addVillagePile(baseCards.getCardPile("barkeep"));
		t.addDungeonPile(baseCards.getMonsterPile("Abyssal"));
		
		PlayHandler p1 = new CliPlayer("Player D");
		PlayHandler p2 = new CliPlayer("Player T");
		Game g = new Game(t, new PlayHandler[] {p1, p2});
		g.play();
	}

}
