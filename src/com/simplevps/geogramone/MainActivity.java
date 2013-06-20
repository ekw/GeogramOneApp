package com.simplevps.geogramone;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

   private static int debugIdx = 0;
   private static final String TAG = "MainActivity";      
   private MyItemizedOverlay prevLocationsOverlay = null;
   private MyItemizedOverlay currLocationOverlay = null;
   private LocationHandler locationHandler = null;
   private boolean locationHandlerRegistered = false;
   
   //======================================================================
   // Intent Handler 
   //======================================================================
   private class LocationHandler extends BroadcastReceiver {
      @Override
      public void onReceive(Context context, Intent intent) 
      {    
         String action = intent.getAction();
          
         // Location update from SMS
         if(action.equalsIgnoreCase(SmsReceiver.MSG_UPDATE_MAP)){ 
            Log.i(TAG, "MSG_UPDATE_MAP Received");
            Bundle b = intent.getExtras();
            String devId = b.getString("devId");           
            updateLocation(devId);
         }
         else {
            Log.w(TAG, "LocationHandler: Unknown action [" + action + "]");
         }             
      }
   }

   //======================================================================
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       MapView mapView = (MapView) findViewById(R.id.mapview);
       mapView.setUseSafeCanvas(false);
       mapView.setBuiltInZoomControls(true);
       mapView.setMultiTouchControls(true);
       
       locationHandler = new LocationHandler();
       ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

       Drawable marker = getResources().getDrawable(android.R.drawable.presence_offline);
       int markerWidth = marker.getIntrinsicWidth();
       int markerHeight = marker.getIntrinsicHeight();
       marker.setBounds(0, markerHeight, markerWidth, 0);

       Drawable currMarker = getResources().getDrawable(android.R.drawable.presence_online);
       markerWidth = marker.getIntrinsicWidth();
       markerHeight = marker.getIntrinsicHeight();
       currMarker.setBounds(0, markerHeight, markerWidth, 0);

       prevLocationsOverlay = new MyItemizedOverlay(marker, resourceProxy);
       currLocationOverlay  = new MyItemizedOverlay(currMarker, resourceProxy);
       mapView.getOverlays().add(prevLocationsOverlay);
       mapView.getOverlays().add(currLocationOverlay);
       
       DeviceDb devDb = DeviceDb.getInstance();
       devDb.init(getApplicationContext());
   }  
   
   //======================================================================
   protected void onResume() {
      super.onResume();
      
      if (!locationHandlerRegistered) {
         registerReceiver(locationHandler, new IntentFilter(SmsReceiver.MSG_UPDATE_MAP));
         locationHandlerRegistered = true;
      }
   }
   
   //======================================================================
   protected void onPause() {
      super.onPause();
      
      if (locationHandlerRegistered) {
         unregisterReceiver(locationHandler);
         locationHandlerRegistered = false;
      }
   }
   
   //======================================================================
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }
   
   //======================================================================
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
          case R.id.action_commands:
             startActivity(new Intent(this, CommandActivity.class));
             break;
          case R.id.action_devices:
             startActivity(new Intent(this, DevicesActivity.class));
             break;
          case R.id.action_debug:
             doDebug();
             break;
       }
       return true;
   }
   
   private void updateLocation(String devId) {
      LocationModel lm = LocationModel.getInstance();
      List<Location> locList = lm.getLocations(devId);
      Location prevLoc = null;
      Location currLoc = locList.get(locList.size()-1);
      
      if (locList.size() > 1) {
         prevLoc = locList.get(locList.size()-2);
         GeoPoint p = new GeoPoint(prevLoc.getLatitude(), prevLoc.getLongitude());
         prevLocationsOverlay.addItem(p, prevLoc.getInfo(), prevLoc.getInfo());
      }
      
      MapView mapView = (MapView) findViewById(R.id.mapview);
      
      GeoPoint myPoint1 = new GeoPoint(currLoc.getLatitude(), currLoc.getLongitude());
      currLocationOverlay.removeAllItems();
      currLocationOverlay.addItem(myPoint1, currLoc.getInfo(), currLoc.getInfo());
      
      mapView.getController().setZoom(13);
      mapView.getController().animateTo(myPoint1);
      mapView.invalidate();
   }
   
   private void doDebug() {      
      double slat = 34.103395; 
      double slon = -118.237057;
    
      Location loc = new Location(
            (int)(slat*1000000), 
            (int)((slon + 0.01*debugIdx)*1000000), 
            "point"+Integer.toString(debugIdx));
      LocationModel.getInstance().add("testId", loc);
      
      updateLocation("testId");
      debugIdx++;
   }
}
