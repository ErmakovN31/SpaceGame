package ru.xaero31.oskol.screen.gameScreen.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;

public class Bullet extends Sprite {
    protected final Vector2 speed = new Vector2();

    protected Rect worldBounds;
    protected int damage;

    private Object owner;

    public Bullet() {
        regions = new TextureRegion[1];
    }

    public void set(
        Object owner,
        TextureRegion region,
        Vector2 pos0,
        Vector2 speed0,
        float height,
        Rect worldBounds,
        int damage
    ) {
        this.owner = owner;
        this.regions[0] = region;
        this.pos.set(pos0);
        this.speed.set(speed0);
        setHeightProportion(height);
        this.worldBounds = worldBounds;
        this.damage = damage;
    }

    @Override
    public void update(float delta) {
        this.pos.mulAdd(speed, delta);
        if (isOutside(worldBounds)) {
            destroy();
        }
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Object getOwner() {
        return owner;
    }
}
