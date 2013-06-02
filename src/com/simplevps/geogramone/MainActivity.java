package com.simplevps.geogramone;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

   MyItemizedOverlay myItemizedOverlay = null;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       MapView mapView = (MapView) findViewById(R.id.mapview);
       mapView.setUseSafeCanvas(false);
       mapView.setBuiltInZoomControls(true);
       mapView.setMultiTouchControls(true);
       
       Drawable marker=getResources().getDrawable(android.R.drawable.star_big_on);
       int markerWidth = marker.getIntrinsicWidth();
       int markerHeight = marker.getIntrinsicHeight();
       marker.setBounds(0, markerHeight, markerWidth, 0);
        
       ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        
       myItemizedOverlay = new MyItemizedOverlay(marker, resourceProxy);
       mapView.getOverlays().add(myItemizedOverlay);
        
       GeoPoint myPoint1 = new GeoPoint(0*1000000, 0*1000000);
       myItemizedOverlay.addItem(myPoint1, "myPoint1", "myPoint1");
       GeoPoint myPoint2 = new GeoPoint(50*1000000, 50*1000000);
       myItemizedOverlay.addItem(myPoint2, "myPoint2", "myPoint2");
        
   } 

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
          case R.id.action_commands:
             Toast t1 = Toast.makeText(getApplicationContext(), 
                   "Commands", 
                   Toast.LENGTH_LONG);
             t1.setGravity(Gravity.TOP, 0, 100);
             t1.show();
             break;
          case R.id.action_devices:
             startActivity(new Intent(this, DevicesActivity.class));
             break;
       }
       return true;
   }
}
