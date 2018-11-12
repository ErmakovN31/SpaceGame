package ru.xaero31.oskol.screen.pool;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.bossLevel.entity.Boss;
import ru.xaero31.oskol.screen.bossLevel.entity.Grenade;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class GrenadePool extends SpritesPool<Grenade> {
    private TextureAtlas atlas;
    private Rect worldBounds;
    private SpaceShip player;
    private Boss boss;
    private ExplosionPool explosionPool;

    public GrenadePool(TextureAtlas atlas, SpaceShip player, ExplosionPool explosionPool,
                       Rect worldBounds) {
        this.atlas = atlas;
        this.player = player;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    @Override
    protected Grenade newObject() {
        return new Grenade(atlas, 1, 2, 2, player, boss, explosionPool,
                           worldBounds);
    }
}
