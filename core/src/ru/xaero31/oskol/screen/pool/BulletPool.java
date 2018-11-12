package ru.xaero31.oskol.screen.pool;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;

public class BulletPool extends SpritesPool<Bullet> {
    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
