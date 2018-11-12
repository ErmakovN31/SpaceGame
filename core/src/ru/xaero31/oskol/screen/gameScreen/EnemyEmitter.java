package ru.xaero31.oskol.screen.gameScreen;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.math.Rnd;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.Enemy;
import ru.xaero31.oskol.screen.pool.EnemyPool;
import ru.xaero31.oskol.utils.Regions;

public class EnemyEmitter {

    //константы для UFO
    private final float UFO_RELOAD_INTERVAL = 2.5f;
    private final float UFO_MOVE_INTERVAL = 0.2f;
    private final float UFO_HEIGHT = 90f;
    private final int UFO_BULLET_DAMAGE = 10;
    private final int UFO_HP = 4;
    private final int UFO_SCORE = 150;
    private final byte UFO_TYPE = 1;
    private final TextureRegion[] UFO_REGION;
    private final Vector2 UFO_DEFAULT_SPEED = new Vector2(90f, 0f);
    private final Vector2 UFO_BULLET_SPEED = new Vector2(0f, -300f);

    //константы для migShip
    private final float MIG_RELOAD_INTERVAL = 2.4f;
    private final float MIG_MOVE_INTERVAL = 1f;
    private final float MIG_HEIGHT = 150f;
    private final int MIG_BULLET_DAMAGE = 15;
    private final int MIG_HP = 2;
    private final int MIG_SCORE = 150;
    private final byte MIG_TYPE = 2;
    private final TextureRegion[] MIG_REGION;
    private final Vector2 MIG_DEFAULT_SPEED = new Vector2 (400f, 0f);
    private final Vector2 MIG_BULLET_SPEED = new Vector2(0f, -800f);

    //константы для crabShip
    private final float CRAB_RELOAD_INTERVAL = 2.1f;
    private final float CRAB_MOVE_INTERVAL = 0.9f;
    private final float CRAB_HEIGHT = 150f;
    private final int CRAB_BULLET_DAMAGE = 5;
    private final int CRAB_HP = 3;
    private final int CRAB_SCORE = 100;
    private final byte CRAB_TYPE = 3;
    private final TextureRegion[] CRAB_REGION;
    private final Vector2 CRAB_DEFAULT_SPEED = new Vector2(140f, 0f);
    private final Vector2 CRAB_BULLET_SPEED = new Vector2(0f, -500f);

    //константы для rocketShip
    private final float ROCKET_RELOAD_INTERVAL = 0f;
    private final float ROCKET_MOVE_INTERVAL = 0f;
    private final float ROCKET_HEIGHT = 150f;
    private final int ROCKET_BULLET_DAMAGE = 0;
    private final int ROCKET_HP = 1;
    private final int ROCKET_BOSS_HP = 30;
    private final int ROCKET_SCORE = 100;
    private final byte ROCKET_TYPE = 4;
    private final TextureRegion[] ROCKET_REGION;
    private final Vector2 ROCKET_DEFAULT_SPEED = new Vector2(0f, -1800f);
    private final Vector2 ROCKET_BULLET_SPEED = new Vector2(0f, 0f);

    //константы для alienShip
    private final float ALIEN_RELOAD_INTERVAL = 5f;
    private final float ALIEN_MOVE_INTERVAL = 6f;
    private final float ALIEN_HEIGHT = 150f;
    private final int ALIEN_BULLET_DAMAGE = 0;
    private final int ALIEN_HP = 8;
    private final int ALIEN_SCORE = 250;
    private final byte ALIEN_TYPE = 5;
    private final TextureRegion[] ALIEN_REGION;
    private final Vector2 ALIEN_DEFAULT_SPEED = new Vector2(0f, -1800f);
    private final Vector2 ALIEN_BULLET_SPEED = new Vector2(0f, 0f);

    //константы для tentacleShip
    private final float TENTACLE_RELOAD_INTERVAL = 1.8f;
    private final float TENTACLE_MOVE_INTERVAL = 6f;
    private final float TENTACLE_HEIGHT = 215f;
    private final int TENTACLE_BULLET_DAMAGE = 15;
    private final int TENTACLE_HP = 10;
    private final int TENTACLE_SCORE = 300;
    private final byte TENTACLE_TYPE = 6;
    private final TextureRegion[] TENTACLE_REGION;
    private final Vector2 TENTACLE_DEFAULT_SPEED = new Vector2(90f, 0f);
    private final Vector2 TENTACLE_BULLET_SPEED = new Vector2(250f, 0f);

    //константы для smallShip
    private final float SMALL_RELOAD_INTERVAL = 0.14f;
    private final float SMALL_MOVE_INTERVAL = 3f;
    private final float SMALL_HEIGHT = 150f;
    private final int SMALL_BULLET_DAMAGE = 2;
    private final int SMALL_HP = 4;
    private final int SMALL_SCORE = 200;
    private final byte SMALL_TYPE = 7;
    private final TextureRegion[] SMALL_REGION;
    private final Vector2 SMALL_DEFAULT_SPEED = new Vector2(100f, 0f);
    private final Vector2 SMALL_BULLET_SPEED = new Vector2(0f, -650f);

    //константы для wideShip
    private final float WIDE_RELOAD_INTERVAL = 3.8f;
    private final float WIDE_MOVE_INTERVAL = 0f;
    private final float WIDE_HEIGHT = 90f;
    private final int WIDE_BULLET_DAMAGE = 0;
    private final int WIDE_HP = 5;
    private final int WIDE_SCORE = 250;
    private final byte WIDE_TYPE = 8;
    private final TextureRegion[] WIDE_REGION;
    private final Vector2 WIDE_DEFAULT_SPEED = new Vector2(150f, 0f);
    private final Vector2 WIDE_BULLET_SPEED = new Vector2(0f, 0f);

    private final String easy = "EASY";
    private final String normal = "NORMAL";
    private final String nightmare = "HARD";
    private final String survival = "SURVIVAL";

    private Rect worldBounds;
    private TextureRegion bulletRegion;
    private EnemyPool enemyPool;
    private Random typeGenerator = new Random();

    private float generateInterval;
    private float generateTimer;
    private float battleYPos;
    private float reloadIntervalMultiplier;
    private float enemySpeedMultiplier = 1f;
    private float moveIntervalMultiplier = 1f;
    private byte typeOfEnemies;
    private byte enemyTypeData;
    private byte maxEnemies;
    private byte countGeneratedEnemies;

    public EnemyEmitter(TextureAtlas atlas, Rect worldBounds, EnemyPool enemyPool,
                        byte typeOfEnemies, byte worldNumber, byte maxEnemies,
                        String difficulty, byte countGeneratedEnemies) {
        this.worldBounds = worldBounds;
        this.enemyPool = enemyPool;
        this.bulletRegion = atlas.findRegion("bulletEnemy");
        this.typeOfEnemies = typeOfEnemies;
        this.enemyTypeData = worldNumber;
        this.maxEnemies = maxEnemies;
        this.countGeneratedEnemies = countGeneratedEnemies;
        setDifficultyParameters(difficulty);
        this.UFO_REGION = Regions.split(atlas.findRegion("UFO"), 1,2,
                2);
        this.MIG_REGION = Regions.split(atlas.findRegion("migShip"),1,2,
                2);
        this.CRAB_REGION = Regions.split(atlas.findRegion("crabShip"),1,2,
                2);
        this.ROCKET_REGION = Regions.split(atlas.findRegion("rocketShip"), 1, 2,
                2);
        this.ALIEN_REGION = Regions.split(atlas.findRegion("alienShip"), 2, 4,
                8);
        this.TENTACLE_REGION = Regions.split(atlas.findRegion("tentacleShip"), 1, 2,
                2);
        this.SMALL_REGION = Regions.split(atlas.findRegion("smallShip"), 1, 2,
                2);
        this.WIDE_REGION = Regions.split(atlas.findRegion("wideShip"), 1, 2,
                2);
    }

    public EnemyEmitter(TextureAtlas atlas, Rect worldBounds, EnemyPool enemyPool,
                        String difficulty) {
        this.bulletRegion = atlas.findRegion("bulletEnemy");
        this.worldBounds = worldBounds;
        this.enemyPool = enemyPool;
        setDifficultyParameters(difficulty);
        this.UFO_REGION = Regions.split(atlas.findRegion("UFO"), 1,2, 2);
        this.ROCKET_REGION = Regions.split(atlas.findRegion("rocketShip"), 1, 2,
                2);
        this.MIG_REGION = null;
        this.CRAB_REGION = null;
        this.ALIEN_REGION = null;
        this.TENTACLE_REGION = null;
        this.SMALL_REGION = null;
        this.WIDE_REGION = null;
    }

    private void setDifficultyParameters(String difficulty) {
        switch (difficulty) {
            case easy:
                generateInterval = 3f;
                reloadIntervalMultiplier = 1.5f;
                break;
            case normal:
                generateInterval = 2f;
                reloadIntervalMultiplier = 1f;
                break;
            case nightmare:
                generateInterval = 1f;
                reloadIntervalMultiplier = 0.65f;
                this.countGeneratedEnemies ++;
                break;
            case survival:
                generateInterval = 4f;
                reloadIntervalMultiplier = 1.5f;
                break;
        }
    }

    public void randomGenerator(float delta) {
            generateTimer += delta;
            if (generateTimer >= generateInterval) {
                for (int i = 0; i < countGeneratedEnemies; i++) {
                    if (enemyPool.getActiveObjects().size() < maxEnemies) {
                        generateTimer = 0f;
                        Enemy enemy = enemyPool.obtain();
                        enemySetCommonParameters(enemy);
                        switch (generateCase()) {
                            case 0:
                                generateCrabShip(enemy);
                                break;
                            case 1:
                                generateUFO(enemy);
                                break;
                            case 2:
                                generateSmallShip(enemy);
                                break;
                            case 3:
                                generateMigShip(enemy);
                                break;
                            case 4:
                                generateRocketShip(enemy);
                                break;
                            case 5:
                                generateAlienShip(enemy);
                                continue;
                            case 6:
                                generateWideShip(enemy);
                                break;
                            case 7:
                                generateTentacleShip(enemy);
                                break;
                        }
                        enemySetLocation(enemy);
                    }
            }
        }
    }

    private void enemySetCommonParameters(Enemy enemy) {
        enemy.setReloadIntervalMultiplier(reloadIntervalMultiplier);
        enemy.setEnemySpeedMultiplier(enemySpeedMultiplier);
        enemy.setMoveIntervalMultiplier(moveIntervalMultiplier);
    }

    private int generateCase() {
        return (typeGenerator.nextInt(typeOfEnemies) + (enemyTypeData - 1) * 3);
    }

    private void enemySetLocation(Enemy enemy) {
        enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(),
                worldBounds.getRight() - enemy.getHalfWidth());
        enemy.setBottom(worldBounds.getTop());
    }

    public void upgradeDifficulty() {
        switch (typeGenerator.nextInt(5)) {
            case 0:
                if (generateInterval > 0.5f) {
                    generateInterval -= 0.5f;
                } else {
                    upgradeDifficulty();
                }
                break;
            case 1:
                maxEnemies++;
                break;
            case 2:
                if (reloadIntervalMultiplier > 0.3f) {
                    reloadIntervalMultiplier -= 0.2f;
                } else {
                    upgradeDifficulty();
                }
                break;
            case 3:
                countGeneratedEnemies++;
                break;
            case 4:
                enemySpeedMultiplier += 0.5f;
                if (moveIntervalMultiplier >= 0.1) {
                    moveIntervalMultiplier -= 0.2;
                }
        }
        if (typeOfEnemies < 8) {
            typeOfEnemies++;
        }
    }

    private void generateUFO(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - UFO_HEIGHT);
        enemy.set(UFO_REGION, UFO_DEFAULT_SPEED, bulletRegion, UFO_BULLET_SPEED, UFO_BULLET_DAMAGE,
                UFO_RELOAD_INTERVAL, UFO_HEIGHT, UFO_HP, battleYPos, UFO_TYPE, UFO_MOVE_INTERVAL,
                UFO_SCORE);
    }

    private void generateMigShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - MIG_HEIGHT);
        enemy.set(MIG_REGION, MIG_DEFAULT_SPEED, bulletRegion, MIG_BULLET_SPEED, MIG_BULLET_DAMAGE,
                MIG_RELOAD_INTERVAL, MIG_HEIGHT, MIG_HP, battleYPos, MIG_TYPE, MIG_MOVE_INTERVAL,
                MIG_SCORE);
    }

    private void generateCrabShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - CRAB_HEIGHT);
        enemy.set(CRAB_REGION, CRAB_DEFAULT_SPEED, bulletRegion, CRAB_BULLET_SPEED,
                CRAB_BULLET_DAMAGE, CRAB_RELOAD_INTERVAL, CRAB_HEIGHT, CRAB_HP, battleYPos,
                CRAB_TYPE, CRAB_MOVE_INTERVAL, CRAB_SCORE);
    }

    private void generateRocketShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, 200f);
        enemy.set(ROCKET_REGION, ROCKET_DEFAULT_SPEED, bulletRegion, ROCKET_BULLET_SPEED,
                ROCKET_BULLET_DAMAGE, ROCKET_RELOAD_INTERVAL, ROCKET_HEIGHT, ROCKET_HP, battleYPos,
                ROCKET_TYPE, ROCKET_MOVE_INTERVAL, ROCKET_SCORE);
    }

    private void generateAlienShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - ALIEN_HEIGHT);
        enemy.set(ALIEN_REGION, ALIEN_DEFAULT_SPEED, bulletRegion, ALIEN_BULLET_SPEED,
                ALIEN_BULLET_DAMAGE, ALIEN_RELOAD_INTERVAL, ALIEN_HEIGHT, ALIEN_HP, battleYPos,
                ALIEN_TYPE, ALIEN_MOVE_INTERVAL, ALIEN_SCORE);
        enemy.setFrame(7);
        enemy.setBottom(battleYPos);
        enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(),
                worldBounds.getRight() - enemy.getHalfWidth());
    }

    private void generateTentacleShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - TENTACLE_HEIGHT);
        enemy.set(TENTACLE_REGION, TENTACLE_DEFAULT_SPEED, bulletRegion, TENTACLE_BULLET_SPEED,
                TENTACLE_BULLET_DAMAGE, TENTACLE_RELOAD_INTERVAL, TENTACLE_HEIGHT, TENTACLE_HP, battleYPos,
                TENTACLE_TYPE, TENTACLE_MOVE_INTERVAL, TENTACLE_SCORE);
    }

    private void generateSmallShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - SMALL_HEIGHT);
        enemy.set(SMALL_REGION, SMALL_DEFAULT_SPEED, bulletRegion, SMALL_BULLET_SPEED,
                SMALL_BULLET_DAMAGE, SMALL_RELOAD_INTERVAL, SMALL_HEIGHT, SMALL_HP, battleYPos,
                SMALL_TYPE, SMALL_MOVE_INTERVAL, SMALL_SCORE);
    }

    private void generateWideShip(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, worldBounds.getTop() - WIDE_HEIGHT);
        enemy.set(WIDE_REGION, WIDE_DEFAULT_SPEED, bulletRegion, WIDE_BULLET_SPEED,
                WIDE_BULLET_DAMAGE, WIDE_RELOAD_INTERVAL, WIDE_HEIGHT, WIDE_HP, battleYPos,
                WIDE_TYPE, WIDE_MOVE_INTERVAL, WIDE_SCORE);
    }

    public void setCountGeneratedEnemies(byte countGeneratedEnemies) {
        this.countGeneratedEnemies = countGeneratedEnemies;
    }

    public void bossGenerateUFO() {
        for (int i = 0; i < 2; i++) {
            Enemy enemy = enemyPool.obtain();
            enemy.setReloadIntervalMultiplier(reloadIntervalMultiplier);
            enemy.setEnemySpeedMultiplier(enemySpeedMultiplier);
            enemy.setMoveIntervalMultiplier(moveIntervalMultiplier);
            generateUFOForBoss(enemy);
            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(),
                    worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }

    private void generateUFOForBoss(Enemy enemy) {
        battleYPos = Rnd.nextFloat(0f, 300f - UFO_HEIGHT);
        enemy.set(UFO_REGION, UFO_DEFAULT_SPEED, bulletRegion, UFO_BULLET_SPEED, UFO_BULLET_DAMAGE,
                UFO_RELOAD_INTERVAL, UFO_HEIGHT, UFO_HP, battleYPos, UFO_TYPE, UFO_MOVE_INTERVAL,
                UFO_SCORE);
    }

    private void generateRocketShipForBoss(Enemy enemy) {
        battleYPos = worldBounds.getTop();
        enemy.set(ROCKET_REGION, ROCKET_DEFAULT_SPEED, bulletRegion, ROCKET_BULLET_SPEED,
                ROCKET_BULLET_DAMAGE, ROCKET_RELOAD_INTERVAL, ROCKET_HEIGHT, ROCKET_BOSS_HP,
                battleYPos, ROCKET_TYPE, ROCKET_MOVE_INTERVAL, ROCKET_SCORE);
    }

    public void bossGenerateRocket(float x) {
        Enemy enemy = enemyPool.obtain();
        enemy.setEnemySpeedMultiplier(enemySpeedMultiplier);
        generateRocketShipForBoss(enemy);
        enemy.pos.x = x;
        enemy.setBottom(worldBounds.getTop());
    }
}
