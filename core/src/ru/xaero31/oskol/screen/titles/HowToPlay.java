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
import ru.xaero31.oskol.screen.gameScreen.GameScreen;
import ru.xaero31.oskol.screen.sprites.Background;
import ru.xaero31.oskol.screen.sprites.HowToPlaySprite;

public class HowToPlay extends Base2DScreen {
    private final String easy = "EASY";

    private enum State {LEFT, RIGHT, CENTER, SHOT}
    private State state = State.LEFT;

    private HowToPlaySprite howToPlay;
    private Background background;
    private Sprite left;
    private Sprite right;
    private Sprite center;
    private Sprite arrow;
    private Sprite toContinue;

    private TextureAtlas howToPlayAtlas;
    private Texture howToPlayTexture;
    private Texture backgroundTexture;

    public HowToPlay(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        howToPlayTexture = new Texture("textures/howToPlay.png");
        howToPlayAtlas = new TextureAtlas("textures/howToPlayText.pack");
        backgroundTexture = new Texture("textures/worldOneBackground.png");
        howToPlay = new HowToPlaySprite(new TextureRegion(howToPlayTexture));
        left = new Sprite(howToPlayAtlas.findRegion("left"));
        right = new Sprite(howToPlayAtlas.findRegion("right"));
        center = new Sprite(howToPlayAtlas.findRegion("shot"));
        arrow = new Sprite(howToPlayAtlas.findRegion("arrow"));
        toContinue = new Sprite(howToPlayAtlas.findRegion("continue"));
        background = new Background(new TextureRegion(backgroundTexture));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        howToPlay.draw(batch);
        left.draw(batch);
        right.draw(batch);
        center.draw(batch);
        arrow.draw(batch);
        toContinue.draw(batch);
        batch.end();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        howToPlay.resize(worldBounds);
        background.resize(worldBounds);
        left.setHeightProportion(90f);
        left.setTop(100f);
        arrow.setHeightProportion(100f);
        arrow.setTop(-150f);
        arrow.setLeft(-(worldBounds.getWidth() / 3));
    }

    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
        howToPlayTexture.dispose();
        howToPlayAtlas.dispose();
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        switch (state) {
            case LEFT:
                leftStateTouch();
                break;
            case RIGHT:
                rightStateTouch();
                break;
            case CENTER:
                centerStateTouch();
                break;
            case SHOT:
                game.setScreen(new GameScreen(game, easy));
                break;
        }
        return true;
    }

    private void leftStateTouch() {
        left.destroy();
        right.setHeightProportion(95f);
        right.setTop(106f);
        arrow.rotate(180);
        arrow.setRight(worldBounds.getWidth() / 3);
        state = State.RIGHT;
    }

    private void rightStateTouch() {
        right.destroy();
        center.setHeightProportion(99f);
        center.setTop(106f);
        arrow.rotate(-90);
        arrow.setTop(-300f);
        arrow.pos.x = worldBounds.pos.x;
        state = State.CENTER;
    }

    private void centerStateTouch() {
        center.destroy();
        arrow.destroy();
        toContinue.setHeightProportion(60f);
        toContinue.setTop(105f);
        state = State.SHOT;
    }
}
