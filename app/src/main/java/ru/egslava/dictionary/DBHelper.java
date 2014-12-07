package ru.egslava.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by egslava on 29/11/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final Context context;

    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, "db", factory, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String query = IOUtils.toString(context.getResources().openRawResource(R.raw.sqlite_work_types));
            String[] query_parts = StringUtils.split(query, ';');
            for(String querie: query_parts){
                getReadableDatabase().execSQL(querie);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
