package ru.xaero31.oskol.screen.gameScreen.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class Rocket extends Bullet {
    private static final int SCORE = 50;

    private SpaceShip player;
    private ExplosionPool explosionPool;
    private Vector2 direction = new Vector2();

    public Rocket(TextureAtlas atlas, SpaceShip player, ExplosionPool explosionPool) {
        this.player = player;
        this.explosionPool = explosionPool;
        regions = new TextureRegion[1];
        regions[0] = atlas.findRegion("rocket");
        setHeightProportion(20f);
        this.damage = 20;
    }

    public void set(
            Vector2 pos0,
            Rect worldBounds
    ) {
        this.pos.set(pos0);
        this.worldBounds = worldBounds;
        speed.set(120f, 0f);
    }

    public void setForBoss(Vector2 pos0, Rect worldBounds, Vector2 bulletV, int damage) {
        this.pos.set(pos0);
        this.worldBounds = worldBounds;
        this.damage = damage;
        speed.set(bulletV);
    }

    @Override
    public void update(float delta) {
        direction.set(player.pos);
        direction.sub(pos);
        speed.rotate(direction.angle() - speed.angle());
        rotate(direction.angle() - angle);
        this.pos.mulAdd(speed, delta);
    }

    @Override
    public void destroy() {
        boom();
        super.destroy();
    }

    public void boom() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(getWidth(), pos);
    }

    public boolean isBulletCollision(Rect bullet) {
        if ((angle < 30 && angle > -30) || (angle > 150 && angle < 210)) {
            return (bullet.getRight() > getLeft() + (getHalfWidth() * 0.3f) &&
                    bullet.getLeft() < getRight() - (getHalfWidth() * 0.3f) &&
                    bullet.getBottom() < getTop() &&
                    bullet.getTop() > getBottom());
        } else {
            return (bullet.getRight() > getLeft() &&
                    bullet.getLeft() < getRight() &&
                    bullet.getBottom() < getTop() &&
                    bullet.getTop() > pos.y - (getHalfWidth() * 0.75f));
        }
    }

    public int getSCORE() {
        return SCORE;
    }
}
