package ru.xaero31.oskol.screen.titles;

import com.badlogic.gdx.Game;

import ru.xaero31.oskol.base.Sprite;

public class OpenScreen extends MessageScreen {
    public OpenScreen(Game game) {
        super(game);
    }

    @Override
    protected void setMessageRegion() {
        message = new Sprite(messageAtlas.findRegion("entry"));
    }

    @Override
    protected void setMessageSize() {
        message.setHeightProportion(320f);
    }
}
