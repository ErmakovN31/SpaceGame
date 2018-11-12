package ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.ScaledTouchUpButton;
import ru.xaero31.oskol.math.Rect;

public class ButtonBack extends ScaledTouchUpButton {
    public ButtonBack(TextureAtlas atlas, ActionListener actionListener, float pressScale) {
        super(atlas.findRegion("backBtn"), actionListener, pressScale);
    }

    @Override
    public void resize(Rect worldBounds) {
        setTop(-370f);
        setRight(35f);
    }
}
