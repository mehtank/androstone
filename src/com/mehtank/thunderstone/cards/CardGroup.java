package com.mehtank.thunderstone.cards;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mehtank.thunderstone.engine.Card;
import com.mehtank.thunderstone.engine.Strings;

public class CardGroup {
	String type;
	String name;
	String title;
	ArrayList<Card> cards = new ArrayList<Card>();
	
	public CardGroup(Element e, Cards allCards) {
		type = e.getAttribute("type");		
		name = e.getAttribute("name");		
		title = e.getAttribute("title");
		
		NodeList cardNodes = e.getElementsByTagName("card");
		for (int i = 0; i < cardNodes.getLength(); i++) {
			Node node = cardNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				Card card = allCards.getCard(element.getAttribute("name"));
				card.addAttribute("type", type);
				card.addAttribute("grouptitle", title);
				cards.add(card);
			}
		}
	}
	
	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public boolean equals(String s) {
		String cName = Strings.getCanonicalName(name);
		return (cName.equals(Strings.getCanonicalName(s)));
	}
}
