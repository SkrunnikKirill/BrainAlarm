package com.example.alscon.brainalarm.datebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alscon.brainalarm.Brain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alscon on 23-Nov-16.
 */

public class DataBase extends SQLiteOpenHelper {
    public static final String BRAIN_TABLE = "brain";
    public static final String COLUMN_BRAIN_ID = "_id";
    public static final String COLUMN_BRAIN_ACTIVE = "brain_active";
    public static final String COLUMN_BRAIN_TIME = "brain_time";
    public static final String COLUMN_BRAIN_DAYS = "brain_days";
    public static final String COLUMN_BRAIN_DIFFICULTY = "brain_difficulty";
    public static final String COLUMN_BRAIN_TONE = "brain_tone";
    public static final String COLUMN_BRAIN_VIBRATE = "brain_vibrate";
    public static final String COLUMN_BRAIN_NAME = "brain_name";
    static final String DATABASE_NAME = "BRAIN";
    static final int DATABASE_VERSION = 1;
    static DataBase instance = null;
    static SQLiteDatabase database = null;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new DataBase(context);
        }
    }

    public static SQLiteDatabase getDatabase() {
        if (database == null) {
            database = instance.getWritableDatabase();
        }
        return database;
    }
    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }

    public static long create(Brain brain) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BRAIN_ACTIVE, brain.getBrainActive());
        contentValues.put(COLUMN_BRAIN_TIME, brain.getBrainTimeString());

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(brain.getDays());
            byte[] buff = byteArrayOutputStream.toByteArray();

            contentValues.put(COLUMN_BRAIN_DAYS, buff);
        } catch (Exception e) {
        }
        contentValues.put(COLUMN_BRAIN_DIFFICULTY, brain.getDifficulty().ordinal());
        contentValues.put(COLUMN_BRAIN_TONE, brain.getBrainTonePath());
        contentValues.put(COLUMN_BRAIN_VIBRATE, brain.getVibrate());
        contentValues.put(COLUMN_BRAIN_NAME, brain.getBrainName());

        return getDatabase().insert(BRAIN_TABLE, null, contentValues);
    }

    public static int update(Brain brain) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BRAIN_ACTIVE, brain.getBrainActive());
        cv.put(COLUMN_BRAIN_TIME, brain.getBrainTimeString());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(brain.getDays());
            byte[] buff = bos.toByteArray();

            cv.put(COLUMN_BRAIN_DAYS, buff);

        } catch (Exception e){
        }

        cv.put(COLUMN_BRAIN_DIFFICULTY, brain.getDifficulty().ordinal());
        cv.put(COLUMN_BRAIN_TONE, brain.getBrainTonePath());
        cv.put(COLUMN_BRAIN_VIBRATE, brain.getVibrate());
        cv.put(COLUMN_BRAIN_NAME, brain.getBrainName());

        return getDatabase().update(BRAIN_TABLE, cv, "_id=" + brain.getId(), null);
    }

    public static int deleteEntry(Brain brain) {
        return deleteEntry(brain.getId());
    }

    public static int deleteEntry(int id) {
        return getDatabase().delete(BRAIN_TABLE, COLUMN_BRAIN_ID + "=" + id, null);
    }

    public static int deleteAll() {
        return getDatabase().delete(BRAIN_TABLE, "1", null);
    }

    public static Brain getBrain(int id) {
        String[] colums = new String[]{
                COLUMN_BRAIN_ID,
                COLUMN_BRAIN_ACTIVE,
                COLUMN_BRAIN_TIME,
                COLUMN_BRAIN_DAYS,
                COLUMN_BRAIN_DIFFICULTY,
                COLUMN_BRAIN_TONE,
                COLUMN_BRAIN_VIBRATE,
                COLUMN_BRAIN_NAME
        };

        Cursor cursor = getDatabase().query(BRAIN_TABLE, colums, COLUMN_BRAIN_ID + "=" + id, null, null, null, null);
        Brain brain = null;

        if (cursor.moveToFirst()) {
            brain = new Brain();
            brain.setId(cursor.getInt(1));
            brain.setBrainActive(cursor.getInt(2) == 1);
            brain.setBrainTime(cursor.getString(3));
            byte[] repeatDaysBytes = cursor.getBlob(4);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(repeatDaysBytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Brain.Day[] repeatDay;
                Object object = objectInputStream.readObject();
                if (object instanceof Brain.Day[]) {
                    repeatDay = (Brain.Day[]) object;
                    brain.setDays(repeatDay);
                }
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            brain.setDifficulty(Brain.Difficulty.values()[cursor.getInt(5)]);
            brain.setBrainTonePath(cursor.getString(6));
            brain.setVibrate(cursor.getInt(7) == 1);
            brain.setBrainName(cursor.getString(8));
        }
        cursor.close();
        return brain;
    }

    public static Cursor getCursor() {
        String[] colums = new String[]{
                COLUMN_BRAIN_ID,
                COLUMN_BRAIN_ACTIVE,
                COLUMN_BRAIN_TIME,
                COLUMN_BRAIN_DAYS,
                COLUMN_BRAIN_DIFFICULTY,
                COLUMN_BRAIN_TONE,
                COLUMN_BRAIN_VIBRATE,
                COLUMN_BRAIN_NAME
        };
        return getDatabase().query(BRAIN_TABLE, colums, null, null, null, null, null);
    }

    public static List<Brain> getAll() {
        List<Brain> Lbrain = new ArrayList<>();
        Cursor cursor = DataBase.getCursor();
        if (cursor.moveToFirst()) {
            do {
                Brain brain = new Brain();
                brain.setId(cursor.getInt(0));
                brain.setBrainActive(cursor.getInt(1) == 1);
                brain.setBrainTime(cursor.getString(2));
                byte[] repeatDaysBytes = cursor.getBlob(3);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(repeatDaysBytes);

                try{
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Brain.Day[] repeatDays;
                    Object object = objectInputStream.readObject();
                    if (object instanceof Brain.Day[]){
                        repeatDays = (Brain.Day[])object;
                        brain.setDays(repeatDays);
                    }
                }catch (StreamCorruptedException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
                brain.setDifficulty(Brain.Difficulty.values()[cursor.getInt(4)]);
                brain.setBrainTonePath(cursor.getString(5));
                brain.setVibrate(cursor.getInt(6)==1);
                brain.setBrainName(cursor.getString(7));

                Lbrain.add(brain);

            }while (cursor.moveToNext());
        }
        cursor.close();
        return Lbrain;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + BRAIN_TABLE + " ( "
                + COLUMN_BRAIN_ID + " INTEGER primary key autoincrement, "
                + COLUMN_BRAIN_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_BRAIN_TIME + " TEXT NOT NULL, "
                + COLUMN_BRAIN_DAYS + " BLOB NOT NULL, "
                + COLUMN_BRAIN_DIFFICULTY + " INTEGER NOT NULL, "
                + COLUMN_BRAIN_TONE + " TEXT NOT NULL, "
                + COLUMN_BRAIN_VIBRATE + " INTEGER NOT NULL, "
                + COLUMN_BRAIN_NAME + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BRAIN_TABLE);
        onCreate(sqLiteDatabase);
    }
}
