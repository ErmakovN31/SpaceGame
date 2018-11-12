package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class HPBar extends Sprite {
    protected final float BOTTOM_LEFT_MARGIN = 8f;
    protected final float HEIGHT = 397f;
    protected Rect worldBounds;

    private SpaceShip player;
    private final int maxHp = 100;

    public HPBar(TextureRegion region, Rect worldBounds) {
        super(region);
        setHeight(HEIGHT);
        setWidth(15f);
        this.worldBounds = worldBounds;
    }

    public void setPlayer(SpaceShip player) {
        this.player = player;
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeight(HEIGHT * player.getHp() / maxHp);
        setBottom(worldBounds.getBottom() + BOTTOM_LEFT_MARGIN);
        setLeft(worldBounds.getLeft() + BOTTOM_LEFT_MARGIN);
    }

    public void gettingDamage() {
        setHeight(HEIGHT * player.getHp() / maxHp);
        setBottom(worldBounds.getBottom() + BOTTOM_LEFT_MARGIN);
        setLeft(worldBounds.getLeft() + BOTTOM_LEFT_MARGIN);
    }
}
