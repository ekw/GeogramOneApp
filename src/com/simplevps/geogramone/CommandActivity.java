package com.simplevps.geogramone;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CommandActivity extends Activity {
   private static final String TAG = "CommandActivity";

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);       
      setContentView(R.layout.activity_commands);
      
      final Button btn = (Button) findViewById(R.id.sendCommandBtn);
      btn.setOnClickListener(new View.OnClickListener() {         
         @SuppressWarnings("unchecked")
         @Override
         public void onClick(View v) {
            Spinner devSpinner = (Spinner) findViewById(R.id.deviceSpinner);
            Map<String, String> devMap = 
                  (Map<String, String>) devSpinner.getSelectedItem();
            
            Spinner cmdSpinner = (Spinner) findViewById(R.id.commandSpinner);
            Map<String, String> cmdMap = 
                  (Map<String, String>) cmdSpinner.getSelectedItem();
            
            int numParms = Integer.parseInt(cmdMap.get("numParams"));

            String parm1 = null, parm2 = null, parm3 = null;
            if (numParms > 0) {
               EditText p1 = (EditText) findViewById(R.id.param1);
               parm1 = p1.getText().toString().trim();
            }            
            if (numParms > 1) {
               EditText p2 = (EditText) findViewById(R.id.param2);
               parm2 = p2.getText().toString().trim();
            }
            if (numParms > 2) {
               EditText p3 = (EditText) findViewById(R.id.param3);
               parm3 = p3.getText().toString().trim();
            }
            
            String devPin = devMap.get("pin");
            if (devPin == null || devPin.length() < 1){
               Toast.makeText(getApplicationContext(), "Device has no PIN stored", 
                     Toast.LENGTH_LONG).show();
            }
            else {
               String command = CommandProcessor.getInstance().makeCommand(
                     cmdMap.get("cmdNum"), devMap.get("pin"), parm1, parm2, parm3);
               
               if (command != null) {
                  Intent i = new Intent(SmsReceiver.MSG_SMS_SEND_CMD);
                  i.putExtra("command", command);
                  i.putExtra("phone", devMap.get("phone"));
                  sendBroadcast(i);
               }
               else {
                  Log.e(TAG, "CommandProcessor.makeCommand() returned null");
                  Toast.makeText(getApplicationContext(), "Can't make command", 
                        Toast.LENGTH_LONG).show();
               }
            }
         }
      });
      
      addDevices();
      addCommands();      
   }

   private void addDevices() {
      Spinner spinner = (Spinner) findViewById(R.id.deviceSpinner);
      DeviceDb devDb = DeviceDb.getInstance();
      devDb.init(getApplicationContext());

      SimpleAdapter listAdapter = new SimpleAdapter(
            getApplicationContext(), 
            devDb.getDeviceMap(), 
            android.R.layout.simple_spinner_item, 
            new String[] {"name"},
            new int[] {android.R.id.text1});
      listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner.setAdapter(listAdapter); 
   }

   private void addCommands() {
      Spinner spinner = (Spinner) findViewById(R.id.commandSpinner);
      CommandProcessor cmdProc = CommandProcessor.getInstance();
      
      SimpleAdapter listAdapter = new SimpleAdapter(
            getApplicationContext(), 
            cmdProc.getCommands(), 
            android.R.layout.simple_spinner_item, 
            new String[] {"name"},
            new int[] {android.R.id.text1});
      listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner.setAdapter(listAdapter); 
      
      spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos,
               long id) {
            // TODO Auto-generated method stub
            @SuppressWarnings("unchecked")
            Map<String, String> m = (Map<String, String>) parent.getItemAtPosition(pos);
            int numParams = Integer.parseInt(m.get("numParams"));
            int [] paramFields = { R.id.param1, R.id.param2, R.id.param3 };
            int [] paramLabels = { R.id.param1Label, R.id.param2Label, R.id.param3Label };
                       
            for (int i=0; i < 3; i++) {
               EditText et = (EditText) findViewById(paramFields[i]);
               TextView tv = (TextView) findViewById(paramLabels[i]);               
               et.setVisibility(View.INVISIBLE);
               tv.setVisibility(View.INVISIBLE);
            }
            
            for (int i=0; i < numParams; i++)
            {
               EditText et = (EditText) findViewById(paramFields[i]);
               TextView tv = (TextView) findViewById(paramLabels[i]);
               et.setVisibility(View.VISIBLE);
               tv.setVisibility(View.VISIBLE);
            }
            
         }

         @Override
         public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            
         }
         
      });
   }
}
