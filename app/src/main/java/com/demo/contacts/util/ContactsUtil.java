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

/**
 * Created by and on 26.09.15.
 */
public class ContactsUtil implements LoaderManager.LoaderCallbacks<Cursor>{

    private static ContactsUtil INSTANCE;
    private Activity mActivity;
    private String mSearchString;
    private static final String TAG = ContactsUtil.class.getSimpleName();

    private String[] mSelectionArgs = { mSearchString };
    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";

    private static final String SELECTION_MIMETYPE = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
    private String mLookupKey;
    private final int PHONE_NUMBER_QUERY_ID = 0;
    private final int MIMETYPE_QUERY_ID = 1;

    private String mContactID = null;
    private Contact contact;
    private ContactsLoaderListener mCallback;

    private static final String[] PROJECTION =
            {
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
//                    ContactsContract.PhoneLookup.NUMBER,
//                    ContactsContract.PhoneLookup.DISPLAY_NAME,
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

    private ContactsUtil(Activity context){
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

    public void search(String phoneNumber,ContactsLoaderListener callback) {
        Log.d(TAG,"search().. "+phoneNumber);
        this.mSearchString = phoneNumber;
        this.mCallback = callback;
        mActivity.getLoaderManager().initLoader(PHONE_NUMBER_QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"onCreateLoader().. ");
        CursorLoader mLoader = null;
        switch (id) {
            case PHONE_NUMBER_QUERY_ID:
                // Assigns the selection parameter
//                mSelectionArgs[0] = mLookupKey;
                // Starts the query
                String[] param = new String[]{mSearchString};
                Log.d(TAG,"onCreateLoader().. "+mSelectionArgs[0]);
//                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
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
                if(this.mContactID == null){
                    break;
                }
                // Assigns the selection parameter
//                mSelectionArgs[0] = mLookupKey;
                // Starts the query
                String[] param2 = new String[]{mContactID,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                Log.d(TAG,"mContactID:  "+mContactID);
//                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                mLoader = new CursorLoader(
                        mActivity,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION_MIMETYPE,
                        SELECTION_MIMETYPE,
                        param2,
                        null
                );
                break;

        }
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        Log.d(TAG,"onLoadFinished().. ");
        switch (loader.getId()) {
            case PHONE_NUMBER_QUERY_ID:
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                        String thumbUriStr = cur.getString(cur.getColumnIndex(ContactsContract.Data.PHOTO_URI));
                        Uri thumbUri = thumbUri = Uri.parse(thumbUriStr);
                        String phoneNumber = cur.getString(cur.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));

                        String contactID =  cur.getString(cur.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                        this.mContactID = contactID;
                        contact = new Contact();
                        contact.setDisplayName(name);
                        contact.setThumbUri(thumbUri);
                        contact.setPhoneNumber(phoneNumber);
                        mCallback.onResult(contact, ContactsLoaderListener.LOADED_PART.BASE);
                        if(mContactID != null) {
                            searchForDetails();
                        }else{
                            mCallback.onFinish();
                        }
//                        Log.d(TAG," found the contact: "+name+" namePhone: "+namePhone+" contactID: "+contactID+" phoneNumber "+phoneNumber);
                    }
                }else{
                    Log.e(TAG," NO contact ! ");
                }
                break;
            case MIMETYPE_QUERY_ID:
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {

                        String city = cur.getString(cur.getColumnIndex(StructuredPostal.CITY));
                        String street = cur.getString(cur.getColumnIndex(StructuredPostal.STREET));
                        String country = cur.getString(cur.getColumnIndex(StructuredPostal.COUNTRY));
                        String postcode = cur.getString(cur.getColumnIndex(StructuredPostal.POSTCODE));
                        if(contact != null){
                            contact.setCity(city);
                            contact.setStreet(street);
                            contact.setCountry(country);
                            contact.setPostcode(postcode);
                            mCallback.onResult(contact, ContactsLoaderListener.LOADED_PART.DETAILS);

                        }
                        Log.d(TAG," found the country: "+country+" email: ");
                    }
                }else{
                    Log.e(TAG," NO contact ! ");
                }
                mCallback.onFinish();

        }

    }

    private void searchForDetails(){
        mActivity.getLoaderManager().initLoader(MIMETYPE_QUERY_ID, null, this);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallback.onFinish();
    }
}
