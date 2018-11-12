package ru.xaero31.oskol.screen.pool;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.screen.gameScreen.entity.Rocket;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class RocketPool extends SpritesPool<Rocket> {
    private TextureAtlas atlas;
    private SpaceShip player;
    private ExplosionPool explosionPool;

    public RocketPool(TextureAtlas atlas, SpaceShip player, ExplosionPool explosionPool) {
        this.atlas = atlas;
        this.player = player;
        this.explosionPool = explosionPool;
    }

    @Override
    protected Rocket newObject() {
        return new Rocket(atlas, player, explosionPool);
    }
}
