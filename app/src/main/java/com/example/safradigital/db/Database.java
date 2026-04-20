package com.example.safradigital.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Database {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public Database(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
    }
}
