package com.mehtank.thunderstone.api;

import com.mehtank.thunderstone.engine.GameEvent;

public interface GameEventListener {
    public void gameEvent(GameEvent event);
}
