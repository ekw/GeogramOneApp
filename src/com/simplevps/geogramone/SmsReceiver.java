package com.simplevps.geogramone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
   private static final String TAG = "SmsReceiver";
   public static final String MSG_SMS_SEND_CMD = "com.simplevps.geogramone.MSG_SMS_SEND_CMD";
   public static final String MSG_SMS_SENT = "com.simplevps.geogramone.MSG_SMS_SENT";
   public static final String MSG_SMS_DELIVERED = "com.simplevps.geogramone.MSG_SMS_DELIVERED";
   
   //======================================================================
   @Override
   public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Log.i(TAG, "Intent received: " + action);
      
      if (action.equalsIgnoreCase(MSG_SMS_SEND_CMD)) {
         Bundle b = intent.getExtras();
         String phone = b.getString("phone");
         String command = b.getString("command");
         sendSMS(context, phone, command);
      }
      else if (action.equalsIgnoreCase(MSG_SMS_SENT)) {
         switch (getResultCode())
         {
             case Activity.RESULT_OK:
                 Toast.makeText(context, "SMS sent", 
                         Toast.LENGTH_SHORT).show();
                 break;
             case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                 Toast.makeText(context, "Generic failure", 
                         Toast.LENGTH_SHORT).show();
                 break;
             case SmsManager.RESULT_ERROR_NO_SERVICE:
                 Toast.makeText(context, "No service", 
                         Toast.LENGTH_SHORT).show();
                 break;
             case SmsManager.RESULT_ERROR_NULL_PDU:
                 Toast.makeText(context, "Null PDU", 
                         Toast.LENGTH_SHORT).show();
                 break;
             case SmsManager.RESULT_ERROR_RADIO_OFF:
                 Toast.makeText(context, "Radio off", 
                         Toast.LENGTH_SHORT).show();
                 break;
         }
      }
      else if (action.equalsIgnoreCase(MSG_SMS_DELIVERED)){
         switch (getResultCode())
         {
             case Activity.RESULT_OK:
                 Toast.makeText(context, "SMS delivered", 
                         Toast.LENGTH_SHORT).show();
                 break;
             case Activity.RESULT_CANCELED:
                 Toast.makeText(context, "SMS canceled", 
                         Toast.LENGTH_SHORT).show();
                 break;                        
         }
      }
      else {
         //---get the SMS message passed in---
         Bundle bundle = intent.getExtras();
         SmsMessage[] msgs = null;
         String str = "";
         String originator = null;
         if (bundle != null)
         {
             //---retrieve the SMS message received---
             Object[] pdus = (Object[]) bundle.get("pdus");
             msgs = new SmsMessage[pdus.length];
             
             for (int i=0; i<msgs.length; i++) {
                 msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                 str += msgs[i].getMessageBody().toString();
                 originator = msgs[i].getOriginatingAddress();
             }
             str = str.trim();
             Log.i(TAG, "originator: " + originator);
             Log.i(TAG, "msg: " + str);
             
             DeviceDb devDb = DeviceDb.getInstance();
             Device dev = devDb.findDeviceWithPhone(originator);
             if (dev != null) { 
                // Process location URL                
                String regex = "q=(\\-?\\d+\\+\\d+\\.\\d+),(\\-?\\d+\\+\\d+\\.\\d+)\\+\\((.+)\\)";
                Pattern location = Pattern.compile(regex);
                Matcher m = location.matcher(str);

                if (m.find()){
                   String lat = m.group(1);
                   String lon = m.group(2);
                   String info = m.group(3);
                   Log.d("SmsReceiver", "Latitude " + lat);
                   Log.d("SmsReceiver", "Longitude " + lon);                
                   Log.d("SmsReceiver", "Info " + info);
                }
                this.abortBroadcast();
             }
         }
      } 
   }
   
   private void sendSMS(Context context, String phoneNumber, String message)
   {        
       PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
           new Intent(MSG_SMS_SENT), 0);

       PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
           new Intent(MSG_SMS_DELIVERED), 0);     

       SmsManager sms = SmsManager.getDefault();
       sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
   }

}
