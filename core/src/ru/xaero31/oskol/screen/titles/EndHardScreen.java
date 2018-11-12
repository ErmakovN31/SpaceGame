package ru.xaero31.oskol.screen.titles;

import com.badlogic.gdx.Game;

import ru.xaero31.oskol.base.Sprite;

public class EndHardScreen extends MessageScreen {
    public EndHardScreen(Game game) {
        super(game);
    }

    @Override
    protected void setMessageRegion() {
        message = new Sprite(messageAtlas.findRegion("endingHard"));
    }

    @Override
    protected void setMessageSize() {
        message.setHeightProportion(134f);
    }
}
