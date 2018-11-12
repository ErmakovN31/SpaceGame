package ru.xaero31.oskol.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreenButton extends ScaledTouchUpButton {
    public GameScreenButton(TextureRegion region, ActionListener actionListener, float pressScale) {
        super(region, actionListener, pressScale);
    }

    @Override
    public void flushDestroy() {
        super.flushDestroy();
        setTransparency(1f);
    }

    @Override
    protected void setStartAlpha(SpriteBatch batch) {}

    @Override
    protected void setEndAlpha(SpriteBatch batch, float tempColor) {}
}
