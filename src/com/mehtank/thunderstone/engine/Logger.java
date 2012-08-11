package com.mehtank.thunderstone.engine;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import com.mehtank.thunderstone.api.LogReader;

public class Logger {

	public enum LogLevel {GAME, INFO, DEBUG};
	
	private static final String TAB = "  ";
	private int indentLevel = 0;
	
	private Map<LogLevel, String> bullet = new EnumMap<LogLevel, String>(LogLevel.class);
	private Map<LogLevel, ArrayList<LogReader>> readers = new EnumMap<LogLevel, ArrayList<LogReader>>(LogLevel.class);

	public Logger() {
		bullet.put(LogLevel.GAME, "* ");
		bullet.put(LogLevel.INFO, "> ");
		bullet.put(LogLevel.DEBUG, "! ");
	}
	
	public void addReader(LogLevel l, LogReader r) {
		if (readers.get(l) == null)
			readers.put(l, new ArrayList<LogReader>());
		readers.get(l).add(r);
	}
	
	public void indent() {
		indentLevel++;
	}
	public void unindent() {
		if (indentLevel > 0)
			indentLevel--;
	}
	
	public void log(LogLevel l, String s) {
		for (LogReader r : readers.get(l))
			r.log(bullet.get(l) + tabify(s));
	}
	
	private String tabify(String s) {
		String r = "";
		for (int i = 0; i < indentLevel; i++) {
			r += TAB;
		}
		return r+s;
	}

}
