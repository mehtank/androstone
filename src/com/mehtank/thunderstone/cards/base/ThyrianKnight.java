package com.mehtank.thunderstone.cards.base;

import com.mehtank.thunderstone.api.PlayerInterface;
import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.Context;
import com.mehtank.thunderstone.engine.Effect;
import com.mehtank.thunderstone.engine.GameEvent;
import com.mehtank.thunderstone.engine.SelectCardOptions;
import com.mehtank.thunderstone.engine.Strings;

public class ThyrianKnight extends Card {

	public ThyrianKnight() {
		super();

		addEffect("militiagain", new Effect() {
			@Override
			public boolean effect(Context context) {
				// TODO: implement
				return false;
			}
		});
		
		addEffect("destroyfood", new Effect() {
			@Override
			public boolean effect(Context context) {
				PlayerInterface currentPlayer = context.getCurrentPlayer();
				
				SelectCardOptions sco = new SelectCardOptions()
					.fromHand()
					.hasParameter("trait", "food")
					.isPassable()
					.to(Strings.toDestroy);
				
				Card[] foodCards = sco.filter(currentPlayer.getHand());

				if (foodCards.length == 0)
					return false;
				
				Card c = currentPlayer.pickACard(sco, foodCards); 
				
				if (c != null) {
					context.getGame().broadcastEvent(new GameEvent(GameEvent.Type.TRASHES, currentPlayer).setCard(c));
					context.getGame().trash(c);
					context.addAttack(2);
					return true;
				}
				
				return false;
			}
		});
	}
}
