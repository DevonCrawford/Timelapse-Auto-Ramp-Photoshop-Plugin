# Timelapse-Auto-Ramp

My favourite project so far. 

Analyzes a sequence of RAW images (.xmp files) and automatically ramps the exposure to achieve smooth lighting results when changing a setting on the camera. 

My algorithm simply looks for changes in shutter speed, aperture, iso, then mathematically calculates the exposure offset required to match the difference, evenly applies the change across leading images, and writes new exposure to the RAW (.xmp) files. This allows for a reopening of after effects to view these changes with no issue, and use the ramped values when rendering. This makes best use in difficult lighting conditions such as a timelapse of a sunrise, where your best option is to change the exposure manually, as the sun rises. If you did not use this technique you will notice flickering on auto mode, or stick with one setting on manual, making some parts too dark and others too bright. 

How to use:
  - manually change camera settings when needed to achieve good lighting
  - this will create ugly flickering for a timelapse, but my program is made to even out this change
  - edit images in photostop lightroom, be sure to SAVE METADATA TO FILE, which creates .xmp files for my program to use
  - go into main class "proRaw.java", set the folder to specified location of images
  - set startImage to the first image number in sequence
  
debugging:
  - comment out updateMetadata(); to stop overwriting of new exposure metadata
