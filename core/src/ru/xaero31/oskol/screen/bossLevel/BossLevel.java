package ru.xaero31.oskol.screen.bossLevel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.Base2DScreen;
import ru.xaero31.oskol.base.Font;
import ru.xaero31.oskol.base.LevelGenerator;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.bossLevel.entity.Boss;
import ru.xaero31.oskol.screen.bossLevel.entity.BossHPBar;
import ru.xaero31.oskol.screen.gameScreen.EnemyEmitter;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Rocket;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonMainMenu;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonNewGame;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonNo;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonYes;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBar;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBarBorder;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.MessageGameOver;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.Enemy;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.SpaceShip;
import ru.xaero31.oskol.screen.mainMenu.MainMenu;
import ru.xaero31.oskol.screen.pool.BulletPool;
import ru.xaero31.oskol.screen.pool.EnemyPool;
import ru.xaero31.oskol.screen.pool.ExplosionPool;
import ru.xaero31.oskol.screen.pool.RocketPool;
import ru.xaero31.oskol.screen.pool.TargetPool;
import ru.xaero31.oskol.screen.sprites.Background;
import ru.xaero31.oskol.screen.sprites.Star;
import ru.xaero31.oskol.utils.DataBaseHelper;

public abstract class BossLevel extends Base2DScreen implements ActionListener {
    protected enum State {START, FINISH, MAIN, PAUSE, GAME_OVER, MAIN_MENU}
    protected State state;
    protected State temp;

    protected String pathToBackground;
    protected String difficulty;
    protected int score;

    protected TextureAtlas gameAtlas;
    protected Sound enemyShoot;
    protected Sound rocket;

    protected LevelGenerator levelGenerator;
    protected EnemyEmitter enemyEmitter;
    protected ExplosionPool explosionPool;
    protected BulletPool bulletPool;
    protected EnemyPool enemyPool;
    protected RocketPool rocketPool;
    protected TargetPool targetPool;
    protected BossHPBar bossHpBar;
    protected Boss boss;
    protected SpaceShip player;

    private final int STAR_COUNT = 50;
    private final float STAR_INTERVAL = 0.02f;
    private final float PLAYER_MAX_HP = 100f;
    private final float BUTTON_TOUCH_SCALE = 0.9f;
    private final float FONT_SIZE = 35f;
    private final float TITLE_SIZE = 80f;
    private final float MUSIC_START_VOL = 0.4f;
    private final float STARTING_INTERVAL = 5f;
    private final float MAIN_MENU_MESSAGE_TOP_MARGIN = 240f;
    private final float CHANGE_LEVEL_INTERVAL = 4f;
    private final float CHANGING_SCREEN_INTERVAL = 0.025f;
    private final String COMPLETE = "BOSS LEVEL COMPLETE";
    private final String GET_READY = "GET READY";
    private final String PAUSED = "PAUSED";
    private final String GO_TO_MAIN_MENU = "Go to main menu?";

    private float changingScreenTimer = 0f;
    private float fogging = 1f;
    private float starTimer;
    private float startingTimer;
    private float changeLevelTimer;
    private byte starAdded = 0;
    private String level = "Level: BOSS";

    private StringBuilder sbScore = new StringBuilder("Score: ");
    private Font font;

    private Background background;
    private TextureAtlas menuAtlas;
    private TextureRegion starRegion;
    private Texture bgTexture;
    private Star[] star;

    private Music bossMusic;
    private Sound playerShoot;
    private Sound explosionSound;
    private Sound target;

    private MessageGameOver messageGameOver;
    private ButtonNewGame buttonNewGame;
    private ButtonMainMenu buttonMainMenu;
    private ButtonYes buttonYes;
    private ButtonNo buttonNo;

    private HPBar hpBar;
    private HPBarBorder hpBarBorder;
    private HPBarBorder bossHpBarBorder;

    public BossLevel(Game game, String difficulty) {
        super(game);
        levelGenerator = DataBaseHelper.getLevelGenerator();
        this.difficulty = difficulty;
        if (difficulty.equals("None")) this.difficulty = "EASY";
        state = State.START;
    }

    @Override
    public void show() {
        super.show();
        initResources();
        initBackground();
        initSounds();
        initButtons();
        initGameObjects();
        font = new Font("font/font.fnt", "font/font.png");
        destroyExcessButtons();
        musicStartToPlay();
        startNewGame();
    }

    private void initResources() {
        bgTexture = new Texture(pathToBackground);
        gameAtlas = new TextureAtlas("textures/mainAtlas.pack");
        menuAtlas = new TextureAtlas("textures/mainMenuAtlas.pack");
    }

    private void initBackground() {
        background = new Background(new TextureRegion(bgTexture));
        starRegion = menuAtlas.findRegion("starSprite");
        star = new Star[STAR_COUNT];
        messageGameOver = new MessageGameOver(gameAtlas);
    }

    private void initSounds() {
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("music/bossMusic.mp3"));
        playerShoot = Gdx.audio.newSound(Gdx.files.internal("soundFX/shot.ogg"));
        enemyShoot = Gdx.audio.newSound(Gdx.files.internal("soundFX/enemyShot.ogg"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("soundFX/explosion.ogg"));
        rocket = Gdx.audio.newSound(Gdx.files.internal("soundFX/rocket.ogg"));
        target = Gdx.audio.newSound(Gdx.files.internal("soundFX/target.ogg"));
    }

    private void initButtons() {
        buttonNewGame = new ButtonNewGame(menuAtlas, this, BUTTON_TOUCH_SCALE);
        buttonMainMenu = new ButtonMainMenu(menuAtlas, this, BUTTON_TOUCH_SCALE);
        buttonYes = new ButtonYes(menuAtlas, this, BUTTON_TOUCH_SCALE);
        buttonNo = new ButtonNo(menuAtlas, this, BUTTON_TOUCH_SCALE);
    }

    private void initGameObjects() {
        initHpBars();
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(gameAtlas, explosionSound);
        player = new SpaceShip(gameAtlas, bulletPool, playerShoot,
                worldBounds, explosionPool, hpBar);
        targetPool = new TargetPool(gameAtlas, explosionPool, player, target);
        rocketPool = new RocketPool(gameAtlas, player, explosionPool);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, enemyShoot,
                rocket, rocketPool, player, targetPool, null);
        enemyEmitter = new EnemyEmitter(gameAtlas, worldBounds, enemyPool, difficulty);
        background.setPlayer(player);
    }

    private void initHpBars() {
        hpBar = new HPBar(gameAtlas.findRegion("hpBar"), worldBounds);
        hpBarBorder = new HPBarBorder(gameAtlas.findRegion("hpBorder"));
        bossHpBar = new BossHPBar(gameAtlas.findRegion("hpBar"), worldBounds);
        bossHpBarBorder = new HPBarBorder(gameAtlas.findRegion("hpBorder"));
    }

    private void destroyExcessButtons() {
        buttonNewGame.destroy();
        buttonMainMenu.destroy();
        buttonYes.destroy();
        buttonNo.destroy();
    }

    private void musicStartToPlay() {
        if (!bossMusic.isPlaying()) {
            bossMusic.setVolume(MUSIC_START_VOL);
            bossMusic.setLooping(true);
            bossMusic.play();
            bossMusic.setPosition(0.25f);
        }
    }

    protected void startNewGame() {
        stopSound();
        targetPool.destroyAllActiveObjects();
        bulletPool.destroyAllActiveObjects();
        enemyPool.destroyAllActiveObjects();
        rocketPool.destroyAllActiveObjects();
        player.startNewGame();
        state = State.START;
        font.setWorldSize(TITLE_SIZE * 0.8f);
        startingTimer = 0;
        score = 0;
    }

    private void stopSound() {
        target.stop();
        playerShoot.stop();
        enemyShoot.stop();
        rocket.stop();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        deleteAllDestroyed();
        draw();
    }

    protected void update(float delta) {
        gameOverCheck();
        updateStars(delta);
        explosionPool.updateActiveSprites(delta);
        if (state == State.MAIN) background.update(delta);
        switch (state) {
            case START:
                switchToMainState(delta);
                break;
            case MAIN:
                mainState(delta);
                break;
            case GAME_OVER:
                gameOverState(delta);
                break;
            case FINISH:
                boss.update(delta);
                changingLevel(delta);
                break;
            case MAIN_MENU:
                changingLevel(delta);
            case PAUSE:
                buttonYes.update(delta);
                buttonNo.update(delta);
        }
    }

    private void gameOverCheck() {
        if (state == State.MAIN || state == State.GAME_OVER) {
            if (player.isDestroyed()) {
                messageGameOver.flushDestroy();
                buttonNewGame.flushDestroy();
                buttonMainMenu.flushDestroy();
                state = State.GAME_OVER;
                levelGenerator.checkScores(score);
            }
        }
    }

    private void updateStars(float delta) {
        starAdd(delta);
        for (int i = 0; i < starAdded; i++) {
            star[i].update(delta);
        }
    }

    private void starAdd(float delta) {
        if (starAdded < STAR_COUNT) {
            starTimer += delta;
            if (starTimer >= STAR_INTERVAL) {
                starTimer = 0;
                star[starAdded] = new Star(starRegion, 1, 19, 19, worldBounds,
                        player);
                starAdded++;
            }
        }
    }

    private void switchToMainState(float delta) {
        player.update(delta);
        startingTimer += delta;
        if (startingTimer >= STARTING_INTERVAL) {
            font.setWorldSize(FONT_SIZE);
            state = State.MAIN;
            boss.ready();
        }
    }

    protected void mainState(float delta) {
        background.update(delta);
        player.update(delta);
        boss.update(delta);
        updatePools(delta);
        checkDying();
    }

    protected void updatePools(float delta) {
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        rocketPool.updateActiveSprites(delta);
        targetPool.updateSpritesForBoss(delta, enemyEmitter);
    }

    protected void checkDying() {
        if (boss.getHp() <= 0) {
            boss.dying();
            score += boss.getSCORE();
            state = State.FINISH;
            font.setWorldSize(TITLE_SIZE * 0.6f);
        }
    }

    private void gameOverState(float delta) {
        messageGameOver.update(delta);
        buttonNewGame.update(delta);
        buttonMainMenu.update(delta);
    }

    private void changingLevel(float delta) {
        if ((boss.isDestroyed() && buttonYes.isDestroyed()) || state == State.MAIN_MENU) {
            changeLevelTimer += delta;
            if (bossMusic.getVolume() >= delta * 0.1f)
                bossMusic.setVolume(bossMusic.getVolume() - delta * 0.1f);
            if (changeLevelTimer >= CHANGE_LEVEL_INTERVAL || state == State.MAIN_MENU) {
                changingScreenTimer += delta;
                if (changingScreenTimer >= CHANGING_SCREEN_INTERVAL) {
                    changingScreenTimer = 0;
                    fogging -= 0.017f;
                    if (fogging <= -0.01f) {
                        if (state == State.MAIN_MENU && canSwitch) {
                            canSwitch = false;
                            game.setScreen(new MainMenu(game));
                        } else if (canSwitch) {
                            canSwitch = false;
                            setNewLevel();
                        }
                    }
                }
            }
        }
    }

    protected void setNewLevel() {
        levelGenerator.worldDone(difficulty, score);
    }

    protected void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        List<Rocket> rocketList = rocketPool.getActiveObjects();
        checkBodyCollisions(enemyList);
        checkEnemyHitCollisions(enemyList, bulletList);
        checkPlayerHitCollisions(bulletList);
        checkRocketHitToPlayerCollisions(rocketList);
        checkPlayerHitToRocketCollisions(rocketList, bulletList);
    }

    private void checkBodyCollisions(List<Enemy> enemyList) {
        for (int i = 0; i < enemyList.size(); i++) {
            if (enemyList.get(i).isDestroyed()) {
                continue;
            }
            float minDist = enemyList.get(i).getHalfWidth() + player.getHalfWidth();
            if (enemyList.get(i).pos.dst2(player.pos) < minDist * minDist) {
                enemyList.get(i).destroy();
                player.destroy();
                return;
            }
        }
    }

    private void checkEnemyHitCollisions(List<Enemy> enemyList, List<Bullet> bulletList) {
        for (int e = 0; e < enemyList.size(); e++) {
            if (enemyList.get(e).isDestroyed()) {
                continue;
            }
            for (int b = 0; b < bulletList.size(); b++) {
                if (bulletList.get(b).getOwner() != player || bulletList.get(b).isDestroyed()) {
                    continue;
                }
                if (enemyList.get(e).isInvulnerable() &&
                        enemyList.get(e).isBulletCollision(bulletList.get(b))) {
                    bulletList.get(b).destroy();
                    continue;
                }
                if (enemyList.get(e).isBulletCollision(bulletList.get(b))) {
                    enemyList.get(e).damage(bulletList.get(b).getDamage());
                    bulletList.get(b).destroy();
                    if (enemyList.get(e).isDestroyed()) {
                        score += enemyList.get(e).getScore();
                    }
                }
            }
        }
    }

    private void checkPlayerHitCollisions(List<Bullet> bulletList) {
        for (int b = 0; b < bulletList.size(); b++) {
            if (bulletList.get(b).isDestroyed() || bulletList.get(b).getOwner() == player) {
                continue;
            }
            if (player.isBulletCollision(bulletList.get(b))) {
                player.damage(bulletList.get(b).getDamage());
                bulletList.get(b).destroy();
            }
        }
    }

    private void checkRocketHitToPlayerCollisions(List<Rocket> rocketList) {
        for (int i = 0; i < rocketList.size(); i++) {
            if (rocketList.get(i).isDestroyed()) {
                continue;
            }
            if (player.isMe(rocketList.get(i).pos)) {
                player.damage(rocketList.get(i).getDamage());
                rocketList.get(i).destroy();
            }
        }
    }

    private void checkPlayerHitToRocketCollisions(List<Rocket> rocketList,
                                                  List<Bullet> bulletList) {
        for (int b = 0; b < bulletList.size(); b++) {
            if (bulletList.get(b).isDestroyed() || bulletList.get(b).getOwner() != player) {
                continue;
            }
            for (int r = 0; r < rocketList.size(); r++) {
                if (rocketList.get(r).isDestroyed()) {
                    continue;
                }
                if (rocketList.get(r).isBulletCollision(bulletList.get(b))) {
                    rocketList.get(r).destroy();
                    score += rocketList.get(r).getSCORE();
                    bulletList.get(b).destroy();
                }
            }
        }
    }

    protected void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
        rocketPool.freeAllDestroyedActiveSprites();
        targetPool.freeAllDestroyedActiveSprites();
    }

    protected void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        drawStars();
        drawGameObjects();
        drawGameOver();
        drawHPBar();
        drawButtons();
        printInfo();
        batch.end();
    }

    private void drawStars() {
        for (int i = 0; i < starAdded; i++) {
            star[i].draw(batch);
        }
    }

    protected void drawGameObjects() {
        bulletPool.drawActiveSprites(batch);
        rocketPool.drawActiveSprites(batch);
        boss.draw(batch);
        enemyPool.drawActiveSprites(batch);
        player.draw(batch);
        explosionPool.drawActiveSprites(batch);
        targetPool.drawActiveSprites(batch);
    }

    private void drawGameOver() {
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
            buttonMainMenu.draw(batch);
        }
    }

    private void drawHPBar() {
        batch.setColor(1f, 1f, 1f, fogging * 0.7f);
        hpBarBorder.draw(batch);
        bossHpBarBorder.draw(batch);
        batch.setColor(1f - player.getHp() / PLAYER_MAX_HP, player.getHp() / PLAYER_MAX_HP,
                0f, fogging * 0.7f);
        hpBar.draw(batch);
        batch.setColor(1f - boss.getHp() / boss.getFloatMaxHp(),
                boss.getHp() / boss.getFloatMaxHp(), 0f, fogging * 0.7f);
        bossHpBar.draw(batch);
        batch.setColor(1f, 1f, 1f, fogging);
    }

    private void drawButtons() {
        buttonNewGame.draw(batch);
        buttonMainMenu.draw(batch);
        buttonYes.draw(batch);
        buttonNo.draw(batch);
    }

    private void printInfo() {
        switch (state) {
            case START:
                startTitle();
                break;
            case MAIN:
                mainStateInfo();
                break;
            case FINISH:
                endingInfo();
                break;
            case PAUSE:
                drawPauseTitle();
                goToMainQuestion();
        }
    }

    private void startTitle() {
        if (startingTimer >= STARTING_INTERVAL / 2f) {
            font.draw(batch, GET_READY, worldBounds.pos.x, worldBounds.pos.y +
                    TITLE_SIZE * 1.5f, Align.center);
        } else {
            font.draw(batch, level, worldBounds.pos.x, worldBounds.pos.y +
                    TITLE_SIZE * 1.5f, Align.center);
        }
    }

    private void mainStateInfo() {
        sbScore.setLength(7);
        font.draw(batch, sbScore.append(score), worldBounds.getLeft(),
                worldBounds.getTop());
        font.draw(batch, level, worldBounds.getRight(),
                worldBounds.getTop(), Align.right);
    }

    private void endingInfo() {
        if (boss.isDestroyed()) {
            font.draw(batch, COMPLETE, worldBounds.pos.x, worldBounds.pos.y +
                    TITLE_SIZE * 1.2f, Align.center);
            font.setColor(1f, 1f, 1f, fogging);
        }
    }

    private void drawPauseTitle() {
        font.draw(batch, PAUSED, worldBounds.pos.x, worldBounds.pos.y +
                TITLE_SIZE * 1.2f, Align.center);
    }

    private void goToMainQuestion() {
        if (!buttonYes.isDestroyed()) {
            font.setWorldSize(FONT_SIZE);
            font.draw(batch, GO_TO_MAIN_MENU, worldBounds.pos.x,
                    worldBounds.pos.y - MAIN_MENU_MESSAGE_TOP_MARGIN, Align.center);
            font.setWorldSize(TITLE_SIZE * 0.8f);
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        starResize(worldBounds);
        player.resize(worldBounds);
        boss.resize(worldBounds);
        hpBar.resize(worldBounds);
        hpBarBorder.pos.set(hpBar.pos);
        bossHpBar.resize(worldBounds);
        bossHpBarBorder.pos.set(bossHpBar.pos);
    }

    private void starResize(Rect worldBounds) {
        for (int i = 0; i < starAdded; i++) {
            star[i].resize(worldBounds);
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (state != State.GAME_OVER && state != State.FINISH && state != State.PAUSE) {
            font.setWorldSize(TITLE_SIZE * 0.8f);
            temp = state;
            state = State.PAUSE;
        }
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        resourcesDispose();
        soundsDispose();
        poolsDispose();
    }

    private void resourcesDispose() {
        bgTexture.dispose();
        menuAtlas.dispose();
        gameAtlas.dispose();
        font.dispose();
    }

    private void soundsDispose() {
        bossMusic.dispose();
        enemyShoot.dispose();
        playerShoot.dispose();
        explosionSound.dispose();
    }

    private void poolsDispose() {
        explosionPool.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.MAIN) {
            player.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.MAIN) {
            player.keyUp(keycode);
        }
        backButtonAction(keycode);
        return false;
    }

    private void backButtonAction(int keycode) {
        if (keycode == Input.Keys.BACK) {
            if (state == State.MAIN) {
                pause();
                resume();
                buttonYes.flushDestroy();
                buttonNo.flushDestroy();
            }
            if (state == State.GAME_OVER) {
                actionPerformed(buttonMainMenu);
            }
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        switch (state) {
            case MAIN:
                player.touchDown(touch, pointer);
                break;
            default:
                buttonNewGame.touchDown(touch, pointer);
                buttonMainMenu.touchDown(touch, pointer);
                buttonYes.touchDown(touch, pointer);
                buttonNo.touchDown(touch, pointer);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        switch (state) {
            case PAUSE:
                pauseTouchUpAction(touch, pointer);
                break;
            case MAIN:
                player.touchUp(touch, pointer);
                break;
            case GAME_OVER:
                buttonNewGame.touchUp(touch, pointer);
                buttonMainMenu.touchUp(touch, pointer);
        }
        return false;
    }

    private void pauseTouchUpAction(Vector2 touch, int pointer) {
        if (!buttonYes.isDestroyed()) {
            buttonYes.touchUp(touch, pointer);
            if (buttonNo.touchUp(touch, pointer)) touchUp(touch, pointer);
        } else {
            if (temp == State.MAIN) {
                font.setWorldSize(FONT_SIZE);
            }
            state = temp;
        }
    }

    @Override
    public void actionPerformed(Object src) {
        newGameAction(src);
        mainMenuAction(src);
        yesAction(src);
        noAction(src);
    }

    private void newGameAction(Object src) {
        if (src == buttonNewGame) {
            startNewGame();
            boss.resetBoss();
            messageGameOver.destroy();
            buttonNewGame.destroy();
            buttonMainMenu.destroy();
        }
    }

    private void mainMenuAction(Object src) {
        if (src == buttonMainMenu) {
            state = State.MAIN_MENU;
        }
    }

    private void yesAction(Object src) {
        if (src == buttonYes) {
            buttonYes.disappear();
            buttonNo.disappear();
            if (temp != State.PAUSE && temp != State.GAME_OVER) {
                font.setWorldSize(FONT_SIZE);
            }
            actionPerformed(buttonMainMenu);
        }
    }

    private void noAction(Object src) {
        if (src == buttonNo) {
            buttonYes.destroy();
            buttonNo.destroy();
        }
    }
}
