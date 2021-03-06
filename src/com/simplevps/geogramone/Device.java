package com.simplevps.geogramone;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

public class Device {
   private Context _ctx;
   private String _contactId;
   private String _lookupKey;
   private String _name;
   private String _phNum;
   private String _pin;
      
   Device(Context ctx, String id) {
      _ctx = ctx;
      _contactId = id;
      _name = null;
      _phNum = null;
      _lookupKey = null;
      _pin = null;
      
      updateInfo();
   }
   
   public String getId() { return _contactId; }
   public String getName() { return _name; }
   public String getPhoneNum() { return _phNum; }
   public String getLookupKey() { return _lookupKey; }
   public String getPIN() { return (_pin == null) ? "":_pin; }
   public void   setPIN(String pin) { _pin = (pin==null)? "":pin; }
   
   public void updateInfo() {
      ContentResolver cr = _ctx.getContentResolver();
      
      Cursor cCur = cr.query(Contacts.CONTENT_URI, null, 
            Contacts._ID +" = ?", new String[]{_contactId}, null);     
      if (cCur.moveToNext()) {
         _name = cCur.getString(cCur.getColumnIndex(Contacts.DISPLAY_NAME));
         _lookupKey = cCur.getString(cCur.getColumnIndex(Contacts.LOOKUP_KEY));
         
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
   
   public String toString() {
      return _name + " " + _phNum + " " + _pin;
   }
}
