package ru.egslava.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by egslava on 29/11/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static String createTable(String tableName) {
        return String.format(
                "CREATE TABLE %s ( `_id` INTEGER, `word` CHAR(255), `definition` TEXT, PRIMARY KEY(_id) );",
                tableName);
    }

    public static String insertWord(String tableName) {
        return String.format( "INSERT INTO %s (word,definition) VALUES (?, ?)",
                tableName);
    }


    private final Context context;

    public DBHelper(Context context) {
        super(context, "db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        fillWords(db, "rus_taj", R.raw.rus_taj);
        fillWords(db, "taj_rus", R.raw.taj_rus);

    }

    private void fillWords(SQLiteDatabase db, String tableName, int wordFileResId) {
        try {
            db.execSQL( createTable(tableName));

            InputStream inputStream = context.getResources().openRawResource( wordFileResId );
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buf = new BufferedReader(reader);

            String s;
            while( buf.ready() ){
                s = buf.readLine();
                db.execSQL(insertWord( tableName), StringUtils.split(s, '%'));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
