package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.GameScreenButton;

public class ButtonNewGame extends GameScreenButton {
    private final float HEIGHT = 55f;
    private final float TOP_MARGIN = -300f;

    public ButtonNewGame(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("restartBtn"), actionListener, pressScale);
        setHeightProportion(HEIGHT);
        setTop(TOP_MARGIN);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
}
