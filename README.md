# Timelapse-Auto-Ramp

My favourite project so far. This program is made for manual timelapse photographers (like me) to use with photoshop!

My algorithm simply looks for changes in shutter speed, aperture, iso, then mathematically calculates the exposure offset required to match the difference between a change in two pictures, evenly applies the exposure change across leading images, and writes new exposure to the RAW (.xmp) files. This allows photoshop or after effects to view these changes with no issue, and use the ramped values when rendering. This makes best use in difficult lighting conditions such as a timelapse of a sunrise, where your best option is to change the exposure manually, as the sun rises. If you did not use this technique you will notice flickering on auto mode, or stick with one setting on manual, making some parts underexposed and others overexposed.

### Example. Original (left) vs Processed (right)

![examples](https://cloud.githubusercontent.com/assets/25334129/22401628/1e86ca5a-e5a9-11e6-904b-ace3d74c7ed5.gif)

### How to shoot:
  - manually change camera settings when shooting to achieve good lighting (this will create a flicker during the timelapse, but my program is made to even out this change)
  - edit images in photostop lightroom, be sure to SAVE METADATA TO FILE, which creates .xmp files for my program to use

### How to use:
  - open up command prompt / terminal
  - cd to location of program (ex. cd Downloads)
  - run by typing "java -jar proRAW.jar"
  - input the file path of the first image
  
![capture](https://cloud.githubusercontent.com/assets/25334129/22497738/c0901156-e821-11e6-9e15-04303fca7e5c.PNG)
  
### Debugging:
  - comment out updateMetadata(); to stop overwriting of new exposure metadata

Note: This program currently has no GUI therefore when you click on the .jar file it appears to not open. Make sure you open the file in command prompt following the instructions above.

*a project by Devon Crawford.*
