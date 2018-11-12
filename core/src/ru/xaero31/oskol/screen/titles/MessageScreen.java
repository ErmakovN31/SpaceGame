package ru.xaero31.oskol.screen.titles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.xaero31.oskol.base.Base2DScreen;
import ru.xaero31.oskol.base.Sprite;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.mainMenu.MainMenu;
import ru.xaero31.oskol.screen.sprites.Background;

public abstract class MessageScreen extends Base2DScreen {
    protected TextureAtlas messageAtlas;
    protected Sprite message;

    private final float DELTA_FOGGING = 0.017f;

    private enum State {SHOW, STATIC, HIDE}
    private State state;
    private Background background;
    private Texture bgTexture;
    private float fogging;

    public MessageScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        bgTexture = new Texture("textures/worldOneBackground.png");
        messageAtlas = new TextureAtlas("textures/opening.pack");
        background = new Background(new TextureRegion(bgTexture));
        setMessageRegion();
        state = State.SHOW;
    }

    protected abstract void setMessageRegion();

    protected abstract void setMessageSize();

    @Override
    public void render(float delta) {
        super.render(delta);
        update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setColor(1f, 1f, 1f, fogging);
        background.draw(batch);
        message.draw(batch);
        batch.end();
    }

    protected void update() {
        switch (state) {
            case SHOW:
                if (fogging + DELTA_FOGGING <= 1) {
                    fogging += DELTA_FOGGING;
                } else {
                    state = State.STATIC;
                }
                break;
            case STATIC:
                break;
            case HIDE:
                if (fogging - DELTA_FOGGING >= 0) {
                    fogging -= DELTA_FOGGING;
                } else if (canSwitch) {
                    canSwitch = false;
                    game.setScreen(new MainMenu(game));
                }
                break;
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        message.pos.set(worldBounds.pos);
        setMessageSize();
    }

    @Override
    public void dispose() {
        messageAtlas.dispose();
        bgTexture.dispose();
        super.dispose();
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (state == State.STATIC) {
            state = State.HIDE;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.STATIC) {
            state = State.HIDE;
        }
        return false;
    }
}
