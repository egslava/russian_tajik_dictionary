package ru.egslava.dictionary;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById
    PagerSlidingTabStrip    tabs;

    @ViewById
    ViewPager               pager;

    @AfterViews
    void init(){


//        new FragmentManager();
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override public int getCount() { return 2; }
            @Override public Fragment getItem(int i) {
                return DictionaryFragment_.builder().build();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return String.valueOf( position );
//                return super.getPageTitle(position);
            }
        });
        tabs.setViewPager(pager);
//        getSupportActionBar().setSubtitle("Blah");
//        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
//                null,
//                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
//                new int[]{android.R.id.text1}, 0);
//
//        list.setTextFilterEnabled(true);
//        adapter.setFilterQueryProvider(this);
//        list.setAdapter(adapter);
//
//        Bundle filtering = new Bundle();
//        filtering.putString("filter", "а");
//        specsLoader = getSupportLoaderManager().initLoader(URL_LOADER, filtering, this);
//        filter.addTextChangedListener(this);
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
//        switch(loaderId){
//            case URL_LOADER:
//
//                CursorLoader result = null;
//
//                if (bundle != null){
//                    String filter1 = "%" + bundle.getString("filter") + "%";
//                    String filterPartOfQuery = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
//                    result = new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, null,
//                            filterPartOfQuery, new String[]{filter1}, null) ;
//
//                }else {
//                    result = new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//                    AutoCompleteTextView tw;
//
//                }
//
//                return result;
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        if (this.cursor == null){       //first time
//            adapter.swapCursor(cursor);
//            return;
//        }else{
//            this.cursor = cursor;
//            list.setFilterText(filter.getText().toString());
//        }
//    }
//
//    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) { adapter.changeCursor( null ); }
//
//    @Override public Cursor runQuery(CharSequence constraint) { return cursor; }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
//        Bundle params = new Bundle();
//        params.putString("filter", s.toString());
//        getSupportLoaderManager().restartLoader(URL_LOADER, params, this);
//    }
//    @Override public void afterTextChanged(Editable s) {}
}
