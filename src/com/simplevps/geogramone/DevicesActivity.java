package com.simplevps.geogramone;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DevicesActivity extends Activity {
   private static final int CONTACT_PICKER_RESULT = 1001;
   private static final int EDIT_CONTACT_RESULT = 1002;
   private SimpleAdapter listAdapter;
   private DeviceDb devDb;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);       
      setContentView(R.layout.activity_devices);
       
      devDb = DeviceDb.getInstance();
      devDb.init(getApplicationContext());
      listAdapter = new SimpleAdapter(
            getApplicationContext(), 
            devDb.getDeviceMap(),
            R.layout.devicerow, 
            new String[] {"name", "phone"},
            new int[] {R.id.text1, R.id.text2});
                                  
      ListView deviceListView = (ListView) findViewById(R.id.deviceListView);
      deviceListView.setAdapter(listAdapter);
      registerForContextMenu(deviceListView);

   }
   
   @Override
   public void onAttachedToWindow() {
       super.onAttachedToWindow();
      
      List<Map<String,String>> devMap = DeviceDb.getInstance().getDeviceMap();
      if (devMap.isEmpty()) {
         openOptionsMenu();
      }
   }
   
   @Override
   public void onCreateContextMenu(ContextMenu menu, View v,
                                   ContextMenuInfo menuInfo) {
       super.onCreateContextMenu(menu, v, menuInfo);
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.device_contextmenu, menu);
   }
   
   @Override
   public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      ListView deviceListView = (ListView) findViewById(R.id.deviceListView);
      @SuppressWarnings("unchecked")
      final Map<String, String> m = (Map<String, String>) deviceListView.getItemAtPosition(info.position);
      
      switch (item.getItemId()) {
      case R.id.devedit:
         String id = m.get("id");
         String key = m.get("lookupKey");
         Uri mSelectedContactUri = Contacts.getLookupUri(Long.parseLong(id), key);

         Intent editIntent  = new Intent(Intent.ACTION_EDIT);
         editIntent.setDataAndType(mSelectedContactUri, Contacts.CONTENT_ITEM_TYPE);
         editIntent.putExtra("finishActivityOnSaveCompleted", true);
         startActivityForResult(editIntent, EDIT_CONTACT_RESULT);

         return true;
      case R.id.devdelete:
         devDb.remove(m.get("id"));
         listAdapter.notifyDataSetChanged();
         return true;
      case R.id.devshowpin:
         String pin = devDb.getPIN(m.get("id"));
         String text;
         
         if (pin == null || pin.length() == 0)
            text = "No PIN stored for this device";
         else
            text = "PIN stored: " + pin; 
         
         Toast.makeText(getApplicationContext(), 
               text,
               Toast.LENGTH_LONG).show();
         return true;
      case R.id.devstorepin:
         AlertDialog.Builder alert = new AlertDialog.Builder(this);

         alert.setTitle("Store PIN");
         alert.setMessage("Enter this device's 4 digit PIN");

         // Set an EditText view to get user input 
         final EditText input = new EditText(this);
         input.setInputType(InputType.TYPE_CLASS_NUMBER);
         alert.setView(input);

         alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               String value = input.getText().toString();

               if (!validatePin(value))
               {
                  Toast.makeText(getApplicationContext(), 
                        "PIN not stored - must contain 4 numbers", 
                        Toast.LENGTH_LONG).show();
               }
               else {
                  devDb.setPIN(m.get("id"), value);
                  Toast.makeText(getApplicationContext(), 
                        "PIN stored", 
                        Toast.LENGTH_LONG).show();
               }
            }
         });

         alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
             // Canceled.
           }
         });

         alert.show();
         return true;
      default:
         return super.onContextItemSelected(item);
      }
   }
   
   private boolean validatePin(String value) {
      
      if (value.length() != 4) {
         return false;
      }
      
      return Character.isDigit(value.charAt(0))
            && Character.isDigit(value.charAt(1))
            && Character.isDigit(value.charAt(2))
            && Character.isDigit(value.charAt(3));
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.device_menu, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
          case R.id.action_devices_add:
             Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
             startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
             break;
       }
       return true;
   }
   
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (resultCode == RESULT_OK) {
         switch (requestCode) {
            case CONTACT_PICKER_RESULT:
               // handle contact results
               Uri contactData = data.getData();  
               Cursor c =  getContentResolver().query(contactData, null, null, null, null);  
               if (c.moveToFirst()) {                    
                  String id = c.getString(c.getColumnIndex(Contacts._ID));
                  if (Integer.parseInt(c.getString(c.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
                     devDb.add(id);
                     listAdapter.notifyDataSetChanged();                     
                  }
                  else {
                     Toast t1 = Toast.makeText(
                           getApplicationContext(), 
                           "No phone numbers found for selected contact", 
                           Toast.LENGTH_LONG);
                     t1.setGravity(Gravity.TOP, 0, 100);
                     t1.show();                  
                  } 
               } 
               break;
            case EDIT_CONTACT_RESULT:
               Cursor cCur =  getContentResolver().query(data.getData(), null, null, null, null);  
               if (cCur.moveToFirst()) {                    
                  String id = cCur.getString(cCur.getColumnIndex(Contacts._ID));
                  devDb.updateDevice(id);
                  listAdapter.notifyDataSetChanged();
               }               
               break;
         }
      } else if (requestCode == CONTACT_PICKER_RESULT) {
         Toast t1 = Toast.makeText(
                        getApplicationContext(), 
                        "Could not add selected contact", 
                        Toast.LENGTH_LONG);
         t1.setGravity(Gravity.TOP, 0, 100);
         t1.show();
      }
   }
}
