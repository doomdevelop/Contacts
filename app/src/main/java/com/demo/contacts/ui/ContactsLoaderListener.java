package com.demo.contacts.ui;

import com.demo.contacts.domain.Contact;

/**
 * Created by and on 26.09.15.
 */
public interface ContactsLoaderListener {
   void onFinish(Contact contact,LOADED_PART loaded_part);
   enum LOADED_PART{
      BASE,DETAILS;
   }
}
