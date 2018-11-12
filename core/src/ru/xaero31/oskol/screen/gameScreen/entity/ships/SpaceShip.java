package ru.xaero31.oskol.screen.gameScreen.entity.ships;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBar;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;

public class SpaceShip extends Ship {
    private final float SHIP_HEIGHT = 150f;
    private final float SPEED = 6f;
    private final float BOTTOM_RANGE = 10f;
    private final int INVALID_POINTER = -1;

    private float touchForShoot;
    private int movingLeftPointer;
    private int movingRightPointer;
    private byte shotNum;
    private boolean isLeftPressed;
    private boolean isRightPressed;

    private long[] soundId;
    private HPBar hpBar;

    public SpaceShip(TextureAtlas atlas, BulletPool bulletPool, Sound sound, Rect worldBounds,
                     ExplosionPool explosionPool, HPBar hpBar) {
        super(atlas.findRegion("shipSprite"), 1, 2, 2, sound, worldBounds,
                explosionPool);
        setHeightProportion(SHIP_HEIGHT);
        this.bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletV.set(0f, 1200f);
        this.bulletDamage = 1;
        this.bulletPool = bulletPool;
        this.hp = 100;
        this.hpBar = hpBar;
        hpBar.setPlayer(this);
        shotNum = 0;
        soundId = new long[2];
    }

    public void startNewGame() {
        flushDestroy();
        this.hp = 100;
        pos.x = worldBounds.pos.x;
        frame = 1;
        isLeftPressed = false;
        isRightPressed = false;
        hpBar.gettingDamage();
        setBottom(worldBounds.getBottom() + BOTTOM_RANGE);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        moveLeft();
        moveRight();
    }

    private void moveLeft() {
        if (isLeftPressed && (getLeft() - SPEED) >= worldBounds.getLeft() &&
                Math.abs(getLeft() - worldBounds.getLeft()) >= SPEED) {
            pos.add(-SPEED, 0);
        } else if (isLeftPressed && Math.abs(getLeft() - worldBounds.getLeft()) < SPEED) {
            setLeft(worldBounds.getLeft());
        }
    }

    private void moveRight() {
        if (isRightPressed && (getRight() + SPEED) <= worldBounds.getRight() &&
                Math.abs(worldBounds.getRight() - getRight()) >= SPEED) {
            pos.add(SPEED, 0);
        } else if (isRightPressed && Math.abs(worldBounds.getRight() - getRight()) < SPEED) {
            setRight(worldBounds.getRight());
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setBottom(worldBounds.getBottom() + BOTTOM_RANGE);
        touchForShoot = ((worldBounds.getWidth() / 2) / 2);
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                isLeftPressed = true;
                break;
            case Input.Keys.RIGHT:
                isRightPressed = true;
                break;
            case Input.Keys.UP:
                shoot(1f);
                break;
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                isLeftPressed = false;
                break;
            case Input.Keys.RIGHT:
                isRightPressed = false;
                break;
        }
        return false;
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (touch.x < -touchForShoot && pointer != INVALID_POINTER) {
            isLeftPressed = true;
            movingLeftPointer = pointer;
        }
        if (touch.x > touchForShoot && pointer != INVALID_POINTER) {
            isRightPressed = true;
            movingRightPointer = pointer;
        }
        if (touch.x > -touchForShoot && touch.x < touchForShoot) {
            shoot(1f);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (pointer == movingLeftPointer) {
            isLeftPressed = false;
            movingLeftPointer = INVALID_POINTER;
        }
        if (pointer == movingRightPointer) {
            isRightPressed = false;
            movingRightPointer = INVALID_POINTER;
        }
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        hp = 0;
        hpBar.gettingDamage();
    }

    protected void shoot(float vol) {
        super.shoot();
        switch (shotNum) {
            case 0:
                shoot.stop(shotNum);
                soundId[++shotNum] = shoot.play(vol);
                break;
            case 1:
                shoot.stop(shotNum);
                soundId[--shotNum] = shoot.play(vol);
        }
    }

    public boolean isBulletCollision(Rect bullet) {
        return (bullet.getRight() > getLeft() &&
                bullet.getLeft() < getRight() &&
                bullet.getBottom() < pos.y + (getHalfHeight() / 2) &&
                bullet.getTop() > getBottom()
        );
    }

    @Override
    public void damage(int damage) {
        frame = 0;
        damageAnimateTimer = 0f;
        hp -= damage;
        hpBar.gettingDamage();
        if (hp <= 0) {
            destroy();
        }
    }

    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    public boolean isRightPressed() {
        return isRightPressed;
    }
}
