package ru.xaero31.oskol.screen.bossLevel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.screen.bossLevel.entity.BossTwo;
import ru.xaero31.oskol.screen.bossLevel.entity.HeartSprite;

public class BossLevelTwo extends BossLevel {
    private TextureAtlas bossAtlas;
    private HeartSprite heartSprite;

    public BossLevelTwo(Game game, String difficulty) {
        super(game, difficulty);
    }

    @Override
    public void show() {
        pathToBackground = "textures/worldTwoBackground.png";
        super.show();
        bossAtlas = new TextureAtlas("textures/bossLevelTwo.pack");
        heartSprite = new HeartSprite(bossAtlas.findRegion("blueSphere"), 1,
                                      2, 2, explosionPool, player, worldBounds);
        boss = new BossTwo(bossAtlas.findRegion("bossTwo"), 1, 1, 1,
                           rocket, worldBounds, explosionPool, bulletPool, difficulty, bossHpBar,
                           gameAtlas, player, targetPool, rocketPool, enemyEmitter, heartSprite);
    }

    @Override
    protected void startNewGame() {
        heartSprite.destroy();
        super.startNewGame();
    }

    @Override
    protected void drawGameObjects() {
        heartSprite.draw(batch);
        super.drawGameObjects();
    }

    @Override
    protected void checkCollisions() {
        super.checkCollisions();
        heartSprite.checkCollisions(bulletPool.getActiveObjects());
    }

    @Override
    protected void checkDying() {
        if (boss.isAbleToDie()) {
            super.checkDying();
        }
    }

    @Override
    protected void mainState(float delta) {
        super.mainState(delta);
        heartSprite.update(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        bossAtlas.dispose();
    }
}
