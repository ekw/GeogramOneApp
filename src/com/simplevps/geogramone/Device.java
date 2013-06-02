package com.simplevps.geogramone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

public class Device {
   private Context _ctx;
   private String _contactId;
   private String _name;
   private String _phNum;
      
   Device(Context ctx, String id) {
      _ctx = ctx;
      _contactId = id;
      _name = null;
      _phNum = null;
      
      ContentResolver cr = _ctx.getContentResolver();
      
      Cursor cCur = cr.query(Contacts.CONTENT_URI, null, 
            Contacts._ID +" = ?", new String[]{_contactId}, null);     
      if (cCur.moveToNext()) {
         _name = cCur.getString(cCur.getColumnIndex(Contacts.DISPLAY_NAME));
         
         if (Integer.parseInt(cCur.getString(cCur.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
            Cursor pCur = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID +" = ?", 
                  new String[]{_contactId}, null);
            if (pCur.moveToNext()) {
               _phNum = pCur.getString(pCur.getColumnIndex(Phone.DATA));
            }
            pCur.close();
         }
      }
      else {
         // contact not found
      }
      cCur.close();
   }
   
   public String getId() { return _contactId; }
   public String getName() { return _name; }
   public String getPhoneNum() { return _phNum; }
   
   public String toString()
   {
      return _name + " " + _phNum;
   }
}
