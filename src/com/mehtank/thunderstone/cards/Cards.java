package com.mehtank.thunderstone.cards;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.CardPile;
import com.mehtank.thunderstone.engine.XMLUtils;

public class Cards {
	private String set = "";
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ArrayList<CardGroup> groups = new ArrayList<CardGroup>();
	
	public String getSet() {
		return set;
	}
	public Card[] getCards() {
		return cards.toArray(new Card[0]);
	}

	public Cards() {
		loadCards();
	}
	
	public Card getCard(String name) {
		for (Card c : cards)
			if (c.equals(name))
				return c.copy();
		return null;
	}
	
	public CardPile getCardPile(String name) {
		return getCardPile(name, -1);
	}
	
	public CardPile getCardPile(String name, int count) {
		Card c = getCard(name);
		if (c == null)
			return null;

		if (count < 0) count = c.getCount();
		
		CardPile p = new CardPile(name);
		for (int i = 0; i < count; i++)
			c.copy().moveTo(p);
		return p;
	}
	
	public CardPile getHeroPile(String name) {
		return getGroupPile(name, "level");
	}
	public CardPile getMonsterPile(String name) {
		return getGroupPile(name, "xp");
	}

	public CardPile getGroupPile(String name, String sort) {
		for (CardGroup g : groups) {
			if (g.equals(name)) {
				CardPile p = new CardPile(g.getTitle());
				TreeMap<Float, Card> hash = new TreeMap<Float, Card>();
				for (Card c : g.getCards())
					hash.put(c.getInt(sort) + new Random().nextFloat(), c); // XXX is this robust enough?
				for (Card c : hash.values())
					for (int i = 0; i < c.getCount() ; i++)
						c.copy().moveTo(p);
				return p;
			}
		}
		return null;
	}
	
	private void loadCards() {
		InputStream xmlStream = this.getClass().getResourceAsStream("cards.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Document doc;
		try {
			doc = dBuilder.parse(xmlStream);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Element root = doc.getDocumentElement();
		root.normalize();

		String rootname = root.getNodeName();

		if (!rootname.equals("cards"))
			return;

		set = root.getAttribute("set");

		for (Element e : XMLUtils.getElementListByTagName(root, "card"))
			makeCard(e);
		for (Element e : XMLUtils.getElementListByTagName(root, "cardgroup"))
			groups.add(new CardGroup(e, this));
	}

	private void makeCard(Element element) {
		String name = element.getAttribute("name");
		String classname = this.getClass().getPackage().getName() + "." + name;
		Class<?> c = null;
		try {
			c = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			return;
		}
		
		if (c == null)
			return;

		Card card;
		try {
			card = (Card) c.newInstance();
		} catch (InstantiationException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		}
		
		card.setXML(element);
		cards.add(card);
	}
	
}
