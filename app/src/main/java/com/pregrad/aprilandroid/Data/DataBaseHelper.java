package com.pregrad.aprilandroid.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "MY_DB.db";
    static final String TABLE_NAME = "MY_TABLE";

    static final String COL_1 = "ID";
    static final String COL_2 = "NAME";
    static final String COL_3 = "SURNAME";
    static final String COL_4 = "MARKS";
    static final String COL_5 = "GRADE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("+COL_1+" INTEGER PRIMARY KEY, "+COL_2+" TEXT, "+COL_3+" TEXT, "+COL_4+" INTEGER, "+COL_5+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     *Inserting data into database
     **/

    public boolean insertData(int id, String name, String surname, int marks, String grade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, marks);
        contentValues.put(COL_5, grade);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateData(int id, String name, String surname, int marks, String grade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, marks);
        contentValues.put(COL_5, grade);
        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        return result != -1;
    }

    public Integer deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getDataByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + id, null);
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
