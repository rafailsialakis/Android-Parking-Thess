package com.example.parkpal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SectorDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "parkpal.db";
    private static final int DATABASE_VERSION = 1;

]    public static final String TABLE_SECTORS = "sectors";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_AVAILABLE = "available_spots";

    private static final String SQL_CREATE_TABLE_SECTORS =
            "CREATE TABLE " + TABLE_SECTORS + " (" +
                    COLUMN_CODE + " TEXT PRIMARY KEY, " +
                    COLUMN_CAPACITY + " INTEGER NOT NULL, " +
                    COLUMN_AVAILABLE + " INTEGER NOT NULL" +
                    ");";

    public SectorDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SECTORS);
        insertDefaultSectors(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECTORS);
        onCreate(db);
    }

    private void insertDefaultSectors(SQLiteDatabase db) {
        insertSector(db, "HONZOO", 100, 10);
        insertSector(db, "ALAMOA", 200, 14);
        insertSector(db, "INTMKT", 150, 11);
        insertSector(db, "KEWALO", 130, 18);
        insertSector(db, "BISQRE", 70, 50);
        insertSector(db, "KALAKA", 55, 23);
        insertSector(db, "KAKWTR", 55, 0);
    }

    public long insertSector(String code, int capacity, int available) {
        SQLiteDatabase db = getWritableDatabase();
        return insertSector(db, code, capacity, available);
    }

    private long insertSector(SQLiteDatabase db, String code, int capacity, int available) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODE, code);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_AVAILABLE, available);
        return db.insertWithOnConflict(
                TABLE_SECTORS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public boolean deleteSector(String code) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_SECTORS,
                COLUMN_CODE + " = ?",
                new String[]{ code }
        );
        return rowsDeleted > 0;
    }

    public List<Sector> getAllSectors() {
        SQLiteDatabase db = getReadableDatabase();
        List<Sector> sectors = new ArrayList<>();

        String[] columns = { COLUMN_CODE, COLUMN_CAPACITY, COLUMN_AVAILABLE };
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_SECTORS,
                    columns,
                    null,     // no WHERE
                    null,     // no args
                    null,     // no GROUP BY
                    null,     // no HAVING
                    COLUMN_CODE + " ASC"  // order by code ascending
            );

            while (cursor.moveToNext()) {
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int available = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVAILABLE));
                Sector sector = new Sector(code, capacity, available);
                sectors.add(sector);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return sectors;
    }

    public Sector getSectorByCode(String code) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = { COLUMN_CODE, COLUMN_CAPACITY, COLUMN_AVAILABLE };
        String selection = COLUMN_CODE + " = ?";
        String[] selectionArgs = { code };
        Cursor cursor = null;
        Sector s = null;

        try {
            cursor = db.query(
                    TABLE_SECTORS,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int available = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVAILABLE));
                s = new Sector(code, capacity, available);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return s;
    }

    public int updateSectorAvailability(String code, int newAvailable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVAILABLE, newAvailable);
        return db.update(
                TABLE_SECTORS,
                values,
                COLUMN_CODE + " = ?",
                new String[]{ code }
        );
    }
}
