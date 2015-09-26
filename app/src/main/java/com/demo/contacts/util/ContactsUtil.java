package com.demo.contacts.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.util.Log;

import com.demo.contacts.domain.Contact;
import com.demo.contacts.ui.ContactsLoaderListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by and on 26.09.15.
 */
public class ContactsUtil implements LoaderManager.LoaderCallbacks<Cursor> {

    private static ContactsUtil INSTANCE;
    private Activity mActivity;
    private String mSearchString;
    private static final String TAG = ContactsUtil.class.getSimpleName();

    private String[] mSelectionArgs = {mSearchString};
    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";

    private static final String SELECTION_MIMETYPE = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
    private String mLookupKey;
    private final int PHONE_NUMBER_QUERY_ID = 2;
    private final int MIMETYPE_QUERY_ID = 1;

    private Contact contact;
    private ContactsLoaderListener mCallback;


    private static final String[] PROJECTION =
            {
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Data.PHOTO_URI,
                    Email._ID,
                    Email.ADDRESS,
                    Email.TYPE,
                    Email.LABEL,
                    StructuredPostal.POSTCODE,
                    StructuredPostal.COUNTRY,
                    StructuredPostal.CITY,
                    StructuredPostal.STREET,

                    ContactsContract.Contacts._ID,


            };

    private static final String[] PROJECTION_MIMETYPE =
            {
                    StructuredPostal.POSTCODE,
                    StructuredPostal.COUNTRY,
                    StructuredPostal.CITY,
                    StructuredPostal.STREET,
                    ContactsContract.Contacts._ID,
            };

    private ContactsUtil(Activity context) {
        this.mActivity = context;
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

    /**
     *
     * Search for contact by given phone number.
     * The search run in background , and will notify with callback
     *
     * If callback has value null , IllegalStateException will be thrown
     * @param phoneNumber
     * @param callback callback will call implementer on finish loading
     */
    public void search(String phoneNumber, ContactsLoaderListener callback) {
        if(callback == null){
            throw (new IllegalStateException("ContactsLoaderListener can not be null !"));
        }
        Log.d(TAG, "search().. " + phoneNumber);
        this.contact = null;
        this.mSearchString = phoneNumber;
        this.mCallback = callback;
        mActivity.getLoaderManager().initLoader(PHONE_NUMBER_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader().. ");
        CursorLoader mLoader = null;
        switch (id) {
            case PHONE_NUMBER_QUERY_ID:
                String[] param = new String[]{mSearchString};
                mLoader = new CursorLoader(
                        mActivity,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        param,
                        null
                );
                break;
            case MIMETYPE_QUERY_ID:
                if (this.contact != null && this.contact.getmContactID() != null) {
                    String[] param2 = new String[]{this.contact.getmContactID(), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Log.d(TAG, "mContactID:  " + this.contact.getmContactID());
                    mLoader = new CursorLoader(
                            mActivity,
                            ContactsContract.Data.CONTENT_URI,
                            PROJECTION_MIMETYPE,
                            SELECTION_MIMETYPE,
                            param2,
                            null
                    );
                }
                break;

        }
        return mLoader;
    }

    /**
     *If several contacts with the same number exist - return the one that has a thumbnail, otherwise - null.
     * @param c
     * @return Contact with thumbnail or null
     */
    private Contact iterateMoreContacts(Cursor c) {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;
        if (c.moveToFirst()) {
            do {
                contact = createContact(c);
                if(contact.getThumbUri() != null) {
                    contactList.add(contact);
                }
            }
            while (c.moveToNext());
        }
        if(contactList.isEmpty()){
            return null;
        }
        return contactList.get(0);
    }

    /**
     * Creating contact from cursor
     * @param cur
     * @return Contact created from cursor
     */
    private Contact createContact(Cursor cur) {
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
        String thumbUriStr = cur.getString(cur.getColumnIndex(ContactsContract.Data.PHOTO_URI));
        Uri thumbUri = null;
        if(thumbUriStr != null) {
            thumbUri = Uri.parse(thumbUriStr);
        }
        String phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        String contactID = cur.getString(cur.getColumnIndex(ContactsContract.Data.CONTACT_ID));

        Contact contact = new Contact();
        contact.setmContactID(contactID);
        contact.setDisplayName(name);
        contact.setThumbUri(thumbUri);
        contact.setPhoneNumber(phoneNumber);
        return contact;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        Log.d(TAG, "onLoadFinished().. ");
        switch (loader.getId()) {
            case PHONE_NUMBER_QUERY_ID:
                if (cur.getCount() > 0) {
                    if (cur.moveToFirst()) {
                        if (cur.getCount() > 1) {
                            this.contact = iterateMoreContacts(cur);
                        } else {
                            this.contact = createContact(cur);
                        }
                    }

                    if (this.contact.getmContactID() != null) {
                        mCallback.onResult(contact, ContactsLoaderListener.LOADED_PART.BASE);
                        searchForDetails();
                    } else {
                        mCallback.onFinish();
                    }
                } else {
                    mCallback.onFinish();
                    Log.e(TAG, " NO contact ! ");
                }

                break;
            case MIMETYPE_QUERY_ID:
                if (cur.getCount() > 0) {
                    if (cur.moveToFirst()) {

                        String city = cur.getString(cur.getColumnIndex(StructuredPostal.CITY));
                        String street = cur.getString(cur.getColumnIndex(StructuredPostal.STREET));
                        String country = cur.getString(cur.getColumnIndex(StructuredPostal.COUNTRY));
                        String postcode = cur.getString(cur.getColumnIndex(StructuredPostal.POSTCODE));
                        if (contact != null) {
                            contact.setCity(city);
                            contact.setStreet(street);
                            contact.setCountry(country);
                            contact.setPostcode(postcode);
                            mCallback.onResult(contact, ContactsLoaderListener.LOADED_PART.DETAILS);

                        }

                        Log.d(TAG, " found the country: " + country + " email: ");
                    }
                } else {
                    Log.e(TAG, " NO contact ! ");
                }
                mCallback.onFinish();
        }
        //work around onCreateLoader was  called only by first search
        mActivity.getLoaderManager().destroyLoader(loader.getId());
    }

    public void destroyLoader() {
        mActivity.getLoaderManager().destroyLoader(PHONE_NUMBER_QUERY_ID);
        mActivity.getLoaderManager().destroyLoader(MIMETYPE_QUERY_ID);
    }

    private void searchForDetails() {
        mActivity.getLoaderManager().initLoader(MIMETYPE_QUERY_ID, null, this);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallback.onFinish();
    }
}
