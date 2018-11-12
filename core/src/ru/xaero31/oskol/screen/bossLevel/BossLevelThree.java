package ru.xaero31.oskol.screen.bossLevel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.xaero31.oskol.screen.bossLevel.entity.BossThree;
import ru.xaero31.oskol.screen.pool.GrenadePool;

public class BossLevelThree extends BossLevel {
    private TextureAtlas bossAtlas;
    private GrenadePool grenadePool;
    private Sound grenade;

    public BossLevelThree(Game game, String difficulty) {
        super(game, difficulty);
    }

    @Override
    public void show() {
        pathToBackground = "textures/worldThreeBackground.png";
        super.show();
        grenade = Gdx.audio.newSound(Gdx.files.internal("soundFX/grenade.ogg"));
        bossAtlas = new TextureAtlas("textures/bossLevelThree.pack");
        grenadePool = new GrenadePool(bossAtlas, player, explosionPool, worldBounds);
        boss = new BossThree(bossAtlas.findRegion("bossThree"), 1, 2, 2,
                             enemyShoot, worldBounds, explosionPool, bulletPool, difficulty,
                             bossHpBar, gameAtlas, player, grenadePool, grenade);
    }

    @Override
    protected void updatePools(float delta) {
        super.updatePools(delta);
        grenadePool.updateActiveSprites(delta);
    }

    @Override
    protected void deleteAllDestroyed() {
        super.deleteAllDestroyed();
        grenadePool.freeAllDestroyedActiveSprites();
    }

    @Override
    protected void drawGameObjects() {
        super.drawGameObjects();
        grenadePool.drawActiveSprites(batch);
    }

    @Override
    protected void startNewGame() {
        super.startNewGame();
        if (grenadePool != null) {
            grenadePool.destroyAllActiveObjects();
        }
    }

    @Override
    protected void setNewLevel() {
        levelGenerator.gameDone(difficulty, score);
    }

    @Override
    public void dispose() {
        super.dispose();
        bossAtlas.dispose();
        grenadePool.dispose();
    }
}
