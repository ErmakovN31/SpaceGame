package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;

public class HPBarBorder extends Sprite {
    private final float HEIGHT = 406f;

    public HPBarBorder(TextureRegion region) {
        super(region);
        setHeight(HEIGHT);
        setWidth(22f);
    }
}
