package com.mehtank.thunderstone.cards.base;

import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.Context;
import com.mehtank.thunderstone.engine.Effect;

public class Barkeep extends Card {
	public Barkeep() {
		super();
		
		final Card me = this;
		
		addEffect("plusbuy", new Effect() {
			@Override
			public boolean effect(Context context) {
				context.addBuys(1);
				return true;
			}});

		addEffect("plusgold", new Effect() {
			@Override
			public boolean effect(Context context) {
				context.getGame().trash(me);
				context.addGold(2);
				return true;
			}});
	}
}
