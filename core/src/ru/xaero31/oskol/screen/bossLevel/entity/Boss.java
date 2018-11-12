package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.Ship;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public abstract class Boss extends Ship {
    protected final String EASY = "EASY";
    protected final String NORMAL = "NORMAL";
    protected final String NIGHTMARE = "HARD";

    protected enum State {PREPARING, BATTLE, DYING, HIDING}
    protected State state;
    protected SpaceShip player;

    protected Vector2 preparingSpeed;
    protected BossHPBar bossHPBar;

    protected float shootInterval = 2f;
    protected float startYPos;
    protected float bulletMultiplier;
    protected float shootTimer;
    protected int maxHp = 100;

    private final float DYING_TIME = 4f;
    private final float EXPLOSIVE_INTERVAL = 0.2f;
    private final float EXPLOSION_HEIGHT = 100f;

    private float explosiveTimer = EXPLOSIVE_INTERVAL;
    private float dyingTimer;

    private Vector2 explosionPlace;

    public Boss(TextureRegion region, int rows, int cols, int frames, Sound sound,
                Rect worldBounds, ExplosionPool explosionPool, BulletPool bulletPool,
                String difficulty, BossHPBar bossHPBar, TextureAtlas atlas, SpaceShip player) {
        super(region, rows, cols, frames, sound, worldBounds, explosionPool);
        bulletRegion = atlas.findRegion("bulletEnemy");
        this.bulletPool = bulletPool;
        this.bossHPBar = bossHPBar;
        this.player = player;
        explosionPlace = new Vector2();
        preparingSpeed = new Vector2();
        preparingSpeed.set(0f, -100f);
        setDifficultyChanges(difficulty);
        hp = maxHp;
    }

    private void setDifficultyChanges(String difficulty) {
        switch (difficulty) {
            case EASY:
                bulletMultiplier = 0.6f;
                maxHp *= 0.5f;
                break;
            case NORMAL:
                bulletMultiplier = 1f;
                maxHp *= 0.75f;
                break;
            case NIGHTMARE:
                bulletMultiplier = 1.4f;
        }
    }

    public void ready() {
        this.state = State.PREPARING;
        setBottom(worldBounds.getTop());
    }

    protected void bossPrepare(float delta) {
        if (getBottom() > startYPos) {
            pos.mulAdd(preparingSpeed, delta);
        }
        if (state == State.PREPARING && getBottom() <= startYPos) {
            state = State.BATTLE;
            shootTimer = 0;
        }
    }

    protected void autoShoot(float delta) {
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0;
            shoot();
        }
    }

    public void checkBossCollision(List<Bullet> bullets) {
    }

    public void bossDyingProcess(float delta) {
        if (!isDestroyed()) {
            explosiveTimer += delta;
            dyingTimer += delta;
            if (dyingTimer >= DYING_TIME) {
                destroy();
            }
            if (explosiveTimer >= EXPLOSIVE_INTERVAL) {
                explosiveTimer = 0;
                for (int i = 0; i < 3; i++) {
                    Explosion explosion = explosionPool.obtain();
                    explosionPlace.set(Rnd.nextFloat(getLeft(), getRight()),
                            Rnd.nextFloat(getTop(), getBottom()));
                    explosion.set(EXPLOSION_HEIGHT, explosionPlace);
                }
            }
        }
    }

    public void dying() {
        this.state = State.DYING;
    }

    public boolean isAbleToDie() {
        return true;
    }

    public void resetBoss() {
        state = null;
        destroy();
        setBottom(worldBounds.getTop());
        flushDestroy();
        hp = maxHp;
        bossHPBar.gettingDamage();
        shootTimer = 0;
    }

    @Override
    public void damage(int damage) {
        frame = 0;
        damageAnimateTimer = 0f;
        hp -= damage;
        bossHPBar.gettingDamage();
    }

    @Override
    public void resize(Rect worldBounds) {
        if (state == null) {
            setBottom(worldBounds.getTop());
        }
    }

    public int getMaxHp() {
        return maxHp;
    }

    public float getFloatMaxHp() {
        return maxHp;
    }

    public int getSCORE() {
        return 0;
    }
}
