package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.GameScreenButton;

public class ButtonYes extends GameScreenButton {
    private final float HEIGHT = 55f;
    private final float TOP_MARGIN = -385f;

    public ButtonYes(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("yesBtn"), actionListener, pressScale);
        setHeightProportion(HEIGHT);
        setTop(TOP_MARGIN);
    }
}
