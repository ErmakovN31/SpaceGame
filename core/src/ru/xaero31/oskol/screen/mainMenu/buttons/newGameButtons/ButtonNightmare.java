package ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.ScaledTouchUpButton;
import ru.xaero31.oskol.math.Rect;

public class ButtonNightmare extends ScaledTouchUpButton {
    public ButtonNightmare(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("nightmareBtn"), actionListener, pressScale);
    }

    @Override
    public void resize(Rect worldBounds) {
        setTop(-140f);
        setRight(35f);
    }
}
