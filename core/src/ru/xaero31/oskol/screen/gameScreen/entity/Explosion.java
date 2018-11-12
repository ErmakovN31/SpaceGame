package ru.xaero31.oskol.screen.gameScreen.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.base.Sprite;

public class Explosion extends Sprite {
    private final float ANIMATE_INTERVAL = 0.017f;

    private float animateTimer;
    private Sound explosionSound;

    public Explosion(TextureRegion region, int rows, int cols, int frames, Sound sound) {
        super(region, rows, cols, frames);
        explosionSound = sound;
    }

    public void set(float height, Vector2 pos) {
        this.pos.set(pos);
        setHeightProportion(height);
        explosionSound.stop();
        explosionSound.play(0.5f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animateTimer += delta;
        if (animateTimer >= ANIMATE_INTERVAL) {
            animateTimer = 0f;
            if (++frame == regions.length) {
                destroy();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        frame = 0;
    }
}
