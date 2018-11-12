package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;
import ru.xaero31.oskol.screen.pool.GrenadePool;

public class BossThree extends Boss {
    private final float ONE_TAP_INTERVAL = 0.2f;
    private final float SHOOT_VOLUME = 0.75f;
    private final float BULLET_ROTATE = 5f;
    private final int DAMAGE_PLAYER = 8;
    private final int SCORE = 30000;

    private float grenadeInterval;
    private float grenadeTimer;
    private float oneTapTimer;
    private int maxNade;
    private byte shootDone;

    private String difficulty;
    private GrenadePool grenadePool;
    private Sound grenade;
    private Vector2 startGrenadePos = new Vector2();
    private Vector2 grenadeSpeed = new Vector2();
    private Vector2 target = new Vector2();
    private Vector2 explosionPos = new Vector2();

    public BossThree(TextureRegion region, int rows, int cols, int frames, Sound sound,
                     Rect worldBounds, ExplosionPool explosionPool, BulletPool bulletPool,
                     String difficulty, BossHPBar bossHPBar, TextureAtlas atlas, SpaceShip player,
                     GrenadePool grenadePool, Sound grenade) {
        super(region, rows, cols, frames, sound, worldBounds, explosionPool, bulletPool,
              difficulty, bossHPBar, atlas, player);
        this.grenadePool = grenadePool;
        this.grenade = grenade;
        this.grenadePool.setBoss(this);
        this.bossHPBar.setBoss(this);
        setHeightProportion(450f);
        startYPos = 100f;
        bulletV.set(400f, 0f);
        bulletV.scl(bulletMultiplier);
        this.difficulty = difficulty;
        setDifficultyParameters(difficulty);
    }

    private void setDifficultyParameters(String difficulty) {
        switch (difficulty) {
            case EASY:
                grenadeSpeed.set(200f, 0f);
                grenadeInterval = 3f;
                shootInterval = 4f;
                maxNade = 3;
                break;
            case NORMAL:
                grenadeSpeed.set(300f, 0f);
                grenadeInterval = 2.5f;
                shootInterval = 3f;
                maxNade = 6;
                break;
            case NIGHTMARE:
                grenadeSpeed.set(400f, 0f);
                grenadeInterval = 2f;
                shootInterval = 2f;
                maxNade = 9;
                break;
        }
    }

    @Override
    public void ready() {
        super.ready();
        getBulletDirection();
    }

    @Override
    protected void autoShoot(float delta) {
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            oneTapTimer += delta;
            if (oneTapTimer >= ONE_TAP_INTERVAL) {
                oneTapTimer = 0;
                shootOneBullet();
                if (++shootDone >= 5) {
                    shootTimer = 0;
                    shootDone = 0;
                    getBulletDirection();
                }
            }
        }
    }

    private void shootOneBullet() {
        startGrenadePos.set(pos.x, getBottom() + getHeight() * 0.15f);
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, startGrenadePos, bulletV, BULLET_HEIGHT, worldBounds,
                   DAMAGE_PLAYER);
        bulletV.rotate(BULLET_ROTATE);
        shoot.stop();
        shoot.play(SHOOT_VOLUME);
    }

    private void getBulletDirection() {
        target.set(Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight()),
                worldBounds.getBottom());
        target.sub(pos);
        bulletV.setAngle(target.angle());
    }

    private void autoLaunchGrenade(float delta) {
        grenadeTimer += delta;
        if (grenadeTimer >= grenadeInterval && grenadePool.getActiveObjects().size() <= maxNade) {
            grenadeTimer = 0;
            startGrenadePos.set(pos.x, getBottom() + getHeight() * 0.15f);
            Grenade grenade = grenadePool.obtain();
            this.grenade.play();
            grenade.launchGrenade(startGrenadePos, grenadeSpeed);

        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        switch (state) {
            case PREPARING:
                bossPrepare(delta);
                break;
            case BATTLE:
                autoShoot(delta);
                autoLaunchGrenade(delta);
                break;
            case DYING:
                bossDyingProcess(delta);
        }
    }

    @Override
    public void boom() {
        Explosion explosion = explosionPool.obtain();
        explosionPos.set(pos.x, pos.y - 0.15f * getHeight());
        explosion.set(getHeight(), explosionPos);
    }

    @Override
    public int getSCORE() {
        return SCORE;
    }
}
