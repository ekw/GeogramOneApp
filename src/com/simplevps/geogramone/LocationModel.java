package com.simplevps.geogramone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationModel {
   
   private static LocationModel instance = null;
   private Map<String, List<Location>> locations;
   
   protected LocationModel() {
      locations = new HashMap<String, List<Location>>();
   }
   public static LocationModel getInstance() {
      if (instance == null) {
         instance = new LocationModel();
      }
      return instance;
   }
   
   public void add(String deviceId, Location loc)
   {
      if (locations.containsKey(deviceId)) {
         locations.get(deviceId).add(loc);
      }
      else {
         ArrayList<Location> locList = new ArrayList<Location>();
         locList.add(loc);
         locations.put(deviceId, locList);
      }   
   }
   
   public List<Location> getLocations(String deviceId) {
      if (locations.containsKey(deviceId))
      {
         return locations.get(deviceId);
      }
      else {
         return null;
      }
   }   
}
