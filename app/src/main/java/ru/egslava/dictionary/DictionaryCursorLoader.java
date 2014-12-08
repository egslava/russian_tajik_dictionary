package ru.egslava.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by egslava on 09/12/14.
 */
public class DictionaryCursorLoader extends CursorLoader {

    private MainActivity ac;

    public DictionaryCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        ac = (MainActivity) context;    // dunno why it saves application context instead of context
    }

    @Override
    public Cursor loadInBackground() {
//        super.loadInBackground();
        String authority = getUri().getAuthority();
        return ac.db().getReadableDatabase().query(authority, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder());
//        return super.loadInBackground();
    }
}
