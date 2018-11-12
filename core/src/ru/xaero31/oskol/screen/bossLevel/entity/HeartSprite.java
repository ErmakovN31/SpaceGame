package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class HeartSprite extends Sprite {
    private final float DAMAGE_ANIMATE_INTERVAL = 0.1f;
    private final float HEIGHT = 60f;
    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;
    private float angle;
    private int hp = 5;

    private ExplosionPool explosionPool;
    private Rect worldBounds;
    private SpaceShip player;
    private Boss boss;

    private Vector2 v0 = new Vector2();

    public HeartSprite(TextureRegion region, int rows, int cols, int frames,
                       ExplosionPool explosionPool, SpaceShip player, Rect worldBounds) {
        super(region, rows, cols, frames);
        this.explosionPool = explosionPool;
        this.player = player;
        this.worldBounds = worldBounds;
        setHeightProportion(HEIGHT);
        super.destroy();
    }

    @Override
    public void update(float delta) {
        move(delta);
        animateTimer(delta);
    }

    private void move(float delta) {
        pos.mulAdd(v0, delta);
        v0.rotate(angle);
    }

    private void animateTimer(float delta) {
        damageAnimateTimer += delta;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL && frame == 0) {
            frame = 1;
        }
    }

    public void showHeart(Vector2 pos) {
        flushDestroy();
        this.pos.set(pos);
        v0.setAngle(0f);
        hp = 5;
    }

    public void dead() {
        boss.damage(5);
        destroy();
    }

    @Override
    public void destroy() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(getHeight(), pos);
        super.destroy();
        boss.setBottom(worldBounds.getTop());
    }

    private void damage(int damage) {
        frame = 0;
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp <= 0) {
            dead();
        }
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void checkCollisions(List<Bullet> bullets) {
        for (int b = 0; b < bullets.size(); b++) {
            if (bullets.get(b).isDestroyed() || bullets.get(b).getOwner() != player) {
                continue;
            } else {
                if (isMe(bullets.get(b).pos) && boss.getTop() <= worldBounds.getBottom()) {
                    damage(bullets.get(b).getDamage());
                    bullets.get(b).destroy();
                }
            }
        }
    }

    @Override
    public boolean isMe(Vector2 touch) {
        if (!isDestroyed()) {
            return super.isMe(touch);
        }
        return false;
    }

    public void setV0(float x, float y) {
        this.v0.set(x, y);
    }

    @Override
    public void setAngle(float angle) {
        this.angle = angle;
    }
}
