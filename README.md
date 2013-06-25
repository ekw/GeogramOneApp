GeogramOneApp
=============

Android app to communicate via SMS with the [Geogram ONE] [1], an open source tracking device.   
See the Geogram ONE [Application Overview] [2] for the device specifications.  
[1]: http://dsscircuits.com/geogram-one.html "Geogram ONE"
[2]: http://dsscircuits.com/images/datasheets/Geogram%20ONE%20Application%20Overview.pdf "Application Overview"

Right now, only these commands are implemented:

1. Option 0 - Return current coordinates (The coordinates in the response message will be displayed on a map)
2. Option 1 - Motion alert 
3. Option 4 - Send interval 
4. Option 6 - EEPROM configuration

OpenStreetMap is the map provider used by this app.

I am not associated with the maker of Geogram ONE in any way.  I bought a Geogram ONE and wrote this app for my own use.