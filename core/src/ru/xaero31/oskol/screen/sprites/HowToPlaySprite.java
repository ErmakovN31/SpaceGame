package ru.xaero31.oskol.screen.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;

public class HowToPlaySprite extends Sprite {
    public HowToPlaySprite(TextureRegion region) {
        super(region);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        pos.set(worldBounds.pos);
        setHeight(worldBounds.getHeight());
        setWidth(worldBounds.getWidth());
    }
}
