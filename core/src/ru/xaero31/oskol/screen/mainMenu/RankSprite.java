package ru.xaero31.oskol.screen.mainMenu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;

public class RankSprite extends Sprite {
    public RankSprite(TextureRegion region) {
        super(region);
        setHeightProportion(150f);
        setTop(95f);
    }

    @Override
    public void resize(Rect worldBounds) {
        setLeft(60f);
    }
}
