package ru.xaero31.oskol.screen.gameScreen.entity.ships;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class Ship extends Sprite {
    protected static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;
    protected final float BULLET_HEIGHT = 16f;

    protected float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;
    protected int hp;
    protected int bulletDamage;
    protected Vector2 v = new Vector2();
    protected Vector2 bulletV = new Vector2();
    protected Rect worldBounds;
    protected BulletPool bulletPool;
    protected ExplosionPool explosionPool;
    protected TextureRegion bulletRegion;
    protected Sound shoot;

    private float boomSize;

    public Ship(BulletPool bulletPool, ExplosionPool explosionPool, Sound sound, Rect worldBounds) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        shoot = sound;
    }

    public Ship(TextureRegion region, int rows, int cols, int frames, Sound sound, Rect worldBounds,
                ExplosionPool explosionPool) {
        super(region, rows, cols, frames);
        this.worldBounds = worldBounds;
        this.explosionPool = explosionPool;
        shoot = sound;
    }

    protected void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, BULLET_HEIGHT, worldBounds,
                   bulletDamage);
    }

    protected void shoot(float vol) {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, BULLET_HEIGHT, worldBounds,
                   bulletDamage);
        shoot.stop();
        shoot.play(vol);
    }

    public void boom() {
        Explosion explosion = explosionPool.obtain();
        if (getWidth() > getHeight()) {
            boomSize = getWidth();
        } else {
            boomSize = getHeight();
        }
        explosion.set(boomSize, pos);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animateTimer(delta);
    }

    protected void animateTimer(float delta) {
        damageAnimateTimer += delta;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL && frame == 0) {
            frame = 1;
        }
    }

    public void damage(int damage) {
        frame = 0;
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp <= 0) {
            destroy();
        }
    }

    @Override
    public void destroy() {
        boom();
        super.destroy();
    }

    public int getHp() {
        return hp;
    }

    public BulletPool getBulletPool() {
        return bulletPool;
    }
}
