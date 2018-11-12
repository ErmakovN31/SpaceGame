package ru.xaero31.oskol.screen.pool;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.screen.gameScreen.EnemyEmitter;
import ru.xaero31.oskol.screen.gameScreen.entity.Target;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;

public class TargetPool extends SpritesPool<Target> {
    private TextureAtlas atlas;
    private ExplosionPool explosionPool;
    private SpaceShip player;
    private Sound targeting;

    public TargetPool(TextureAtlas atlas, ExplosionPool explosionPool, SpaceShip player,
                      Sound targeting) {
        this.atlas = atlas;
        this.explosionPool = explosionPool;
        this.player = player;
        this.targeting = targeting;
    }

    public void updateSpritesForBoss(float delta, EnemyEmitter enemyEmitter) {
        for (int i = 0; i < activeObjects.size(); i++) {
            Target target = activeObjects.get(i);
            if (!target.isDestroyed()) {
                target.updateForBoss(delta, enemyEmitter);
            }
        }
    }

    @Override
    protected Target newObject() {
        return new Target(atlas.findRegion("target"), explosionPool, player, targeting);
    }
}
