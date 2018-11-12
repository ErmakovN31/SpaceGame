package ru.xaero31.oskol.screen.gameScreen.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.screen.gameScreen.EnemyEmitter;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class Target extends Sprite {
    private static final float SCALE_INTERVAL = 0.1f;
    private static final float SCALE_DELTA = 0.01f;
    private static final float MIN_SCALE = 0.55f;
    private static final float HEIGHT = 75f;
    private static final float EXPLOSION_HEIGHT = 100f;
    private static final int DAMAGE = 20;

    private ExplosionPool explosionPool;
    private SpaceShip player;
    private Sound targeting;

    private float scale = 1f;
    private float scaleTimer;

    public Target(TextureRegion region, ExplosionPool explosionPool, SpaceShip player,
                  Sound targeting) {
        super(region);
        scaleTimer = 0f;
        this.explosionPool = explosionPool;
        this.player = player;
        this.targeting = targeting;
        setHeightProportion(HEIGHT);
    }

    public void setTarget(float x, float y) {
        this.pos.set(x, y);
        scale = 1f;
        scaleTimer = 0f;
        setScale(scale);
        targeting.stop();
        targeting.play(0.85f);
    }

    @Override
    public void destroy() {
        super.destroy();
        Explosion explosion = explosionPool.obtain();
        explosion.set(EXPLOSION_HEIGHT, pos);
        if (explosion.isMe(player.getLeft(), pos.y) || explosion.isMe(player.getRight(), pos.y)) {
            player.damage(DAMAGE);
        }
    }

    @Override
    public void update(float delta) {
        scaleTimer += delta;
        if (scaleTimer >= SCALE_INTERVAL) {
            scaleTimer = 0;
            scale -= SCALE_DELTA;
            setScale(scale);
            if (scale <= MIN_SCALE) {
                destroy();
            }
        }
    }

    public void updateForBoss(float delta, EnemyEmitter enemyEmitter) {
        scaleTimer += delta;
        if (scaleTimer >= SCALE_INTERVAL) {
            scaleTimer = 0;
            scale -= SCALE_DELTA;
            setScale(scale);
            if (scale <= MIN_SCALE) {
                enemyEmitter.bossGenerateRocket(pos.x);
                super.destroy();
            }
        }
    }
}
