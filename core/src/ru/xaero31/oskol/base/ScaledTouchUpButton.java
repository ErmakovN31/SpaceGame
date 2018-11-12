package ru.xaero31.oskol.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class ScaledTouchUpButton extends Sprite {
    private enum State {NORMAL, DISAPPEARING, APPEARING, NEW_SCREEN}
    private State state = State.NORMAL;

    private ActionListener actionListener;
    private ArrayList<ScaledTouchUpButton> toAppear = new ArrayList<>();
    private Font font;

    private int pointer;
    private float tempColor;
    private float transparency = 1f;
    private float pressScale;
    private boolean isPressed;

    public ScaledTouchUpButton(TextureRegion region, ActionListener actionListener,
                               float pressScale) {
        super(region);
        this.pressScale = pressScale;
        this.actionListener = actionListener;
    }

    public void fontToShow(Font font) {
        this.font = font;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    protected void setStartAlpha(SpriteBatch batch) {
        tempColor = batch.getPackedColor();
        batch.setColor(1f, 1f, 1f, transparency);
    }

    protected void setEndAlpha(SpriteBatch batch, float tempColor) {
        batch.setColor(tempColor);
    }

    public void toAppearButton(ScaledTouchUpButton btn) {
        toAppear.add(btn);
    }

    public void appear() {
        flushDestroy();
        state = State.APPEARING;
    }

    public void disappear() {
        state = State.DISAPPEARING;
    }

    @Override
    public boolean isMe(Vector2 touch) {
        if (!isDestroyed()) {
            return super.isMe(touch);
        }
        return false;
    }

    @Override
    public boolean isMe(float x, float y) {
        if (!isDestroyed()) {
            return super.isMe(x, y);
        }
        return false;
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (isPressed || !isMe(touch)) {
            return false;
        }
        this.pointer = pointer;
        this.isPressed = true;
        this.scale = pressScale;
        return true;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (this.pointer != pointer || !isPressed) {
            return false;
        }
        if (isMe(touch)) {
            isPressed = false;
            actionListener.actionPerformed(this);
            scale = 1f;
            return true;
        }
        isPressed = false;
        scale = 1f;
        return false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        setStartAlpha(batch);
        super.draw(batch);
        setEndAlpha(batch, tempColor);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (state == State.DISAPPEARING) {
            transparency -= delta;
        }
        if (state == State.APPEARING) {
            transparency += delta;
        }
        if (transparency < 0f && state != State.NORMAL) {
            transparency = 0f;
            state = State.NORMAL;
            destroy();
            showButtons();
            showFont();
        }
        if (transparency > 1f && state != State.NORMAL) {
            transparency = 1f;
            state = State.NORMAL;
        }
    }

    private void showButtons() {
        for (int i = 0; i < toAppear.size(); i++) {
            toAppear.get(i).appear();
        }
        toAppear.clear();
    }

    private void showFont() {
        if (font != null) {
            font.appear();
        }
        font = null;
    }

    @Override
    public void destroy() {
        super.destroy();
        transparency = 0f;
    }
}
