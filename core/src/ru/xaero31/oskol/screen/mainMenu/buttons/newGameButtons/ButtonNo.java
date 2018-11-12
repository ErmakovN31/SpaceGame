package ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.ScaledTouchUpButton;
import ru.xaero31.oskol.math.Rect;

public class ButtonNo extends ScaledTouchUpButton {
    public ButtonNo(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("noBtn"), actionListener, pressScale);
    }

    @Override
    public void resize(Rect worldBounds) {
        setBottom(-35f);
        setRight(35f);
    }
}
