package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.Sprite;

public class MessageGameOver extends Sprite {
    private final float HEIGHT = 70f;
    private final float BOTTOM_MARGIN = 50f;

    public MessageGameOver(TextureAtlas atlas) {
        super(atlas.findRegion("gameOver"));
        setHeightProportion(HEIGHT);
        setBottom(BOTTOM_MARGIN);
    }
}
