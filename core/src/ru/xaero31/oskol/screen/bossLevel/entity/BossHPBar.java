package ru.xaero31.oskol.screen.bossLevel.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBar;

public class BossHPBar extends HPBar {
    private Boss boss;
    private int maxHp;

    public BossHPBar(TextureRegion region, Rect worldBounds) {
        super(region, worldBounds);
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeight(HEIGHT * boss.getHp() / maxHp);
        setBottom(worldBounds.getBottom() + BOTTOM_LEFT_MARGIN);
        setRight(worldBounds.getRight() - BOTTOM_LEFT_MARGIN);
    }

    @Override
    public void gettingDamage() {
        setHeight(HEIGHT * boss.getHp() / maxHp);
        setBottom(worldBounds.getBottom() + BOTTOM_LEFT_MARGIN);
        setRight(worldBounds.getRight() - BOTTOM_LEFT_MARGIN);
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
        maxHp = boss.getMaxHp();
    }
}
