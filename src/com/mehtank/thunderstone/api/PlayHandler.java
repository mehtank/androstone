package com.mehtank.thunderstone.api;

import com.mehtank.thunderstone.comms.GameQuery;

public interface PlayHandler {

	String getName();
	GameQuery query(GameQuery p);

}
