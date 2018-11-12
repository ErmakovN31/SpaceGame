package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.EnemyEmitter;
import ru.xaero31.oskol.screen.gameScreen.entity.Rocket;
import ru.xaero31.oskol.screen.gameScreen.entity.Target;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;
import ru.xaero31.oskol.screen.pool.RocketPool;
import ru.xaero31.oskol.screen.pool.TargetPool;

public class BossTwo extends Boss {
    private final float SHOT_VOLUME = 0.35f;
    private final float HIDE_INTERVAL = 15f;
    private final float BACK_INTERVAL = 10f;
    private final float AIR_ATTACK_INTERVAL = 2f;
    private final float CHECK_HP = 0.5f;
    private final int ROCKET_DAMAGE = 10;
    private final int SCORE = 20000;
    private final Vector2 hideSpeed = new Vector2(0f, -2500f);

    private String difficulty;
    private float rocketSpeedMultiplier;
    private float rocketInterval;
    private float rocketTimer;
    private float hideTimer;
    private float airAttackTimer;
    private int numberOfRockets;
    private boolean ableToDie = false;

    private TargetPool targetPool;
    private RocketPool rocketPool;
    private HeartSprite heartSprite;
    private EnemyEmitter enemyEmitter;
    private Vector2 leftRocket = new Vector2();
    private Vector2 rightRocket = new Vector2();
    private Vector2 nose = new Vector2();

    public BossTwo(TextureRegion region, int rows, int cols, int frames, Sound sound,
                   Rect worldBounds, ExplosionPool explosionPool, BulletPool bulletPool,
                   String difficulty, BossHPBar bossHPBar, TextureAtlas atlas, SpaceShip player,
                   TargetPool targetPool, RocketPool rocketPool, EnemyEmitter enemyEmitter,
                   HeartSprite heartSprite) {
        super(region, rows, cols, frames, sound, worldBounds, explosionPool, bulletPool, difficulty,
              bossHPBar, atlas, player);
        this.targetPool = targetPool;
        this.rocketPool = rocketPool;
        this.enemyEmitter = enemyEmitter;
        this.heartSprite = heartSprite;
        this.heartSprite.setBoss(this);
        this.bossHPBar.setBoss(this);
        setHeightProportion(450f);
        startYPos = 100f;
        this.difficulty = difficulty;
        setDifficultyParameters(difficulty);
        bulletV.set(120f, 0f).scl(rocketSpeedMultiplier);
    }

    private void setDifficultyParameters(String difficulty) {
        switch (difficulty) {
            case EASY:
                rocketSpeedMultiplier = 2f;
                rocketInterval = 3f;
                numberOfRockets = 1;
                heartSprite.setV0(180f, 0f);
                heartSprite.setAngle(3.5f);
                break;
            case NORMAL:
                rocketSpeedMultiplier = 2.5f;
                rocketInterval = 2f;
                numberOfRockets = 1;
                heartSprite.setV0(240f, 0f);
                heartSprite.setAngle(4f);
                break;
            case NIGHTMARE:
                rocketSpeedMultiplier = 3f;
                rocketInterval = 3.5f;
                numberOfRockets = 2;
                heartSprite.setV0(300f, 0f);
                heartSprite.setAngle(4.5f);
                break;
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
                checkHP();
                autoShoot(delta);
                autoAirPunch(delta);
                goToHide(delta);
                break;
            case HIDING:
                hideAndShowProcess(delta);
                airAttack(delta);
                break;
            case DYING:
                bossDyingProcess(delta);
        }
    }

    private void checkHP() {
        if (hideTimer >= CHECK_HP) {
            if (hp <= 0) {
                ableToDie = true;
            }
        }
    }

    @Override
    protected void shoot() {
        leftRocket.set(getLeft() + 0.05f * getWidth(), getTop() - 0.25f * getHeight());
        rightRocket.set(getRight() - 0.05f * getWidth(), getTop() - 0.25f * getHeight());
        nose.set(pos.x, getBottom() + getHeight() * 0.1f);
        Rocket rocket = rocketPool.obtain();
        rocket.setForBoss(leftRocket, worldBounds, bulletV, ROCKET_DAMAGE);
        Rocket rocket1 = rocketPool.obtain();
        rocket1.setForBoss(rightRocket, worldBounds, bulletV, ROCKET_DAMAGE);
        if (difficulty.equals(NIGHTMARE)) {
            Rocket rocket2 = rocketPool.obtain();
            rocket2.setForBoss(nose, worldBounds, bulletV, ROCKET_DAMAGE);
        }
        shoot.stop();
        shoot.play(SHOT_VOLUME);
    }

    private void autoAirPunch(float delta) {
        rocketTimer += delta;
        if (rocketTimer >= rocketInterval) {
            rocketTimer = 0;
            for (int i = 0; i < numberOfRockets; i++) {
                randomTarget();
            }
        }
    }

    private void randomTarget() {
        Target target = targetPool.obtain();
        target.setTarget(Rnd.nextFloat(worldBounds.getLeft() + player.getHalfWidth(),
                         worldBounds.getRight() - player.getHalfWidth()), player.pos.y);
    }

    private void goToHide(float delta) {
        hideTimer += delta;
        if (hideTimer >= HIDE_INTERVAL) {
            hideTimer = 0;
            heartSprite.showHeart(pos);
            state = State.HIDING;
            airAttackTimer = 0;
            shoot.stop();
            shoot.play(SHOT_VOLUME);
        }
    }

    private void hideAndShowProcess(float delta) {
        hideTimerTick(delta);
        if (getTop() >= worldBounds.getBottom() && !heartSprite.isDestroyed()) {
            pos.mulAdd(hideSpeed, delta);
        }
        if (heartSprite.isDestroyed()) {
            if (getBottom() >= startYPos) {
                pos.mulAdd(hideSpeed, delta);
            } else {
                shoot.play(SHOT_VOLUME);
                state = State.BATTLE;
            }
        }
    }

    private void hideTimerTick(float delta) {
        hideTimer += delta;
        if (hideTimer >= BACK_INTERVAL) {
            hideTimer = 0;
            heartSprite.destroy();
        }
    }

    private void airAttack(float delta) {
        if (airAttackTimer >= 0) {
            airAttackTimer += delta;
        }
        if (airAttackTimer >= AIR_ATTACK_INTERVAL) {
            airAttackTimer = -1f;
            float clearPlace = Rnd.nextFloat(worldBounds.getLeft() + player.getWidth() * 0.75f,
                                             worldBounds.getRight() - player.getWidth() * 0.75f);
            airAttackTarget(clearPlace);
        }
    }

    private void airAttackTarget(float clearPlace) {
        for (int i = 0; i < 10; i++) {
            Target target = targetPool.obtain();
            target.setTarget(Rnd.nextFloat(worldBounds.getLeft() + player.getHalfWidth(),
                                           clearPlace - player.getWidth() * 1.5f), player.pos.y);
        }
        for (int i = 0; i < 10; i++) {
            Target target = targetPool.obtain();
            target.setTarget(Rnd.nextFloat(clearPlace + player.getWidth() * 1.5f,
                                           worldBounds.getRight() - player.getHalfWidth()),
                                           player.pos.y);
        }
    }

    @Override
    public void damage(int damage) {
        hp -= damage;
        bossHPBar.gettingDamage();
    }

    @Override
    protected void animateTimer(float delta) {}

    @Override
    public void resetBoss() {
        super.resetBoss();
        ableToDie = false;
    }

    @Override
    public int getSCORE() {
        return SCORE;
    }


    @Override
    public boolean isAbleToDie() {
        return ableToDie;
    }
}
