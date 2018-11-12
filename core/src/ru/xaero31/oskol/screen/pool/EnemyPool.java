package ru.xaero31.oskol.screen.pool;

import com.badlogic.gdx.audio.Sound;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.Enemy;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class EnemyPool extends SpritesPool<Enemy> {
    private SpaceShip player;
    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private RocketPool rocketPool;
    private TargetPool targetPool;
    private Rect worldBounds;
    private Sound shoot;
    private Sound rocket;
    private Sound teleport;

    public EnemyPool(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds,
                     Sound sound, Sound rocket, RocketPool rocketPool, SpaceShip player,
                     TargetPool targetPool, Sound teleport) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        this.shoot = sound;
        this.rocket = rocket;
        this.rocketPool = rocketPool;
        this.player = player;
        this.targetPool = targetPool;
        this.teleport = teleport;
    }

    @Override
    protected Enemy newObject() {
        return new Enemy(bulletPool, explosionPool, shoot, worldBounds, rocket,
                rocketPool, player, targetPool, teleport);
    }
}
