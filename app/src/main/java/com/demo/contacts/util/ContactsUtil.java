package com.demo.contacts.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

/**
 * Created by and on 26.09.15.
 */
public class ContactsUtil implements LoaderManager.LoaderCallbacks<Cursor>{

    private static ContactsUtil INSTANCE;
    private Activity activity;
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };
    private ContactsUtil(Activity context){
        this.activity = context;
    }
    public static ContactsUtil getInstance() {
        if (INSTANCE == null)
            throw (new IllegalStateException("BrushingStateManager not initialized"));
        return (INSTANCE);
    }

    public static ContactsUtil createInstance(Activity activity) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ContactsUtil(activity);
        return INSTANCE;
    }

    public void initLoader() {
        activity.getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
