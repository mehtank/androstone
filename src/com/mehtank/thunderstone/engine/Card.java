package com.mehtank.thunderstone.engine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import com.mehtank.thunderstone.engine.Effect.EffectType;

public class Card {
	Element xmlDefinition;
	String name;
	
	CardPile parent;

	public String getName() { return name; }
	
	public String getStr(String s) { return xmlDefinition.getAttribute(s); };
	public String getTitle() { return getStr("title"); }
	public String getText() { return getStr("text"); }
	public String getType() { return getStr("type"); }
	public String getTrait() { return getStr("trait"); }

	public Integer getInt(String s) { return XMLUtils.getIntFromXML(xmlDefinition, s); }
	public int getCount() { return getInt("count"); }
	public Integer getCost() { return getInt("cost"); }
	public Integer getGold() { return getInt("gold"); }
	public Integer getWeight() { return getInt("weight"); }
	public Integer getStrength() { return getInt("strength"); }
	public Integer getLight() { return getInt("light"); }
	public Integer getLevel() { return getInt("level"); }
	public Integer getVP() { return getInt("vp"); }
	public Integer getXP() { return getInt("xp"); }
	public Integer getHealth() { return getInt("health"); }
	
	HashMap<String, Effect> effects = new HashMap<String, Effect>();

	public Card () {}
	
	public Card (Element e) {
		setXML(e);
	}
	
	public void setXML(Element e) {
		xmlDefinition = e;
	
		name = e.getAttribute("name");		
	}
	
	public void addEffect(String name, Effect effect) {
		effects.put(name, effect);
	}

	public ArrayList<Effect> getEffects(EffectType type) {
		ArrayList<Effect> es = new ArrayList<Effect>();
		
		for (Element e : XMLUtils.getElementListByTagName(xmlDefinition, "effect")) {
			try {
				EffectType eType = EffectType.valueOf(e.getAttribute("type").toUpperCase());
				if (eType.equals(type)) {
					Effect effect = effects.get(e.getAttribute("name"));
					if (effect == null)
						continue;
					effect.setText(e.getAttribute("text"));					
					es.add(effect);
				}
			} catch (IllegalArgumentException ex) {
				// XXX ignore?
			}
		}
		
		return es;
	}
		
	public void moveTo(CardPile cardlist) {
		if (cardlist == null)
			return;
		
		if (parent != null)
			parent.remove(this);
		parent = cardlist;
		parent.add(this);
	}
	public void moveTo(CardPile cardlist, int index) {
		if (parent != null)
			parent.remove(this);
		parent = cardlist;
		parent.add(index, this);
	}
	
	public boolean equals(Card c) {
		return (name.equals(c.getName()));
	}
	public boolean equals(String s) {
		String cName = Strings.getCanonicalName(name);
		return (cName.equals(Strings.getCanonicalName(s)));
	}
	
	public Card copy() {
		Card c = null;
		try {
			c = this.getClass().getDeclaredConstructor().newInstance();
			c.setXML(xmlDefinition);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	public void addAttribute(String name, String value) {
		xmlDefinition.setAttribute(name, value);
	}
}
