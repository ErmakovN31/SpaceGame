package ru.xaero31.oskol.screen.titles;

import com.badlogic.gdx.Game;

import ru.xaero31.oskol.base.Sprite;

public class EndNormalScreen extends MessageScreen {
    public EndNormalScreen(Game game) {
        super(game);
    }

    @Override
    protected void setMessageRegion() {
        message = new Sprite(messageAtlas.findRegion("endingNormal"));
    }

    @Override
    protected void setMessageSize() {
        message.setHeightProportion(516f);
    }
}
