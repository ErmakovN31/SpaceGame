package ru.xaero31.oskol.utils;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

import ru.xaero31.oskol.base.LevelGenerator;

public class DataBaseHelper {
    private static Database db;
    private static DatabaseCursor rs;
    private static String createDatabase = "create table if not exists 'Data' " +
            "('Parameter' text primary key not null, 'Value' text not null);";
    private static String insertDifficulty = "insert into Data ('Parameter', 'Value') VALUES " +
            "('Difficulty', 'None');";
    private static String insertTopScore = "insert into Data ('Parameter', 'Value') VALUES " +
            "('TopScore', '0');";
    private static String insertSavedWorld = "insert into Data ('Parameter', 'Value') VALUES " +
            "('WorldPassed', '0');";
    private static String insertSavedLevel = "insert into Data ('Parameter', 'Value') VALUES " +
            "('LevelPassed', '0');";
    private static String insertCurrentDifficulty = "insert into Data ('Parameter', 'Value') " +
            "VALUES ('CurrentDifficulty', 'None');";

    private static String checkTable = "SELECT count(*) From Data;";

    private static LevelGenerator levelGenerator = new LevelGenerator();

    public DataBaseHelper() {
    }

    public static void prepareDataBase() {
        db = DatabaseFactory.getNewDatabase("Data.s3db", 3,
                createDatabase,null);
        db.setupDatabase();
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery(checkTable);
            if (rs.next()) {
                if (rs.getInt(0) != 5) {
                    db.execSQL(insertSavedLevel);
                    db.execSQL(insertCurrentDifficulty);
                    db.execSQL(insertDifficulty);
                    db.execSQL(insertTopScore);
                    db.execSQL(insertSavedWorld);
                }
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static String loadDifficulty() {
        String difficulty = null;
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery("SELECT * FROM Data WHERE Parameter = 'Difficulty'");
            if (rs.next()) {
                difficulty = rs.getString(1);
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
        return difficulty;
    }

    public static int loadTopScore() {
        int topScore = 0;
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery("SELECT * FROM Data WHERE Parameter = 'TopScore'");
            if (rs.next() && rs.getString(1) != null) {
                topScore = Integer.valueOf(rs.getString(1));
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
        return topScore;
    }

    public static byte loadWorldPassed() {
        byte worldPassed = 0;
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery("SELECT * FROM Data WHERE Parameter = 'WorldPassed'");
            if (rs.next() && rs.getString(1) != null) {
                worldPassed = Byte.valueOf(rs.getString(1));
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
        return worldPassed;
    }

    public static byte loadLevelPassed() {
        byte levelPassed = 0;
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery("SELECT * FROM Data WHERE Parameter = 'LevelPassed'");
            if (rs.next()) {
                levelPassed = Byte.valueOf(rs.getString(1));
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
        return levelPassed;
    }

    public static String loadCurrentDifficulty() {
        String difficulty = null;
        try {
            db.openOrCreateDatabase();
            rs = db.rawQuery("SELECT * FROM Data WHERE Parameter = 'CurrentDifficulty'");
            if (rs.next()) {
                difficulty = rs.getString(1);
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
        return difficulty;
    }

    public static void saveDifficulty(String difficulty) {
        try {
            db.openOrCreateDatabase();
            db.execSQL("UPDATE Data SET Value = '" + difficulty +
                    "' WHERE Parameter = 'Difficulty'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveTopScore(int topScore) {
        try {
            db.openOrCreateDatabase();
            db.execSQL("UPDATE Data SET Value = '" + String.valueOf(topScore) +
                    "' WHERE Parameter = 'TopScore'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveWorldPassed(byte worldPassed) {
        try {
            db.openOrCreateDatabase();
            db.execSQL("UPDATE Data SET Value = '" + String.valueOf(worldPassed) +
                    "' WHERE Parameter = 'WorldPassed'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveLevelPassed(byte levelPassed) {
        try {
            db.openOrCreateDatabase();
            db.execSQL("UPDATE Data SET Value = '" + String.valueOf(levelPassed) +
                    "' WHERE Parameter = 'LevelPassed'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveCurrentDifficulty(String difficulty) {
        try {
            db.openOrCreateDatabase();
            db.execSQL("UPDATE Data SET Value = '" + difficulty +
                    "' WHERE Parameter = 'CurrentDifficulty'");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        } finally {
            try {
                db.closeDatabase();
            } catch (SQLiteGdxException e) {
                e.printStackTrace();
            }
        }
    }

    public static LevelGenerator getLevelGenerator() {
        return levelGenerator;
    }
}
