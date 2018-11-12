package ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.ScaledTouchUpButton;
import ru.xaero31.oskol.math.Rect;

public class ButtonNormal extends ScaledTouchUpButton {
    public ButtonNormal(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("normalBtn"), actionListener, pressScale);
    }

    @Override
    public void resize(Rect worldBounds) {
        setBottom(-115f);
        setRight(35f);
    }
}
