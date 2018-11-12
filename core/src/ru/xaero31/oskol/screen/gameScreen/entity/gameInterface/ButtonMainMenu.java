package ru.xaero31.oskol.screen.gameScreen.entity.gameInterface;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.GameScreenButton;

public class ButtonMainMenu extends GameScreenButton {
    private final float HEIGHT = 55f;
    private final float TOP_MARGIN = -385f;

    public ButtonMainMenu(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("mainMenuBtn"), actionListener, pressScale);
        setHeightProportion(HEIGHT);
        setTop(TOP_MARGIN);
    }
}
