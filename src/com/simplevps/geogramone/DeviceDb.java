package com.simplevps.geogramone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class DeviceDb {
   private static DeviceDb instance = null;
   private static Context appCtx;
   private static ArrayList<Device> deviceList;
   private static List<Map<String, String>> deviceMap;
   
   private final static String DEVICES_FILE = "devices";
   
   protected DeviceDb() {}
   public static DeviceDb getInstance() {
      if (instance == null) {
         instance = new DeviceDb();
      }
      return instance;
   }
   
   public void init(Context ctx) {
      appCtx = ctx;
      deviceList = new ArrayList<Device>();
      deviceMap = new ArrayList<Map<String, String>>();
      
      // Read device storage file
      try {
         FileInputStream fin = appCtx.openFileInput(DEVICES_FILE);
         InputStreamReader isr = new InputStreamReader(fin);
         BufferedReader br = new BufferedReader(isr);
         
         Log.i("DeviceDb init", "start");
         String line = br.readLine();
         while (line != null) {
            line = line.trim();
            if (line.length() > 0) {
               Log.i("DeviceDb init read", line);
               Device d = new Device(appCtx, line);
               deviceList.add(d);
            }
            line = br.readLine();
         }
         isr.close();
         updateMap();
      } 
      catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   private void write() {
      try {
         FileOutputStream fout = appCtx.openFileOutput(DEVICES_FILE, Context.MODE_PRIVATE);
         OutputStreamWriter osw = new OutputStreamWriter(fout);
         BufferedWriter bw = new BufferedWriter(osw);
         
         Log.i("DeviceDb write", "start");
         for (Device dev : deviceList) {
            Log.i("DeviceDb write", dev.toString());
            bw.write(dev.getId()); 
            bw.write("\n");
         }
         
         bw.close();
         
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }      
   }
   
   public void add(String deviceId) {
      boolean exists = false;
      
      for (Device dev : deviceList) {
         if (dev.getId().equals(deviceId)) {
            exists = true;
         }
      }
      
      if (!exists){
         Device d = new Device(appCtx, deviceId);
         Log.i("DeviceDb add", d.toString());
         deviceList.add(d);
         updateMap();
         write();
      }
      else {
         Log.i("DeviceDb exists", deviceId);         
      }
   }
   
   public ArrayList<Device> getDeviceList() {
      return deviceList;
   }
   
   private void updateMap() {
      deviceMap.clear();
      for (Device dev : deviceList) {
         Map<String, String> map = new HashMap<String, String>(3);
         map.put("id", dev.getId());
         map.put("name", dev.getName());
         map.put("phone", dev.getPhoneNum());   
         deviceMap.add(map);
      }   
   }
   
   public List<Map<String, String>> getDeviceMap() {
      return deviceMap;
   }
   
   public void remove(String deviceId)
   {
      int removeIdx = -1;
      for (int i=0; i<deviceList.size(); i++) {
         if (deviceList.get(i).getId().equals(deviceId)) {
            removeIdx = i;
         }
      }
      
      if (removeIdx != -1) {
         deviceList.remove(removeIdx);
         updateMap();
         write();
      }
   }
   
}
