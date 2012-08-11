package com.mehtank.thunderstone.engine;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {
	public static Integer getIntFromXML(Element e, String attribute) {
		String s = e.getAttribute(attribute);
		if (s == null || s.isEmpty())
			return null;
		return Integer.parseInt(s);
	}

	public static Element[] getElementListByTagName(Element e, String name) {
		NodeList cardNodes = e.getElementsByTagName(name);
		ArrayList<Element> elements = new ArrayList<Element>();
		for (int i = 0; i < cardNodes.getLength(); i++) {
			Node node = cardNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				elements.add(element);
			}
		}
		return elements.toArray(new Element[0]);
	}	
}
