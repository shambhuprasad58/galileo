package Contacts;

import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import Contacts.ContactData;
/**
 * Created by Administrator on 7/2/2015.
 */
//

public class Contacts{

    //public
    Context context;
    public  Contacts(Context cx){
        context = cx;
    }

     public List <ContactData> fetchList()
     {
         Cursor cursor = null;
         List <ContactData> PhoneList = new ArrayList<ContactData>();
         try {
             cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
             int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
             int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
             int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
             int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
             cursor.moveToFirst();
             do {
                 String idContact = cursor.getString(contactIdIdx);
                 String name = cursor.getString(nameIdx);
                 String phoneNumber = cursor.getString(phoneNumberIdx);
                 PhoneList.add(new ContactData(name,phoneNumber));

                 //...
             } while (cursor.moveToNext());
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             if (cursor != null) {
                 cursor.close();
             }
         }
         return  PhoneList;
     }
}


