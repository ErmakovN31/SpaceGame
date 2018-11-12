package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class Grenade extends Sprite {
    private static final float BODY_ACCURACY = 0.1f;
    private static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;
    private static final float HEIGHT = 50f;
    private static final float ROTATE_ANGLE = 16f;
    private static final float MIN_GRENADE_ANGLE = -30f;
    private static final float MAX_GRENADE_ANGLE = -150f;
    private static final float BOSS_DAMAGED_PART = 0.6f;
    private static final int BOSS_DAMAGE = 5;
    private static final int PLAYER_DAMAGE = 15;

    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;
    private int hp;

    private Vector2 v0 = new Vector2();
    private Rect worldBounds;
    private SpaceShip player;
    private Boss boss;
    private ExplosionPool explosionPool;
    private BulletPool bulletPool;

    public Grenade(TextureAtlas atlas, int rows, int cols, int frames, SpaceShip player, Boss boss,
                   ExplosionPool explosionPool, Rect worldBounds) {
        super(atlas.findRegion("grenade"), rows, cols, frames);
        this.player = player;
        this.boss = boss;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        this.bulletPool = boss.getBulletPool();
        setHeightProportion(HEIGHT);
        hp = 3;
    }

    public void launchGrenade(Vector2 pos, Vector2 speed) {
        this.pos.set(pos);
        v0.set(speed);
        v0.setAngle(Rnd.nextFloat(MIN_GRENADE_ANGLE, MAX_GRENADE_ANGLE));
        hp = 3;
    }

    @Override
    public void update(float delta) {
        move(delta);
        animateTimer(delta);
        checkBodyCollisions();
        checkHitCollisions(bulletPool.getActiveObjects());
    }

    private void move(float delta) {
        rotate(ROTATE_ANGLE);
        pos.mulAdd(v0, delta);
        if (getLeft() > worldBounds.getRight()) {
            setRight(worldBounds.getLeft());
        }
        if (getRight() < worldBounds.getLeft()) {
            setLeft(worldBounds.getRight());
        }
        if (getTop() < worldBounds.getBottom()) {
            setBottom(worldBounds.getTop());
        }
    }

    private void animateTimer(float delta) {
        damageAnimateTimer += delta;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL && frame == 0) {
            frame = 1;
        }
    }

    private void damage(int damage) {
        frame = 0;
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp <= 0) {
            destroy();
        }
    }

    public void checkBodyCollisions() {
        if (pos.y < player.getTop() - BODY_ACCURACY * player.getHeight()
            && pos.x > player.getLeft() + BODY_ACCURACY * player.getWidth()
            && pos.x < player.getRight() - BODY_ACCURACY * player.getWidth()) {
            destroy();
            player.damage(PLAYER_DAMAGE);
        }
    }

    public void checkHitCollisions(List<Bullet> bullets) {
        for (int b = 0; b < bullets.size(); b++) {
            if (bullets.get(b).isDestroyed() || bullets.get(b).getOwner() != player) {
                continue;
            } else {
                if (isMe(bullets.get(b).pos)) {
                    damage(bullets.get(b).getDamage());
                    bullets.get(b).destroy();
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (pos.y > boss.getBottom() && pos.y
            < (boss.getBottom() + boss.getHeight() * BOSS_DAMAGED_PART)
            && pos.x > boss.getLeft() && pos.x < boss.getRight()) {
            boss.damage(BOSS_DAMAGE);
        }
        Explosion explosion = explosionPool.obtain();
        explosion.set(getHeight() * 2f, pos);
        super.destroy();
    }
}
