package ru.xaero31.oskol.screen.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class Star extends Sprite {
    private final float SHINING_INTERVAL = 0.1f;
    private final float SPEED = 30f;

    private float shiningTimer;
    private float posX;
    private float posY;
    private Rect worldBounds;
    private SpaceShip player;

    public Star(TextureRegion region, int rows, int cols, int frames, Rect worldBounds,
                SpaceShip player) {
        super(region, rows, cols, frames);
        setHeightProportion(15f);
        posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
        posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
        pos.set(posX, posY);
        shiningTimer = Rnd.nextFloat(0.01f, 0.2f);
        this.worldBounds = worldBounds;
        this.player = player;
    }

    public Star(TextureRegion region, int rows, int cols, int frames, Rect worldBounds) {
        super(region, rows, cols, frames);
        setHeightProportion(15f);
        posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
        posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
        pos.set(posX, posY);
        shiningTimer = Rnd.nextFloat(0.01f, 0.2f);
        this.worldBounds = worldBounds;
    }

    @Override
    public void update(float delta) {
        shineStar(delta);
        moveStar(delta);
    }

    private void shineStar(float delta) {
        shiningTimer += delta;
        if (shiningTimer >= SHINING_INTERVAL) {
            shiningTimer = 0f;
            if (++frame == regions.length) {
                frame = 0;
                posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
                posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
                pos.set(posX, posY);
            }
        }
    }

    private void moveStar(float delta) {
        if (player != null) {
            if (player.isLeftPressed()) {
                pos.x += SPEED * delta;
            }
            if (player.isRightPressed()) {
                pos.x -= SPEED * delta;
            }
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        float posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
        float posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
        pos.set(posX, posY);
    }
}
