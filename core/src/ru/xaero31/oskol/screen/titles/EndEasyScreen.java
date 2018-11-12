package ru.xaero31.oskol.screen.titles;

import com.badlogic.gdx.Game;

import ru.xaero31.oskol.base.Sprite;

public class EndEasyScreen extends MessageScreen {
    public EndEasyScreen(Game game) {
        super(game);
    }

    @Override
    protected void setMessageRegion() {
        message = new Sprite(messageAtlas.findRegion("endingEasy"));
    }

    @Override
    protected void setMessageSize() {
        message.setHeightProportion(566f);
    }
}
