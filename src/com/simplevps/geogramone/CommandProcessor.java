package com.simplevps.geogramone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProcessor {
   public final int CMD_CURRENT_COORDINATES = 0;
   public final int CMD_MOTION_ALERT = 1;
   public final int CMD_SEND_INTERVAL = 4;
   public final int CMD_WRITE_EEPROM = 6;
   
   private List<Map<String, String>> commands;
   
   private static CommandProcessor instance = null;
   
   protected CommandProcessor() {
      commands = new ArrayList<Map<String, String>>();
      commands.add(createCommandMap(CMD_CURRENT_COORDINATES, "Current Coordinates", 0));
      commands.add(createCommandMap(CMD_MOTION_ALERT, "Motion Alert", 0));
      commands.add(createCommandMap(CMD_SEND_INTERVAL, "Send Interval", 1));
      commands.add(createCommandMap(CMD_WRITE_EEPROM, "Write EEPROM", 3));
   }
   
   public static CommandProcessor getInstance() {
      if (instance == null) {
         instance = new CommandProcessor();
      }
      return instance;
   }

   private HashMap<String, String> createCommandMap(int cmdNum, String cmdName, int numParams) {
      HashMap<String, String> m = new HashMap<String, String>(2);
      m.put("name", cmdName);
      m.put("cmdNum", Integer.valueOf(cmdNum).toString());
      m.put("numParams", Integer.valueOf(numParams).toString());
      return m;      
   }   
   
   public List<Map<String, String>> getCommands() {
      return commands;
   }
     
   public String makeCommand(String command, String pin, String p1, String p2, String p3) {
      int cmdNum = Integer.parseInt(command);
      String commandStr = null;
      switch(cmdNum) {
      case CMD_CURRENT_COORDINATES:
         commandStr = pin + "." + cmdNum; 
         break;
      case CMD_MOTION_ALERT:
         commandStr = pin + "." + cmdNum;
         break;
      case CMD_SEND_INTERVAL:
         commandStr = pin + "." + cmdNum + "." + p1;
         break;
      case CMD_WRITE_EEPROM:
         commandStr = pin + "." + cmdNum + "." + p1 + "." + p2 + "." + p3;
         break;
      default:
         break;   
      }
      
      return commandStr;
   }   
}
