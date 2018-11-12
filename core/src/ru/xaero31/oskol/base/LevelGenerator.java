package ru.xaero31.oskol.base;

import com.badlogic.gdx.Game;

import java.util.Random;

import ru.xaero31.oskol.screen.bossLevel.BossLevelOne;
import ru.xaero31.oskol.screen.bossLevel.BossLevelThree;
import ru.xaero31.oskol.screen.bossLevel.BossLevelTwo;
import ru.xaero31.oskol.screen.gameScreen.GameScreen;
import ru.xaero31.oskol.screen.titles.EndEasyScreen;
import ru.xaero31.oskol.screen.titles.EndHardScreen;
import ru.xaero31.oskol.screen.titles.EndNormalScreen;
import ru.xaero31.oskol.utils.DataBaseHelper;

public class LevelGenerator {
    private final String none = "None";
    private final String easy = "EASY";
    private final String normal = "NORMAL";
    private final String nightmare = "HARD";

    private Random rnd = new Random();
    private Game game;

    private String levelMusic;
    private String levelBackground;
    private String levelName;
    private String savedDifficulty;
    private String currentDifficulty;
    private int fragsToNextLevel;
    private int topScore;
    private byte typeOfEnemies;
    private byte enemyTypeData;
    private byte maxEnemiesOnScreen;
    private byte levelPassed;
    private byte worldPassed;
    private byte countGeneratedEnemies;

    public LevelGenerator() {
        DataBaseHelper.prepareDataBase();
        savedDifficulty = DataBaseHelper.loadDifficulty();
        topScore = DataBaseHelper.loadTopScore();
        worldPassed = DataBaseHelper.loadWorldPassed();
        levelPassed = DataBaseHelper.loadLevelPassed();
        currentDifficulty = DataBaseHelper.loadCurrentDifficulty();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void setBossLevel(String difficulty) {
        switch (worldPassed) {
            case 0:
                game.setScreen(new BossLevelOne(game, difficulty));
                break;
            case 1:
                game.setScreen(new BossLevelTwo(game, difficulty));
                break;
            case 2:
                game.setScreen(new BossLevelThree(game, difficulty));
                break;
        }
    }

    private void setFinalScreen(String difficulty) {
        switch (savedDifficulty) {
            case none:
                savedDifficulty = difficulty;
                game.setScreen(new EndEasyScreen(game));
                break;
            case easy:
                if (difficulty.equals(normal)) {
                    savedDifficulty = difficulty;
                    game.setScreen(new EndNormalScreen(game));
                    return;
                }
            default:
                savedDifficulty = difficulty;
                game.setScreen(new EndHardScreen(game));
        }
    }

    public void levelDone(String difficulty, int score) {
        checkScores(score);
        DataBaseHelper.saveCurrentDifficulty(difficulty);
        DataBaseHelper.saveLevelPassed(++levelPassed);
        if (levelPassed < 4) {
            changeLevel();
            game.setScreen(new GameScreen(game, difficulty));
        } else {
            setBossLevel(difficulty);
        }
    }

    public void worldDone(String difficulty, int score) {
        if (++worldPassed < 3) {
            DataBaseHelper.saveWorldPassed(worldPassed);
        }
        checkScores(score);
        levelReset();
        changeLevel();
        DataBaseHelper.saveCurrentDifficulty(difficulty);
        DataBaseHelper.saveLevelPassed(levelPassed);
        game.setScreen(new GameScreen(game, difficulty));
    }

    public void gameDone(String difficulty, int score) {
        saveDifficulty(difficulty);
        checkScores(score);
        resetGame();
        clearSave();
        setFinalScreen(difficulty);
    }

    public void checkScores(int newScore) {
        if (newScore > topScore) {
            topScore = newScore;
            DataBaseHelper.saveTopScore(topScore);
        }
    }

    private void saveDifficulty(String difficulty) {
        switch (difficulty) {
            case easy:
                if (savedDifficulty.equals(none)) {
                    DataBaseHelper.saveDifficulty(difficulty);
                }
                break;
            case normal:
                if (savedDifficulty.equals(easy)) {
                    DataBaseHelper.saveDifficulty(difficulty);
                }
                break;
            case nightmare:
                if (savedDifficulty.equals(normal)) {
                    DataBaseHelper.saveDifficulty(difficulty);
                }
                break;
        }
    }

    public void loadSave() {
        worldPassed = DataBaseHelper.loadWorldPassed();
        levelPassed = DataBaseHelper.loadLevelPassed();
        currentDifficulty = DataBaseHelper.loadCurrentDifficulty();
        if (levelPassed >= 4) {
            setBossLevel(currentDifficulty);
        }
    }

    public void clearSave() {
        DataBaseHelper.saveWorldPassed((byte) 0);
        DataBaseHelper.saveLevelPassed((byte) 0);
        DataBaseHelper.saveCurrentDifficulty(none);
    }

    public void levelReset() {
        levelPassed = 0;
    }

    public void resetGame() {
        levelPassed = 0;
        worldPassed = 0;
        currentDifficulty = none;
    }

    public void changeLevel() {
        switch (worldPassed) {
            case 0:
                chooseLevelWorldOne(levelPassed);
                break;
            case 1:
                chooseLevelWorldTwo(levelPassed);
                break;
            case 2:
                chooseLevelWorldThree(levelPassed);
                break;
        }
    }

    public void survivalMode() {
        switch (rnd.nextInt(3)) {
            case 0:
                levelMusic = "music/worldOneMusic.mp3";
                levelBackground = "textures/worldOneBackground.png";
                break;
            case 1:
                levelMusic = "music/worldTwoMusic.mp3";
                levelBackground = "textures/worldTwoBackground.png";
                break;
            case 2:
                levelMusic = "music/worldThreeMusic.mp3";
                levelBackground = "textures/worldThreeBackground.png";
                break;
        }
        levelName = "";
        fragsToNextLevel = 0;
        typeOfEnemies = 3;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 2;
        countGeneratedEnemies = 1;
    }

    private void chooseLevelWorldOne(byte levelPassed) {
        levelMusic = "music/worldOneMusic.mp3";
        levelBackground = "textures/worldOneBackground.png";
        switch (levelPassed) {
            case 0:
                setToWorldOneLevelOne();
                break;
            case 1:
                setToWorldOneLevelTwo();
                break;
            case 2:
                setToWorldOneLevelThree();
                break;
            case 3:
                setToWorldOneLevelFour();
                break;
        }
    }

    private void chooseLevelWorldTwo(byte levelPassed) {
        levelMusic = "music/worldTwoMusic.mp3";
        levelBackground = "textures/worldTwoBackground.png";
        switch (levelPassed) {
            case 0:
                setToWorldTwoLevelOne();
                break;
            case 1:
                setToWorldTwoLevelTwo();
                break;
            case 2:
                setToWorldTwoLevelThree();
                break;
            case 3:
                setToWorldTwoLevelFour();
                break;
        }
    }

    private void chooseLevelWorldThree(byte levelPassed) {
        levelMusic = "music/worldThreeMusic.mp3";
        levelBackground = "textures/worldThreeBackground.png";
        switch (levelPassed) {
            case 0:
                setToWorldThreeLevelOne();
                break;
            case 1:
                setToWorldThreeLevelTwo();
                break;
            case 2:
                setToWorldThreeLevelThree();
                break;
            case 3:
                setToWorldThreeLevelFour();
                break;
        }
    }

    private void setToWorldOneLevelOne() {
        levelName = "1-1";
        fragsToNextLevel = 100;
        typeOfEnemies = 1;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 3;
        countGeneratedEnemies = 1;
    }

    private void setToWorldOneLevelTwo() {
        levelName = "1-2";
        fragsToNextLevel = 100;
        typeOfEnemies = 2;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 3;
        countGeneratedEnemies = 1;
    }

    private void setToWorldOneLevelThree() {
        levelName = "1-3";
        fragsToNextLevel = 150;
        typeOfEnemies = 3;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 4;
        countGeneratedEnemies = 2;
    }

    private void setToWorldOneLevelFour() {
        levelName = "1-4";
        fragsToNextLevel = 200;
        typeOfEnemies = 3;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 5;
        countGeneratedEnemies = 2;
    }

    private void setToWorldTwoLevelOne() {
        levelName = "2-1";
        fragsToNextLevel = 100;
        typeOfEnemies = 1;
        enemyTypeData = 2;
        maxEnemiesOnScreen = 3;
        countGeneratedEnemies = 2;
    }

    private void setToWorldTwoLevelTwo() {
        levelName = "2-2";
        fragsToNextLevel = 150;
        typeOfEnemies = 2;
        enemyTypeData = 2;
        maxEnemiesOnScreen = 4;
        countGeneratedEnemies = 2;
    }

    private void setToWorldTwoLevelThree() {
        levelName = "2-3";
        fragsToNextLevel = 200;
        typeOfEnemies = 3;
        enemyTypeData = 2;
        maxEnemiesOnScreen = 5;
        countGeneratedEnemies = 2;
    }

    private void setToWorldTwoLevelFour() {
        levelName = "2-4";
        fragsToNextLevel = 200;
        typeOfEnemies = 3;
        enemyTypeData = 2;
        maxEnemiesOnScreen = 5;
        countGeneratedEnemies = 2;
    }

    private void setToWorldThreeLevelOne() {
        levelName = "3-1";
        fragsToNextLevel = 100;
        typeOfEnemies = 1;
        enemyTypeData = 3;
        maxEnemiesOnScreen = 3;
        countGeneratedEnemies = 2;
    }

    private void setToWorldThreeLevelTwo() {
        levelName = "3-2";
        fragsToNextLevel = 150;
        typeOfEnemies = 2;
        enemyTypeData = 3;
        maxEnemiesOnScreen = 3;
        countGeneratedEnemies = 2;
    }

    private void setToWorldThreeLevelThree() {
        levelName = "3-3";
        fragsToNextLevel = 200;
        typeOfEnemies = 8;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 4;
        countGeneratedEnemies = 2;
    }

    private void setToWorldThreeLevelFour() {
        levelName = "3-4";
        fragsToNextLevel = 200;
        typeOfEnemies = 8;
        enemyTypeData = 1;
        maxEnemiesOnScreen = 5;
        countGeneratedEnemies = 2;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelMusic() {
        return levelMusic;
    }

    public String getLevelBackground() {
        return levelBackground;
    }

    public String getSavedDifficulty() {
        return savedDifficulty;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    public int getFragsToNextLevel() {
        return fragsToNextLevel;
    }

    public int getTopScore() {
        return topScore;
    }

    public byte getTypeOfEnemies() {
        return typeOfEnemies;
    }

    public byte getEnemyTypeData() {
        return enemyTypeData;
    }

    public byte getMaxEnemiesOnScreen() {
        return maxEnemiesOnScreen;
    }

    public byte getCountGeneratedEnemies() {
        return countGeneratedEnemies;
    }

    public byte getLevelPassed() {
        return levelPassed;
    }

    public byte getWorldPassed() {
        return worldPassed;
    }
}
