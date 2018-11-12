package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.EnemyEmitter;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.EnemyPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class BossOne extends Boss {
    private final float SHOOT_VOLUME = 0.75f;
    private final int SCORE = 10000;

    private int damage;
    private float spawnInterval;
    private float spawnTimer;
    private byte damageDone;
    private boolean missBullets;

    private EnemyEmitter enemyEmitter;
    private EnemyPool enemyPool;
    private Vector2 target;

    public BossOne(TextureRegion region, int rows, int cols, int frames, Sound sound,
                   Rect worldBounds, ExplosionPool explosionPool, BulletPool bulletPool,
                   EnemyEmitter enemyEmitter, String difficulty, EnemyPool enemyPool,
                   BossHPBar bossHPBar, TextureAtlas atlas, SpaceShip player) {
        super(region, rows, cols, frames, sound, worldBounds,
                explosionPool, bulletPool, difficulty, bossHPBar, atlas, player);
        this.enemyEmitter = enemyEmitter;
        this.enemyPool = enemyPool;
        target = new Vector2();
        damage = 6;
        setHeightProportion(170f);
        startYPos = 200f;
        bulletV.set(0f, -300f * bulletMultiplier);
        switch (difficulty) {
            case EASY:
                spawnInterval = 8f;
                break;
            case NORMAL:
                spawnInterval = 6f;
                break;
            case NIGHTMARE:
                spawnInterval = 4f;
        }
        spawnTimer = spawnInterval;
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
                spawnAdd(delta);
                break;
            case DYING:
                bossDyingProcess(delta);
        }
    }

    private void spawnAdd(float delta) {
        spawnTimer += delta;
        if ((spawnTimer >= spawnInterval || damageDone >= 5) &&
                enemyPool.getActiveObjects().size() == 0) {
            spawnTimer = 0;
            damageDone = 0;
            enemyEmitter.bossGenerateUFO();
        }
    }

    @Override
    protected void shoot() {
        for (int i = 0; i < 4; i++) {
            Bullet bullet = bulletPool.obtain();
            getBulletDirection();
            bullet.set(this, bulletRegion, pos, bulletV, BULLET_HEIGHT, worldBounds, damage);
            bulletV.setAngle(0f);
        }
        shoot.stop();
        shoot.play(SHOOT_VOLUME);
    }

    private void getBulletDirection() {
        target.set(Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight()),
                worldBounds.getBottom());
        target.sub(pos);
        bulletV.setAngle(target.angle());
    }

    @Override
    public void checkBossCollision(List<Bullet> bullets) {
        missBullets = false;
        for (int b = 0; b < bullets.size(); b++) {
            if (bullets.get(b).isDestroyed()) continue;
            if (isGotBullet(bullets.get(b).pos) && bullets.get(b).getOwner() == player) {
                if (isVulnerable() && state == State.BATTLE) {
                    damage(bullets.get(b).getDamage());
                    damageDone++;
                    bullets.get(b).destroy();
                    if (hp <= 0) {
                        return;
                    }
                }
                else if (state != State.DYING) {
                    missBullets = true;
                }
            }
        }
    }

    private boolean isVulnerable() {
        if (enemyPool.getActiveObjects().size() == 0 && damageDone < 5) {
            return true;
        }
        return false;
    }

    private boolean isGotBullet(Vector2 bullet) {
        if (bullet.x > getLeft() && bullet.x < getRight() &&
                bullet.y > (getBottom() + getHeight() * 0.2f)) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (missBullets) batch.setColor(1f, 1f, 1f, 0.5f);
        super.draw(batch);
        if (missBullets) batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void resetBoss() {
        super.resetBoss();
        spawnTimer = 0;
    }

    @Override
    public int getSCORE() {
        return SCORE;
    }
}
