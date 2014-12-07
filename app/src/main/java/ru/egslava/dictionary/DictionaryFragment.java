package ru.egslava.dictionary;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.getbase.android.db.loaders.CursorLoaderBuilder;
import com.getbase.android.db.provider.ProviderAction;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import static android.provider.ContactsContract.*;
import static android.provider.ContactsContract.Contacts.*;


@EFragment(R.layout.fragment_word_list)
@OptionsMenu(R.menu.main)
public class DictionaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FilterQueryProvider, TextWatcher {

    public static final int URL_LOADER = 0;

    @ViewById
    ListView                        list;

    private Loader<Cursor>          specsLoader;
    private SimpleCursorAdapter     adapter;
    private Cursor cursor;

    @ViewById
    EditText filter;

    @Override
    public String toString() {
        return  "123";
//        return super.toString();
    }

    @AfterViews
    void init(){
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[]{DISPLAY_NAME},
                new int[]{android.R.id.text1}, 0);

        list.setTextFilterEnabled(true);
        adapter.setFilterQueryProvider(this);
        list.setAdapter(adapter);

        specsLoader = getLoaderManager().initLoader(URL_LOADER, null, this);
        filter.addTextChangedListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch(loaderId){
            case URL_LOADER:

                CursorLoaderBuilder builder = CursorLoaderBuilder.forUri(CONTENT_URI);

                if (bundle != null && !bundle.getString("filter").isEmpty()){
                    builder.where(DISPLAY_NAME + " LIKE ?", "%" + bundle.getString("filter") + "%");
                }

                return builder.build(getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (this.cursor == null){       //first time
            adapter.swapCursor(cursor);
            return;
        }else{
            this.cursor = cursor;
            list.setFilterText(filter.getText().toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) { adapter.changeCursor( null ); }

    @Override public Cursor runQuery(CharSequence constraint) { return cursor; }


    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        Bundle params = new Bundle();
        params.putString("filter", s.toString());
        getLoaderManager().restartLoader(URL_LOADER, params, this);
    }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void afterTextChanged(Editable s) {}
}
