package com.demo.contacts.util;

import com.demo.contacts.domain.Contact;

/**
 * Created by and on 26.09.15.
 */
public interface ContactsLoaderListener {
   void onResult(Contact contact, LOADED_PART loaded_part);
   void onFinish();
   enum LOADED_PART{
      BASE,DETAILS;
   }
}
