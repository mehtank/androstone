package com.mehtank.thunderstone.test;

import java.io.IOException;

import com.mehtank.thunderstone.api.PlayHandler;
import com.mehtank.thunderstone.comms.GameQuery;
import com.mehtank.thunderstone.engine.SelectCardOptions;

public class CliPlayer implements PlayHandler {
	String name = "Clyde";
	
	public CliPlayer(String name) {
		super();
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public GameQuery query(GameQuery p) {
		// TODO Auto-generated method stub
		System.out.println(p.gs.toString());
		System.out.println("------------------------------------------------");
		if (p.s != null) System.out.println(p.s);
		switch (p.t) {
		case GETOPTION:
			return getOption(p);
		case GETCARD:
			return getCard(p);
		}
		return null;
	}
	
	private GameQuery getOption(GameQuery p) {
		int i = 0;
		String[] opts = (String[]) p.o;
		for (String s : opts)
			System.out.println(i++ + ": " + s);
		System.out.print("?  ");
		try {
			i = Integer.parseInt(readString());
		} catch (Exception e) {
			i = -1;
		};
		return new GameQuery(p.r, null).setInteger(i);
	}

	private GameQuery getCard(GameQuery p) {
		SelectCardOptions sco = (SelectCardOptions) p.o;
		System.out.println(sco.toString());
		
		System.out.print("?  ");
		String choice = readString();
		return new GameQuery(p.r, null).setObject(new String[] {choice});
	}

    static String readString() {
        StringBuilder sb = new StringBuilder();
        try {
            do {
                int input = System.in.read();
                if (input != -1 && input != 10 && input != 13)
                    sb.append((char) input);
            } while (System.in.available() != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
