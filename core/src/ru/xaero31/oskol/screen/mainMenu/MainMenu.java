package ru.xaero31.oskol.screen.mainMenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import ru.xaero31.oskol.base.ActionListener;
import ru.xaero31.oskol.base.Base2DScreen;
import ru.xaero31.oskol.base.Font;
import ru.xaero31.oskol.base.LevelGenerator;
import ru.xaero31.oskol.base.ScaledTouchUpButton;
import ru.xaero31.oskol.math.Rect;
import ru.xaero31.oskol.screen.gameScreen.GameScreen;
import ru.xaero31.oskol.screen.mainMenu.buttons.mainPageButtons.ButtonContinue;
import ru.xaero31.oskol.screen.mainMenu.buttons.mainPageButtons.ButtonExit;
import ru.xaero31.oskol.screen.mainMenu.buttons.mainPageButtons.ButtonNewGame;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonBack;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonEasy;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonNightmare;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonNo;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonNormal;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonSurvival;
import ru.xaero31.oskol.screen.mainMenu.buttons.newGameButtons.ButtonYes;
import ru.xaero31.oskol.screen.sprites.Background;
import ru.xaero31.oskol.screen.sprites.Star;
import ru.xaero31.oskol.screen.titles.HowToPlay;
import ru.xaero31.oskol.utils.DataBaseHelper;

public class MainMenu extends Base2DScreen implements ActionListener {
    private final String none = "None";
    private final String easy = "EASY";
    private final String normal = "NORMAL";
    private final String nightmare = "HARD";
    private final String lost = "It will erase your saved game";
    private final String continueText = "Do you want to continue?";
    private final float BUTTON_PRESS_SCALE = 0.9f;
    private final float BUTTON_HEIGHT = 55f;
    private final float STAR_INTERVAL = 0.02f;
    private final float FONT_HEIGHT = 32f;
    private final int STAR_COUNT = 50;

    private enum ButtonPressed {CONTINUE, EXIT, EASY, NORMAL, HARD, SURVIVAL}
    private ButtonPressed buttonPressed;
    private LevelGenerator levelGenerator;

    private StringBuilder scores;
    private String savedDifficulty;
    private String topScore;
    private float changingScreenInterval = 0.025f;
    private float changingScreenTimer = 0f;
    private float fogging = 1f;
    private float starTimer;
    private byte starAdded = 0;
    private boolean changingScreen;

    private Background background;
    private Texture bgTexture;
    private TextureAtlas atlas;
    private TextureRegion starRegion;
    private RankSprite silver;
    private RankSprite goldNova;
    private RankSprite eagleMaster;
    private RankSprite globalElite;

    private Font font;
    private Star[] star;
    private Music menuMusic;

    private ScaledTouchUpButton[] buttons;
    private ButtonExit buttonExit;
    private ButtonNewGame buttonNewGame;
    private ButtonContinue buttonContinue;
    private ButtonBack buttonBack;
    private ButtonEasy buttonEasy;
    private ButtonNightmare buttonNightmare;
    private ButtonNormal buttonNormal;
    private ButtonSurvival buttonSurvival;
    private ButtonYes buttonYes;
    private ButtonNo buttonNo;

    public MainMenu(Game game) {
        super(game);
    }

    @Override
    public void show () {
        super.show();
        prepareGame();
        initBackground();
        initRanks();
        setRank();
        initFont();
        initMusic();
        showButtons();
    }

    private void prepareGame() {
        levelGenerator = DataBaseHelper.getLevelGenerator();
        levelGenerator.setGame(game);
        savedDifficulty = levelGenerator.getSavedDifficulty();
        topScore = String.valueOf(levelGenerator.getTopScore());
        scores = new StringBuilder("Top Score: ");
        scores.append(topScore);
    }

    private void initBackground() {
        bgTexture = new Texture("textures/worldOneBackground.png");
        background = new Background(new TextureRegion(bgTexture));
        atlas = new TextureAtlas("textures/mainMenuAtlas.pack");
        starRegion = atlas.findRegion("starSprite");
        star = new Star[STAR_COUNT];
    }

    private void initRanks() {
        silver = new RankSprite(atlas.findRegion("silverRank"));
        goldNova = new RankSprite(atlas.findRegion("goldNovaRank"));
        eagleMaster = new RankSprite(atlas.findRegion("eagleMasterRank"));
        globalElite = new RankSprite(atlas.findRegion("globalEliteRank"));
    }

    private void setRank() {
        switch (savedDifficulty) {
            case none:
                silver.flushDestroy();
                goldNova.destroy();
                eagleMaster.destroy();
                globalElite.destroy();
                break;
            case easy:
                silver.destroy();
                goldNova.flushDestroy();
                eagleMaster.destroy();
                globalElite.destroy();
                break;
            case normal:
                silver.destroy();
                goldNova.destroy();
                eagleMaster.flushDestroy();
                globalElite.destroy();
                break;
            case nightmare:
                silver.destroy();
                goldNova.destroy();
                eagleMaster.destroy();
                globalElite.flushDestroy();
                break;
        }
    }

    private void initFont() {
        font = new Font("font/font.fnt", "font/font.png");
        font.setColor(1f, 1f, 1f, 0f);
        font.setTransparency(0f);
        font.setWorldSize(FONT_HEIGHT);
    }

    private void initMusic() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menuMusic.mp3"));
        menuMusic.setVolume(0.4f);
        menuMusic.setLooping(true);
        menuMusic.play();
    }

    private void showButtons() {
        initButtons();
        setButtonsHeight();
        destroyExcessButtons();
        addButtonsToArray();
    }

    private void initButtons() {
        buttonExit = new ButtonExit(atlas, this, BUTTON_PRESS_SCALE);
        buttonNewGame = new ButtonNewGame(atlas, this, BUTTON_PRESS_SCALE);
        buttonContinue = new ButtonContinue(atlas, this, BUTTON_PRESS_SCALE);
        buttonBack = new ButtonBack(atlas, this, BUTTON_PRESS_SCALE);
        buttonEasy = new ButtonEasy(atlas, this, BUTTON_PRESS_SCALE);
        buttonNightmare = new ButtonNightmare(atlas, this, BUTTON_PRESS_SCALE);
        buttonNormal = new ButtonNormal(atlas, this, BUTTON_PRESS_SCALE);
        buttonSurvival = new ButtonSurvival(atlas, this, BUTTON_PRESS_SCALE);
        buttonYes = new ButtonYes(atlas, this, BUTTON_PRESS_SCALE);
        buttonNo = new ButtonNo(atlas, this, BUTTON_PRESS_SCALE);
    }

    private void setButtonsHeight() {
        buttonExit.setHeightProportion(BUTTON_HEIGHT);
        buttonNewGame.setHeightProportion(BUTTON_HEIGHT);
        buttonContinue.setHeightProportion(BUTTON_HEIGHT);
        buttonBack.setHeightProportion(BUTTON_HEIGHT);
        buttonEasy.setHeightProportion(BUTTON_HEIGHT);
        buttonNightmare.setHeightProportion(BUTTON_HEIGHT);
        buttonNormal.setHeightProportion(BUTTON_HEIGHT);
        buttonSurvival.setHeightProportion(BUTTON_HEIGHT);
        buttonYes.setHeightProportion(BUTTON_HEIGHT);
        buttonNo.setHeightProportion(BUTTON_HEIGHT);
    }

    private void destroyExcessButtons() {
        buttonEasy.destroy();
        buttonNightmare.destroy();
        buttonNormal.destroy();
        buttonSurvival.destroy();
        buttonYes.destroy();
        buttonNo.destroy();
        buttonBack.destroy();
    }

    private void addButtonsToArray() {
        buttons = new ScaledTouchUpButton[10];
        int i = 0;
        if (i < buttons.length) {
            buttons[i++] = buttonExit;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonNewGame;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonContinue;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonBack;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonEasy;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonNightmare;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonNormal;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonSurvival;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonYes;
        }
        if (i < buttons.length) {
            buttons[i++] = buttonNo;
        }
    }

    @Override
    public void render (float delta) {
        super.render(delta);
        update(delta);
        draw();
    }

    public void update(float delta) {
        starAdd(delta);
        font.update(delta);
        for (int i = 0; i < starAdded; i++) {
            star[i].update(delta);
        }
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].update(delta);
        }
        if (changingScreen) {
            changeScreen(delta);
        }
    }

    private void starAdd(float delta) {
        if (starAdded < STAR_COUNT) {
            starTimer += delta;
            if (starTimer >= STAR_INTERVAL) {
                starTimer = 0;
                star[starAdded] = new Star(starRegion, 1, 19, 19, worldBounds);
                starAdded++;
            }
        }
    }

    private void changeScreen(float delta) {
        changingScreenTimer += delta;
        if (changingScreenTimer >= changingScreenInterval && fogging >= 0) {
            changingScreenTimer = 0;
            fogging -= 0.017f;
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setTransparency(fogging);
            }
            font.setColor(1f, 1f, 1f, fogging);
            if (fogging - 0.017f < 0) {
                switch (buttonPressed) {
                    case EASY:
                    case NORMAL:
                    case HARD:
                        launchNewGame();
                        break;
                    case SURVIVAL:
                        launchSurvivalMode();
                        break;
                    case CONTINUE:
                        launchSavedGame();
                        break;
                    case EXIT:
                        Gdx.app.exit();
                }
            }
        }
    }

    private void launchNewGame() {
        levelGenerator.resetGame();
        levelGenerator.changeLevel();
        if (savedDifficulty.equals(none) && canSwitch) {
            canSwitch = false;
            game.setScreen(new HowToPlay(game));
        }
        if (!savedDifficulty.equals(none) && canSwitch) {
            canSwitch = false;
            game.setScreen(new GameScreen(game, buttonPressed.toString()));
        }
    }

    private void launchSurvivalMode() {
        levelGenerator.resetGame();
        levelGenerator.survivalMode();
        if (canSwitch) {
            canSwitch = false;
            game.setScreen(new GameScreen(game, buttonPressed.toString()));
        }
    }

    private void launchSavedGame() {
        levelGenerator.levelReset();
        levelGenerator.loadSave();
        if (levelGenerator.getLevelPassed() >= 4 && canSwitch) {
            canSwitch = false;
            return;
        }
        levelGenerator.changeLevel();
        if (canSwitch) {
            canSwitch = false;
            game.setScreen(new GameScreen(game,
                    levelGenerator.getCurrentDifficulty()));
        }
    }

    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        buttonsDraw();
        starsDraw();
        drawText();
        drawRank();
        batch.setColor(1, 1, 1, fogging);
        batch.end();
    }

    private void buttonsDraw() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].draw(batch);
        }
    }

    private void starsDraw() {
        for (int i = 0; i < starAdded; i++) {
            star[i].draw(batch);
        }
    }

    private void drawText() {
        if (!buttonYes.isDestroyed()) {
            font.setColor(1f, 1f, 1f, font.getTransparency());
            font.draw(batch, lost, worldBounds.pos.x, 250f, Align.center);
            font.draw(batch, continueText, worldBounds.pos.x, 200f, Align.center);
        }
        font.setColor(1f, 1f, 1f, fogging);
        if (changingScreen) {
            font.setTransparency(fogging);
        }
        font.draw(batch, scores, worldBounds.getLeft() + 10f, worldBounds.getTop() - 10f,
                Align.left);
    }

    private void drawRank() {
        silver.draw(batch);
        goldNova.draw(batch);
        eagleMaster.draw(batch);
        globalElite.draw(batch);
    }

    @Override
    public void dispose () {
        super.dispose();
        bgTexture.dispose();
        atlas.dispose();
        menuMusic.dispose();
        font.dispose();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        buttonsResize(worldBounds);
        starsResize(worldBounds);
        rankResize(worldBounds);
    }

    private void buttonsResize(Rect worldBounds) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].resize(worldBounds);
        }
    }

    private void starsResize(Rect worldBounds) {
        for (int i = 0; i < starAdded; i++) {
            star[i].resize(worldBounds);
        }
    }

    private void rankResize(Rect worldBounds) {
        silver.resize(worldBounds);
        goldNova.resize(worldBounds);
        eagleMaster.resize(worldBounds);
        globalElite.resize(worldBounds);
    }
    
    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].touchDown(touch, pointer);
        }
        return super.touchDown(touch, pointer);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        for (int i = 0; i < buttons.length; i++) {
           buttons[i].touchUp(touch, pointer);
        }
        return super.touchUp(touch, pointer);
    }

    @Override
    public boolean keyUp(int keycode) {
        super.keyUp(keycode);
        if (keycode == Input.Keys.BACK) {
            if (!buttonNo.isDestroyed()) {
                actionPerformed(buttonNo);
            }
            if (!buttonBack.isDestroyed()) {
                actionPerformed(buttonBack);
            }
            if (!buttonExit.isDestroyed()) {
                actionPerformed(buttonExit);
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(Object src) {
        mainMenuAction(src);
        yesNoAction(src);
        difficultyMenuAction(src);
    }

    private void mainMenuAction(Object src) {
        if (src == buttonNewGame) {
            newGame(savedDifficulty);
        }
        if (src == buttonExit) {
            buttonPressed = ButtonPressed.EXIT;
            changingScreen = true;
        }
        if (src == buttonContinue) {
            buttonPressed = ButtonPressed.CONTINUE;
            changingScreen = true;
        }
    }

    private void newGame(String difficulty) {
        mainScreenDestroyButtons();
        if (difficulty.equals(none)) {
            if (levelGenerator.getWorldPassed() == 0) {
                buttonPressed = ButtonPressed.EASY;
                changingScreen = true;
            } else {
                buttonPressed = ButtonPressed.EASY;
                buttonNewGame.toAppearButton(buttonYes);
                buttonNewGame.toAppearButton(buttonNo);
                buttonNewGame.fontToShow(font);
                return;
            }
        }
        newGameShowButtons(difficulty, buttonNewGame);
    }

    private void yesNoAction(Object src) {
        if (src == buttonYes) {
            levelGenerator.clearSave();
            changingScreen = true;
        }
        if (src == buttonNo) {
            yesNoDestroyButtons();
            font.disappear();
            if (savedDifficulty.equals(none)) {
                buttonNo.toAppearButton(buttonNewGame);
                buttonNo.toAppearButton(buttonContinue);
                buttonNo.toAppearButton(buttonExit);
            } else {
                newGameShowButtons(savedDifficulty, buttonNo);
            }
        }
    }

    private void difficultyMenuAction(Object src) {
        backAction(src);
        easyAction(src);
        normalAction(src);
        nightmareAction(src);
        survivalAction(src);
    }

    private void backAction(Object src) {
        if (src == buttonBack) {
            newGameDestroyButtons();
            buttonBack.toAppearButton(buttonNewGame);
            buttonBack.toAppearButton(buttonContinue);
            buttonBack.toAppearButton(buttonExit);
        }
    }

    private void easyAction(Object src) {
        if (src == buttonEasy) {
            buttonPressed = ButtonPressed.EASY;
            newGameDestroyButtons();
            yesNoShowButtons(buttonEasy);
            buttonEasy.fontToShow(font);
        }
    }

    private void normalAction(Object src) {
        if (src == buttonNormal) {
            buttonPressed = ButtonPressed.NORMAL;
            newGameDestroyButtons();
            yesNoShowButtons(buttonNormal);
            buttonEasy.fontToShow(font);
        }
    }

    private void nightmareAction(Object src) {
        if (src == buttonNightmare) {
            buttonPressed = ButtonPressed.HARD;
            newGameDestroyButtons();
            yesNoShowButtons(buttonNightmare);
            buttonEasy.fontToShow(font);
        }
    }

    private void survivalAction(Object src) {
        if (src == buttonSurvival) {
            buttonPressed = ButtonPressed.SURVIVAL;
            newGameDestroyButtons();
            yesNoShowButtons(buttonSurvival);
            buttonSurvival.fontToShow(font);
        }
    }

    private void mainScreenDestroyButtons() {
        buttonNewGame.disappear();
        buttonContinue.disappear();
        buttonExit.disappear();
    }

    private void newGameShowButtons(String difficulty, ScaledTouchUpButton btn) {
        switch (difficulty) {
            case nightmare:
            case normal:
                btn.toAppearButton(buttonNightmare);
            case easy:
                btn.toAppearButton(buttonSurvival);
                btn.toAppearButton(buttonEasy);
                btn.toAppearButton(buttonNormal);
                btn.toAppearButton(buttonBack);
        }
    }

    private void newGameDestroyButtons() {
        buttonBack.disappear();
        buttonEasy.disappear();
        buttonNormal.disappear();
        buttonNightmare.disappear();
        buttonSurvival.disappear();
    }

    private void yesNoShowButtons(ScaledTouchUpButton btn) {
        btn.toAppearButton(buttonYes);
        btn.toAppearButton(buttonNo);
    }

    private void yesNoDestroyButtons() {
        buttonYes.disappear();
        buttonNo.disappear();
    }
}
