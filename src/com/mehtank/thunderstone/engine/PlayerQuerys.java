package com.mehtank.thunderstone.engine;

import java.util.ArrayList;
import java.util.Arrays;

import com.mehtank.thunderstone.comms.GameQuery;
import com.mehtank.thunderstone.comms.GameQuery.QueryType;

public abstract class PlayerQuerys {
    
    public Card pickACard(SelectCardOptions sco, Card[] allcards) {
    	sco.exactly(1);
    	
    	Card[] cs = pickCards(sco, allcards);
    	if (cs == null)
    		return null;
    	if (cs.length > 0)
    		for (Card c : allcards)
    			if (c == cs[0])
    				return c;
    	return null;
    }
    
    public Card[] pickCards(SelectCardOptions sco, Card[] allcards) {
        GameQuery p = new GameQuery(QueryType.GETCARD, QueryType.CARD)
        	.setObject(sco);
        
        p = query(p);
        if (p == null)
        	return null;
        if (p.t != QueryType.CARD)
        	return null;
		if (p.o instanceof Card[] || p.o instanceof String[]) {
			String[] selected;
			if (p.o instanceof Card[]) {
				ArrayList<String> a = new ArrayList<String>();
				for (Card c : (Card[]) p.o) 
					a.add(c.getName());
				selected = a.toArray(new String[0]);
			} else
				selected = (String[])p.o;
			
			ArrayList<Card> ret = new ArrayList<Card>();
			ArrayList<Card> all = new ArrayList<Card>(Arrays.asList(allcards));
			for (int i = 0; i < selected.length; i++) {
				for (int j = 0; j < all.size(); j++) {
					if (all.get(j).equals(selected[i])) {
						ret.add(all.get(j));
						all.remove(j);
						break;
					}
				}
			}
			return ret.toArray(new Card[0]);
		}
        
        return null;
    }
    
    public Integer selectOption(String header, String[] s) {
    	GameQuery p = new GameQuery(QueryType.GETOPTION, QueryType.OPTION)
    					.setString(header)
    					.setObject(s);
    	p = query(p);
    	if (p == null)
    		return null;
    	if (p.t != QueryType.OPTION)
    		return null;
    	if (p.i < 0 || p.i >= s.length)
    		return null;
    	
    	return p.i;
    }
    
	public Integer[] orderCards(String header, Card[] cards) {
		GameQuery p = new GameQuery(QueryType.ORDERCARDS, QueryType.CARDORDER)
			.setString(header)
			.setObject(cards);
    
		p = query(p);
		if (p == null)
			return null;
		else
			try {
				return (Integer[])p.o;
			} catch (ClassCastException e) {
				return null;
			}
	}
	
    public boolean selectBoolean(String header, String strTrue, String strFalse) {
    	String [] s = new String [] {strTrue, strFalse};
    	Integer i = selectOption(header, s);
    	if (i == null)
    		return false;
    	
    	if (i.equals(0))
    		return true;
    	
    	return false;
    }

    public abstract GameQuery query(GameQuery p);
}