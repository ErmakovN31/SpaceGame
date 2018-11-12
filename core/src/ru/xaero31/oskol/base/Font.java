package ru.xaero31.oskol.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Font extends BitmapFont {
    private enum State {NORMAL, DISAPPEARING, APPEARING}
    private State state = State.NORMAL;
    private float transparency = 1f;

    public Font(String fontFile, String imageFile) {
        super(Gdx.files.internal(fontFile), Gdx.files.internal(imageFile), false,
                false);
        getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.MipMapLinearLinear);
    }

    public void setWorldSize(float worldSize) {
        getData().setScale(1f);
        getData().setScale(worldSize/getCapHeight());
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, int halign) {
        return super.draw(batch, str, x, y, 0f, halign, false);
    }

    public void update(float delta) {
        if (state == State.DISAPPEARING) {
            transparency -= delta;
            setColor(1f, 1f, 1f, transparency);
        }
        if (state == State.APPEARING) {
            transparency += delta;
            setColor(1f, 1f, 1f, transparency);
        }
        if (transparency <= 0f && state != State.NORMAL) {
            transparency = 0f;
            state = State.NORMAL;
        }
        if (transparency >= 1f && state != State.NORMAL) {
            transparency = 1f;
            state = State.NORMAL;
        }
    }

    public void disappear() {
        state = State.DISAPPEARING;
    }

    public void appear() {
        state = State.APPEARING;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    public float getTransparency() {
        return transparency;
    }
}
