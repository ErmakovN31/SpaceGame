package ru.xaero31.oskol.screen.bossLevel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.screen.bossLevel.entity.BossOne;

public class BossLevelOne extends BossLevel {
    private TextureAtlas bossAtlas;

    public BossLevelOne(Game game, String difficulty) {
        super(game, difficulty);
    }

    @Override
    public void show() {
        pathToBackground = "textures/worldOneBackground.png";
        super.show();
        bossAtlas = new TextureAtlas("textures/bossLevelOne.pack");
        boss = new BossOne(bossAtlas.findRegion("bossLevelOne"), 1, 2, 2,
                enemyShoot, worldBounds, explosionPool, bulletPool,
                enemyEmitter, difficulty, enemyPool, bossHpBar, gameAtlas, player);
        bossHpBar.setBoss(boss);
    }

    @Override
    protected void checkCollisions() {
        super.checkCollisions();
        boss.checkBossCollision(bulletPool.getActiveObjects());
    }

    @Override
    public void dispose() {
        super.dispose();
        bossAtlas.dispose();
    }
}