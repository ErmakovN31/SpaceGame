package ru.xaero31.oskol.screen.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class Background extends Sprite {
    private final float SPEED = 10f;
    private final float SCALE = 1.2f;

    private SpaceShip player;
    private Rect worldBounds;

    public Background(TextureRegion region) {
        super(region);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (player != null) {
            if (player.isLeftPressed() && getRight() > worldBounds.getRight()) {
                pos.x += SPEED * delta;
            }
            if (player.isRightPressed() && getLeft() < worldBounds.getLeft()) {
                pos.x -= SPEED * delta;
            }
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(worldBounds.getHeight() * SCALE);
        pos.set(worldBounds.pos);
        this.worldBounds = worldBounds;
    }

    public void setPlayer(SpaceShip player) {
        this.player = player;
    }
}
