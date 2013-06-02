package com.simplevps.geogramone;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DevicesActivity extends Activity {
   private static final int CONTACT_PICKER_RESULT = 1001;
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
      /*
      deviceListView.setOnItemClickListener(new OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            @SuppressWarnings("unchecked")
            Map<String, String> m = (Map<String, String>) parent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), 
                  m.get("id") + m.get("name") + m.get("phone"), Toast.LENGTH_SHORT).show();
        }
      });
          
      deviceListView.setOnItemLongClickListener(new OnItemLongClickListener() {
         public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            @SuppressWarnings("unchecked")
            Map<String, String> m = (Map<String, String>) parent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), 
                  "Long " + m.get("id") + m.get("name") + m.get("phone"), Toast.LENGTH_SHORT).show();
            return true;
         }
      });
      */
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
      switch (item.getItemId()) {
      case R.id.devedit:
         return true;
      case R.id.devdelete:
         ListView deviceListView = (ListView) findViewById(R.id.deviceListView);
         @SuppressWarnings("unchecked")
         Map<String, String> m = (Map<String, String>) deviceListView.getItemAtPosition(info.position);
         devDb.remove(m.get("id"));
         listAdapter.notifyDataSetChanged();
         return true;
      default:
         return super.onContextItemSelected(item);
      }
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
         }
      } else {
         // gracefully handle failure
         Toast t1 = Toast.makeText(
                        getApplicationContext(), 
                        "Could not add selected contact", 
                        Toast.LENGTH_LONG);
         t1.setGravity(Gravity.TOP, 0, 100);
         t1.show();
      }
   }
}
