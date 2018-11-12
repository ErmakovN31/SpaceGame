package ru.xaero31.oskol.screen.pool;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.base.SpritesPool;
import ru.xaero31.oskol.screen.gameScreen.entity.Explosion;

public class ExplosionPool extends SpritesPool<Explosion> {
    private TextureRegion region;
    private Sound explosionSound;

    public ExplosionPool(TextureAtlas atlas, Sound sound) {
        region = atlas.findRegion("explosion");
        explosionSound = sound;
    }

    @Override
    protected Explosion newObject() {
        return new Explosion(region, 9, 9, 74, explosionSound);
    }
}
