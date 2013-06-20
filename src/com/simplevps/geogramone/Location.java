package com.simplevps.geogramone;

public class Location {

   private int latitudeE6;
   private int longitudeE6;
   private String info;
   
   Location(int lat, int lon, String _info) {
      latitudeE6 = lat;
      longitudeE6 = lon;
      info = _info;            
   }
   
   int getLatitude() { return latitudeE6; }
   int getLongitude() { return longitudeE6; }
   String getInfo() { return info; }
   
   public static double DMDMToDecimal(String dmdm)
   {
      String[] comps = dmdm.split("\\+");
      double degrees = Double.parseDouble(comps[0]);
      double dm = Double.parseDouble(comps[1])/60;
      boolean flipSign = false;
      if (degrees<0)
      {
         degrees = -degrees;
         flipSign = true;
      }
      return flipSign ? -(degrees+dm) : degrees+dm;       
   }
}
