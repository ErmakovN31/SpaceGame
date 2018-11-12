package ru.xaero31.oskol.screen.gameScreen.entity.ships;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Rocket;
import ru.xaero31.oskol.screen.gameScreen.entity.Target;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;
import ru.xaero31.oskol.screen.pool.RocketPool;
import ru.xaero31.oskol.screen.pool.TargetPool;

public class Enemy extends Ship {
    private final float ANIMATE_INTERVAL = 0.08f;

    private enum State {PREPARING, BATTLE, TELEPORTING_BEFORE, TELEPORTING_AFTER}
    private State state;

    private SpaceShip player;

    private Vector2 preparingSpeed = new Vector2(0f, -200f);
    private Vector2 bulletDirection = new Vector2();
    private Sound rocket;
    private Sound teleport;

    private RocketPool rocketPool;
    private TargetPool targetPool;

    private float reloadInterval;
    private float reloadTimer;
    private float moveInterval;
    private float moveTimer;
    private float animateTimer;
    private float battleYPos;
    private float reloadIntervalMultiplier;
    private float enemySpeedMultiplier;
    private float moveIntervalMultiplier;
    private int score;
    private byte enemyType;
    private byte battleModeTick = 0;
    private byte shots = 0;
    private boolean invulnerable;

    public Enemy(BulletPool bulletPool, ExplosionPool explosionPool, Sound sound,
                 Rect worldBounds, Sound rocket, RocketPool rocketPool, SpaceShip player,
                 TargetPool targetPool, Sound teleport) {
        super(bulletPool, explosionPool, sound, worldBounds);
        this.rocket = rocket;
        this.rocketPool = rocketPool;
        this.player = player;
        this.targetPool = targetPool;
        this.teleport = teleport;

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        preparingToBattle(delta);
        battleMode(delta);
        autoShoot(delta);
    }

    //Блок передвижения
    private void preparingToBattle(float delta) {
        switch (enemyType) {
            case 5:
                alienPreparingTeleport(delta);
                break;
            default:
                defaultPreparingToBattle(delta);
        }
    }

    private void defaultPreparingToBattle(float delta) {
        if (state == State.PREPARING) {
            pos.mulAdd(preparingSpeed, delta);
            if (getBottom() <= battleYPos) {
                battleModeTick = 0;
                state = State.BATTLE;
                invulnerable = false;
                if (enemyType == 4) {
                    rocket.stop();
                    rocket.play(0.45f);
                }
            }
        }
    }

    private void alienPreparingTeleport(float delta) {
        if (state == State.PREPARING) {
            animateTimer += delta;
            if (animateTimer >= ANIMATE_INTERVAL && --frame > 1) {
                animateTimer = 0f;
            } else if (frame == 1) {
                state = State.BATTLE;
                invulnerable = false;
                moveTimer = 0;
            }
        }
    }

    private void battleMode(float delta) {
        if (state == State.BATTLE) {
            switch (enemyType) {
                case 1:
                    ufoBattleMode(delta);
                    break;
                case 2:
                    migBattleMode(delta);
                    break;
                case 3:
                    crabBattleMode(delta);
                    break;
                case 4:
                    rocketBattleMode(delta);
                    break;
                case 5:
                    alienBattleMode(delta);
                    break;
                case 6:
                    tentacleBattleMode(delta);
                    break;
                case 7:
                    smallBattleMode(delta);
                    break;
                case 8:
                    wideBattleMode(delta);
                    break;
            }
        }
        alienTeleporting(delta);
    }

    private void alienBattleMode(float delta) {
        moveTimer += delta;
        if (moveTimer >= moveInterval) {
            moveTimer = 0;
            animateTimer = 0;
            state = State.TELEPORTING_BEFORE;
            teleport.stop();
            teleport.play(0.45f);
            invulnerable = true;
        }
    }

    private void alienTeleporting(float delta) {
        switch (state) {
            case TELEPORTING_BEFORE:
                animateTimer += delta;
                if (animateTimer >= ANIMATE_INTERVAL) {
                    switch (frame) {
                        case 7:
                            switchTeleportState();
                            break;
                        default:
                            animateTimer = 0;
                            frame++;
                            return;
                    }
                }
                break;
            case TELEPORTING_AFTER:
                animateTimer += delta;
                if (animateTimer >= ANIMATE_INTERVAL && --frame > 0) {
                    animateTimer = 0;
                    if (frame == 1) {
                        state = State.BATTLE;
                        invulnerable = false;
                    }
                }
        }
    }

    private void switchTeleportState() {
        animateTimer = 0;
        pos.x = Rnd.nextFloat(worldBounds.getLeft() + halfWidth,
                worldBounds.getRight() - halfWidth);
        setBottom(Rnd.nextFloat(0f, worldBounds.getTop() - getHeight()));
        state = State.TELEPORTING_AFTER;
    }

    private void ufoBattleMode(float delta) {
        if (getLeft() > worldBounds.getLeft() && getRight() < worldBounds.getRight() &&
                getTop() < worldBounds.getTop()) {
            pos.mulAdd(v, delta);
        } else if (getLeft() + v.x > worldBounds.getLeft() && getRight() + v.x <
                worldBounds.getRight() && getTop() + v.y < worldBounds.getTop()) {
            pos.mulAdd(v, delta);
        }
        v.rotate(1.5f);
    }

    private void migBattleMode(float delta) {
        moveTimer += delta;
        migMove(delta);
        migSwitch(delta);
    }

    private void migMove(float delta) {
        if (getLeft() > worldBounds.getLeft() && getRight() < worldBounds.getRight()) {
            pos.x += (v.x * delta);
        }
    }

    private void migSwitch(float delta) {
        if (moveTimer >= moveInterval) {
            moveTimer = 0f;
            v.rotate(90);
            pos.x += (v.x * delta);
        }
    }

    private void crabBattleMode(float delta) {
        moveTimer += delta;
        crabMove(delta);
        crabSwitch(delta);
    }

    private void crabMove(float delta) {
        if (getLeft() > worldBounds.getLeft() && getRight() < worldBounds.getRight() &&
                getTop() < worldBounds.getTop()) {
            pos.mulAdd(v, delta);
        } else if (getLeft() + v.x > worldBounds.getLeft() && getRight() + v.x <
                worldBounds.getRight() && getTop() + v.y < worldBounds.getTop()) {
            pos.mulAdd(v, delta);
        }
    }

    private void crabSwitch(float delta) {
        if (moveTimer >= moveInterval) {
            moveTimer = 0f;
            switch (battleModeTick++) {
                case 0:
                    crabBattleStep(delta, 180);
                    break;
                case 1:
                    crabBattleStep(delta, -60);
                    break;
                case 2:
                    crabBattleStep(delta, 180);
                    break;
                case 3:
                    crabBattleStep(delta, 60);
                    battleModeTick = 0;
            }
        }
    }

    private void crabBattleStep(float delta, int degree) {
        v.rotate(degree);
    }

    private void rocketBattleMode(float delta) {
        pos.mulAdd(v, delta);
        if (getBottom() <= worldBounds.getBottom()) {
            destroy();
        }
    }

    private void smallBattleMode(float delta) {
        moveTimer += delta;
        if (getLeft() > worldBounds.getLeft() && getRight() < worldBounds.getRight()) {
            pos.x += (v.x * delta);
        }
        if (moveTimer >= moveInterval) {
            v.rotate(90);
            moveTimer = 0;
            shots = 0;
            pos.x += (v.x * delta);
        }
    }

    private void tentacleBattleMode(float delta) {
        if (moveTimer >= moveInterval) {
            moveTimer = 0;
            v.set(Rnd.nextFloat(worldBounds.getLeft() + halfWidth, worldBounds.getRight() -
                    halfWidth), Rnd.nextFloat(worldBounds.pos.y + halfHeight,
                    worldBounds.getTop() - halfHeight));
            v.sub(pos);
            tentacleStickOff();
        }
        moveTimer += delta;
        if (getLeft() >= worldBounds.getLeft() && getRight() <= worldBounds.getRight() &&
                getTop() <= worldBounds.getTop() && getBottom() >= worldBounds.pos.y) {
            pos.mulAdd(v, delta * 0.2f);
        }
    }

    private void tentacleStickOff() {
        if (getTop() > worldBounds.getTop()) {
            setTop(worldBounds.getTop());
        }
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
        }
        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
        }
        if (getBottom() < worldBounds.pos.y) {
            setBottom(worldBounds.pos.y);
        }
    }

    private void wideBattleMode(float delta) {
        pos.mulAdd(v, delta);
        if (getLeft() < worldBounds.getLeft() || getRight() > worldBounds.getRight()) {
            v.rotate(180);
        }
    }

    private void autoShoot(float delta) {
        reloadTimer += delta;
        if (reloadTimer >= reloadInterval && reloadInterval != 0f && state == State.BATTLE) {
            reloadTimer = 0f;
            shoot(0.75f);
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            Vector2 bulletSpeed,
            int bulletDamage,
            float reloadInterval,
            float height,
            int hp,
            float battleYPos,
            byte enemyType,
            float moveInterval,
            int score
    ) {
        this.regions = regions;
        this.bulletRegion = bulletRegion;
        this.bulletV.set(bulletSpeed);
        this.bulletDamage = bulletDamage;
        this.reloadInterval = reloadInterval * reloadIntervalMultiplier;
        this.hp = hp;
        this.battleYPos = battleYPos;
        this.moveInterval = moveInterval;
        this.invulnerable = true;
        this.enemyType = enemyType;
        this.score = score;
        state = State.PREPARING;
        setHeightProportion(height);
        v.set(v0);
        v.scl(enemySpeedMultiplier);
        if (enemyType != 4) {
            v.setAngle(0);
        }
        if (enemyType == 3) {
            v.setAngle(30);
        }
        if (enemyType == 6) {
            moveTimer = moveInterval;
        }
        if (enemyType == 5) {
            teleport.stop();
            teleport.play(0.45f);
            this.moveInterval *= moveIntervalMultiplier;
        }
    }

    public int getScore() {
        return score;
    }

    public void setReloadIntervalMultiplier(float reloadIntervalMultiplier) {
        this.reloadIntervalMultiplier = reloadIntervalMultiplier;
    }

    public void setMoveIntervalMultiplier(float moveIntervalMultiplier) {
        this.moveIntervalMultiplier = moveIntervalMultiplier;
    }

    public void setEnemySpeedMultiplier(float enemySpeedMultiplier) {
        this.enemySpeedMultiplier = enemySpeedMultiplier;
    }

    public void setFrame(int frame) {
        if (frame >= 0 && frame < regions.length) {
            this.frame = frame;
        }
    }

    //Блок стрельбы
    protected void shoot(float vol) {
        switch (enemyType) {
            case 1:
                super.shoot(vol);
                shootUFO();
                break;
            case 5:
                shootAlien();
                break;
            case 8:
                shootWide();
                break;
            case 7:
                if (shots++ < 5) {
                    super.shoot(vol);
                }
                break;
            case 6:
                shootTentacle();
            default:
                super.shoot(vol);
        }
    }

    private void shootWide() {
        Target target = targetPool.obtain();
        target.setTarget(Rnd.nextFloat(worldBounds.getLeft() + target.getHalfWidth(),
                worldBounds.getRight() - target.getHalfWidth()), player.pos.y);
    }

    private void shootTentacle() {
        bulletDirection.set(player.pos);
        bulletDirection.sub(pos);
        bulletV.setAngle(bulletDirection.angle());
    }

    private void shootUFO() {
        Bullet bulletRight = bulletPool.obtain();
        bulletRight.set(this, bulletRegion, pos, bulletV.cpy().rotate(30), BULLET_HEIGHT,
                worldBounds, bulletDamage);
        Bullet bulletLeft = bulletPool.obtain();
        bulletLeft.set(this, bulletRegion, pos, bulletV.cpy().rotate(-30), BULLET_HEIGHT,
                worldBounds, bulletDamage);
    }

    private void shootAlien() {
        Rocket rocket = rocketPool.obtain();
        rocket.set(pos, worldBounds);
        this.rocket.stop();
        this.rocket.play(0.35f);
    }

    public boolean isBulletCollision(Rect bullet) {
        return (bullet.getRight() > getLeft() &&
                bullet.getLeft() < getRight() &&
                bullet.getBottom() < getTop() &&
                bullet.getTop() > pos.y
                );
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }
}
