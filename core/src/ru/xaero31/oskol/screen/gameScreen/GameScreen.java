package ru.xaero31.oskol.screen.gameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.Base2DScreen;
import ru.xaero31.oskol.base.Font;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.entity.Bullet;
import ru.xaero31.oskol.screen.gameScreen.entity.Rocket;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonMainMenu;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonNewGame;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonNo;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.ButtonYes;
import ru.xaero31.oskol.screen.gameScreen.entity.ships.Enemy;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBar;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.HPBarBorder;
import ru.xaero31.oskol.screen.gameScreen.entity.gameInterface.MessageGameOver;
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
import ru.xaero31.oskol.base.LevelGenerator;

public class GameScreen extends Base2DScreen implements ActionListener {
    private enum State {PLAYING, GAME_OVER, NEXT_LEVEL, STARTING, PAUSE, GO_TO_MAIN}
    private State state;
    private State temp = null;

    private LevelGenerator levelGenerator;

    private final float UPGRADE_DIFFICULTY_INTERVAL = 25f;
    private final float STAR_INTERVAL = 0.02f;
    private final float MUSIC_START = 1.5f;
    private final float MUSIC_INTERVAL = 0.2f;
    private final float MUSIC_DELTA = 0.08f;
    private final float MUSIC_END_VOLUME = 0.4f;
    private final float STARTING_INTERVAL = 5f;
    private final float CHANGE_LEVEL_INTERVAL = 4f;
    private final float CHANGING_SCREEN_INTERVAL = 0.025f;
    private final float FONT_SIZE = 35f;
    private final float TITLE_SIZE = 80f;
    private final float PLAYER_MAX_HP = 100f;
    private final float BUTTON_TOUCH_SCALE = 0.9f;
    private final float MAIN_MENU_MESSAGE_TOP_MARGIN = 240f;
    private final int STAR_COUNT = 50;
    private final String COMPLETE = "LEVEL COMPLETE";
    private final String GET_READY = "GET READY";
    private final String PAUSED = "PAUSED";
    private final String SURVIVAL = "SURVIVAL";
    private final String GO_TO_MAIN_MENU = "Go to main menu?";

    private float changingScreenTimer = 0f;
    private float fogging = 1f;
    private float starTimer;
    private float musicTimer;
    private float startingTimer;
    private float changeLevelTimer;
    private float upgradeDifficultyTimer;
    private int frags;
    private int score;
    private int fragsToNextLevel;
    private byte starAdded = 0;
    private byte countGeneratedEnemies;
    private byte typeOfEnemies;
    private byte enemyTypeData;
    private byte maxEnemiesOnScreen;

    private Background background;
    private Texture bgTexture;
    private TextureAtlas atlas;
    private TextureAtlas mainAtlas;
    private TextureRegion starRegion;
    private Star[] star;

    private SpaceShip player;
    private HPBar hpBar;
    private HPBarBorder hpBarBorder;

    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;
    private RocketPool rocketPool;
    private TargetPool targetPool;
    private EnemyEmitter enemyEmitter;

    private Music gameMusic;
    private Sound shoot;
    private Sound explosionSound;
    private Sound enemyShot;
    private Sound rocket;
    private Sound teleport;
    private Sound targeting;

    private MessageGameOver messageGameOver;
    private ButtonNewGame buttonNewGame;
    private ButtonMainMenu buttonMainMenu;
    private ButtonYes buttonYes;
    private ButtonNo buttonNo;

    private Font font;
    private StringBuilder sbScore = new StringBuilder("Score: ");
    private String level = "Level: ";
    private String levelName;
    private String difficulty;
    private String pathToMusic;
    private String pathToBackground;

    public GameScreen(Game game, String difficulty) {
        super(game);
        levelGenerator = DataBaseHelper.getLevelGenerator();
        this.difficulty = difficulty;
        this.fragsToNextLevel = levelGenerator.getFragsToNextLevel();
        this.levelName = levelGenerator.getLevelName();
        this.typeOfEnemies = levelGenerator.getTypeOfEnemies();
        this.enemyTypeData = levelGenerator.getEnemyTypeData();
        this.maxEnemiesOnScreen = levelGenerator.getMaxEnemiesOnScreen();
        this.pathToMusic = levelGenerator.getLevelMusic();
        this.pathToBackground = levelGenerator.getLevelBackground();
        this.countGeneratedEnemies = levelGenerator.getCountGeneratedEnemies();
        setLevelExtension(difficulty);
        changeLevelTimer = 0;
    }

    private void setLevelExtension(String difficulty) {
        if (difficulty.equals("None")) {
            this.difficulty = "EASY";
        }
        if (this.difficulty.equals("EASY")) {
            fragsToNextLevel /= 2;
        }
        if (this.difficulty.equals("NORMAL")) {
            fragsToNextLevel = fragsToNextLevel / 4 * 3;
        }
    }

    @Override
    public void show() {
        super.show();
        initResources();
        initBackground();
        initSounds();
        initGameObjects();
        initButtons();
        initFont();
        startNewGame();
    }

    private void initResources() {
        mainAtlas = new TextureAtlas("textures/mainAtlas.pack");
        atlas = new TextureAtlas("textures/mainMenuAtlas.pack");
        messageGameOver = new MessageGameOver(mainAtlas);
    }

    private void initBackground() {
        bgTexture = new Texture(pathToBackground);
        background = new Background(new TextureRegion(bgTexture));
        starRegion = atlas.findRegion("starSprite");
        star = new Star[STAR_COUNT];
    }

    private void initSounds() {
        shoot = Gdx.audio.newSound(Gdx.files.internal("soundFX/shot.ogg"));
        enemyShot = Gdx.audio.newSound(Gdx.files.internal("soundFX/enemyShot.ogg"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("soundFX/explosion.ogg"));
        rocket = Gdx.audio.newSound(Gdx.files.internal("soundFX/rocket.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal(pathToMusic));
        targeting = Gdx.audio.newSound(Gdx.files.internal("soundFX/target.ogg"));
        teleport = Gdx.audio.newSound(Gdx.files.internal("soundFX/teleport.ogg"));
    }

    private void initGameObjects() {
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(mainAtlas, explosionSound);
        hpBar = new HPBar(mainAtlas.findRegion("hpBar"), worldBounds);
        hpBarBorder = new HPBarBorder(mainAtlas.findRegion("hpBorder"));
        player = new SpaceShip(mainAtlas, bulletPool, shoot, worldBounds, explosionPool, hpBar);
        background.setPlayer(player);
        rocketPool = new RocketPool(mainAtlas, player, explosionPool);
        targetPool = new TargetPool(mainAtlas, explosionPool, player, targeting);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, enemyShot, rocket,
                rocketPool, player, targetPool, teleport);
        enemyEmitter = new EnemyEmitter(mainAtlas, worldBounds, enemyPool, typeOfEnemies,
                enemyTypeData, maxEnemiesOnScreen, difficulty, countGeneratedEnemies);
    }

    private void initButtons() {
        buttonNewGame = new ButtonNewGame(atlas, this, BUTTON_TOUCH_SCALE);
        buttonMainMenu = new ButtonMainMenu(atlas, this, BUTTON_TOUCH_SCALE);
        buttonYes = new ButtonYes(atlas, this, BUTTON_TOUCH_SCALE);
        buttonNo = new ButtonNo(atlas, this, BUTTON_TOUCH_SCALE);
        destroyExcessButtons();
    }

    private void destroyExcessButtons() {
        buttonNewGame.destroy();
        buttonMainMenu.destroy();
        buttonYes.destroy();
        buttonNo.destroy();
    }

    private void initFont() {
        font = new Font("font/font.fnt", "font/font.png");
        font.setWorldSize(FONT_SIZE);
        level = (level + levelName);
        if (difficulty.equals("SURVIVAL")) {
            level = difficulty;
        }
    }

    private void startNewGame() {
        stopSound();
        player.startNewGame();
        state = State.STARTING;
        font.setWorldSize(TITLE_SIZE * 0.8f);
        startingTimer = 0;
        frags = 0;
        score = 0;
        targetPool.destroyAllActiveObjects();
        bulletPool.destroyAllActiveObjects();
        enemyPool.destroyAllActiveObjects();
        rocketPool.destroyAllActiveObjects();
    }

    private void stopSound() {
        targeting.stop();
        shoot.stop();
        enemyShot.stop();
        teleport.stop();
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

    public void update(float delta) {
        gameOverCheck();
        updateStars(delta);
        explosionPool.updateActiveSprites(delta);
        hpBar.update(delta);
        switch (state) {
            case STARTING:
                musicIncreaseVolume(delta);
                startingTimer += delta;
                if (startingTimer >= STARTING_INTERVAL) {
                    state = State.PLAYING;
                    font.setWorldSize(FONT_SIZE);
                }
                break;
            case PLAYING:
                updateGameplay(delta);
                break;
            case NEXT_LEVEL:
                changingLevel(delta);
                break;
            case GAME_OVER:
                messageGameOver.update(delta);
                buttonNewGame.update(delta);
                buttonMainMenu.update(delta);
                break;
            case GO_TO_MAIN:
                goToMain(delta);
            case PAUSE:
                buttonYes.update(delta);
                buttonNo.update(delta);
                break;
        }
    }

    private void gameOverCheck() {
        if (state == State.PLAYING) {
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

    private void updateGameplay(float delta) {
        musicIncreaseVolume(delta);
        player.update(delta);
        updatePools(delta);
        generateEnemy(delta);
        background.update(delta);
        upgradeSurvivalDifficulty(delta);
    }

    private void musicIncreaseVolume(float delta) {
        musicStartToPlay(delta);
        musicVolumeUp(delta);
    }

    private void musicStartToPlay(float delta) {
        if (!gameMusic.isPlaying()) {
            musicTimer += delta;
            if (musicTimer >= MUSIC_START) {
                musicTimer = 0;
                gameMusic.setVolume(0f);
                gameMusic.setLooping(true);
                gameMusic.play();
                gameMusic.setPosition(0.08f);
            }
        }
    }

    private void musicVolumeUp(float delta) {
        if (gameMusic.getVolume() <= MUSIC_END_VOLUME && gameMusic.isPlaying()) {
            musicTimer += delta;
            if (musicTimer >= MUSIC_INTERVAL) {
                musicTimer = 0;
                gameMusic.setVolume(gameMusic.getVolume() + MUSIC_DELTA);
            }
        }
    }

    private void updatePools(float delta) {
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        rocketPool.updateActiveSprites(delta);
        targetPool.updateActiveSprites(delta);
    }

    private void generateEnemy(float delta) {
        if (enemyPool.getActiveObjects().size() + frags < fragsToNextLevel ||
                fragsToNextLevel == 0) {
            if (fragsToNextLevel - enemyPool.getActiveObjects().size() -
                    frags < countGeneratedEnemies && fragsToNextLevel != 0) {
                countGeneratedEnemies = (byte) (fragsToNextLevel -
                        enemyPool.getActiveObjects().size() - frags);
                enemyEmitter.setCountGeneratedEnemies(countGeneratedEnemies);
            }
            enemyEmitter.randomGenerator(delta);
        }
    }

    private void upgradeSurvivalDifficulty(float delta) {
        if (difficulty.equals(SURVIVAL)) {
            upgradeDifficultyTimer += delta;
            if (upgradeDifficultyTimer >= UPGRADE_DIFFICULTY_INTERVAL) {
                upgradeDifficultyTimer = 0;
                enemyEmitter.upgradeDifficulty();
            }
        }
    }

    private void changingLevel(float delta) {
        changeLevelTimer += delta;
        if (gameMusic.getVolume() >= delta * 0.1f) {
            gameMusic.setVolume(gameMusic.getVolume() - delta * 0.1f);
        }
        if (changeLevelTimer >= CHANGE_LEVEL_INTERVAL) {
            changingScreenTimer += delta;
            if (changingScreenTimer >= CHANGING_SCREEN_INTERVAL) {
                changingScreenTimer = 0;
                fogging -= 0.017f;
                if (fogging <= -0.01f && canSwitch) {
                    canSwitch = false;
                    levelGenerator.levelDone(difficulty, score);
                }
            }
        }
    }

    private void goToMain(float delta) {
        changingScreenTimer += delta;
        if (changingScreenTimer >= CHANGING_SCREEN_INTERVAL) {
            changingScreenTimer = 0;
            fogging -= 0.017f;
            if (fogging <= -0.01f && canSwitch) {
                canSwitch = false;
                game.setScreen(new MainMenu(game));
            }
        }
    }

    public void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        List<Rocket> rocketList = rocketPool.getActiveObjects();
        checkBodyCollisions(enemyList);
        checkEnemyHitCollisions(enemyList, bulletList);
        checkPlayerHitCollisions(bulletList);
        checkRocketHitToPlayerCollisions(rocketList);
        checkPlayerHitToRocketCollisions(rocketList, bulletList);
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
                        frags++;
                        checkCompletingLevel();
                    }
                }
            }
        }
    }

    protected void checkCompletingLevel() {
        if (frags >= fragsToNextLevel && fragsToNextLevel != 0) {
            state = State.NEXT_LEVEL;
            font.setWorldSize(TITLE_SIZE * 0.8f);
        }
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

    private void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
        rocketPool.freeAllDestroyedActiveSprites();
        targetPool.freeAllDestroyedActiveSprites();
    }

    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        drawStars(batch);
        drawGameObjects(batch);
        drawHPBar(batch);
        drawYesNoBtns(batch);
        printInfo();
        gameOver(batch);
        batch.end();
    }

    private void drawStars(SpriteBatch batch) {
        for (int i = 0; i < starAdded; i++) {
            star[i].draw(batch);
        }
    }

    private void drawGameObjects(SpriteBatch batch) {
        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        explosionPool.drawActiveSprites(batch);
        rocketPool.drawActiveSprites(batch);
        player.draw(batch);
        targetPool.drawActiveSprites(batch);
    }

    private void drawHPBar(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, fogging * 0.7f);
        hpBarBorder.draw(batch);
        batch.setColor(1f - player.getHp() / PLAYER_MAX_HP, player.getHp() / PLAYER_MAX_HP,
                0f, fogging * 0.7f);
        hpBar.draw(batch);
        batch.setColor(1f, 1f, 1f, fogging);
    }

    private void drawYesNoBtns(SpriteBatch batch) {
        buttonNo.draw(batch);
        buttonYes.draw(batch);
    }

    public void printInfo() {
        switch (state) {
            case STARTING:
                openLevelInfo();
                break;
            case NEXT_LEVEL:
                changeLevelInfo();
                break;
            case PAUSE:
                pauseInfo();
                break;
            case GO_TO_MAIN:
                font.setColor(1f, 1f, 1f, fogging);
            default:
                defaultInfo();
        }
    }

    private void openLevelInfo() {
        if (startingTimer >= STARTING_INTERVAL / 2f) {
            font.draw(batch, GET_READY, worldBounds.pos.x, worldBounds.pos.y +
                    TITLE_SIZE * 1.5f, Align.center);
        } else {
            font.draw(batch, level, worldBounds.pos.x, worldBounds.pos.y +
                    TITLE_SIZE * 1.5f, Align.center);
        }
    }

    private void changeLevelInfo() {
        font.draw(batch, COMPLETE, worldBounds.pos.x, worldBounds.pos.y +
                TITLE_SIZE * 1.2f, Align.center);
        font.setColor(1f, 1f, 1f, fogging);
    }

    private void pauseInfo() {
        font.draw(batch, PAUSED, worldBounds.pos.x, worldBounds.pos.y +
                TITLE_SIZE * 1.2f, Align.center);
        if (!buttonYes.isDestroyed()) {
            font.setWorldSize(FONT_SIZE);
            font.draw(batch, GO_TO_MAIN_MENU, worldBounds.pos.x,
                    worldBounds.pos.y - MAIN_MENU_MESSAGE_TOP_MARGIN, Align.center);
            font.setWorldSize(TITLE_SIZE * 0.8f);
        }
    }

    private void defaultInfo() {
        sbScore.setLength(7);
        font.draw(batch, sbScore.append(score), worldBounds.getLeft(),
                worldBounds.getTop());
        font.draw(batch, level, worldBounds.getRight(),
                worldBounds.getTop(), Align.right);
    }

    private void gameOver(SpriteBatch batch) {
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
            buttonMainMenu.draw(batch);
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        player.resize(worldBounds);
        starResize(worldBounds);
        hpBar.resize(worldBounds);
        hpBarBorder.pos.set(hpBar.pos);
    }

    private void starResize(Rect worldBounds) {
        for (int i = 0; i < starAdded; i++) {
            star[i].resize(worldBounds);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        bgTexture.dispose();
        atlas.dispose();
        mainAtlas.dispose();
        disposePools();
        disposeSounds();
        font.dispose();
    }

    private void disposeSounds() {
        gameMusic.dispose();
        shoot.dispose();
        explosionSound.dispose();
        enemyShot.dispose();
        rocket.dispose();
        targeting.dispose();
        teleport.dispose();
    }

    private void disposePools() {
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();
        rocketPool.dispose();
        targetPool.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.PLAYING) {
            player.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            player.keyUp(keycode);
        }
        if (state != State.GAME_OVER && state != State.GO_TO_MAIN) {
            if (keycode == Input.Keys.BACK) {
                buttonYes.flushDestroy();
                buttonNo.flushDestroy();
                pause();
                resume();
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        switch (state) {
            case PLAYING:
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
    public void pause() {
        super.pause();
        if (state != State.GAME_OVER && state != State.GO_TO_MAIN && state != State.PAUSE) {
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
    public boolean touchUp(Vector2 touch, int pointer) {
        switch (state) {
            case PAUSE:
                pauseTouch(touch, pointer);
                break;
            case PLAYING:
                player.touchUp(touch, pointer);
                break;
            case GAME_OVER:
                buttonNewGame.touchUp(touch, pointer);
                buttonMainMenu.touchUp(touch, pointer);
        }
        return false;
    }

    private void pauseTouch(Vector2 touch, int pointer) {
        if (!buttonYes.isDestroyed()) {
            buttonYes.touchUp(touch, pointer);
            if (buttonNo.touchUp(touch, pointer)) {
                touchUp(touch, pointer);
            }
        } else {
            if (temp == State.PLAYING) {
                font.setWorldSize(FONT_SIZE);
            }
            state = temp;
            temp = null;
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
            messageGameOver.destroy();
            buttonNewGame.destroy();
        }
    }

    private void mainMenuAction(Object src) {
        if (src == buttonMainMenu) {
            state = State.GO_TO_MAIN;
        }
    }

    private void yesAction(Object src) {
        if (src == buttonYes) {
            buttonYes.disappear();
            buttonNo.disappear();
            if (temp == State.PLAYING || temp == State.STARTING || temp == State.NEXT_LEVEL) {
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
