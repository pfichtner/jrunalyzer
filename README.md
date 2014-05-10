# jrunalayzer [![Build Status](https://buildhive.cloudbees.com/job/pfichtner/job/jrunalyzer/badge/icon)](https://buildhive.cloudbees.com/job/pfichtner/job/jrunalyzer/)

JRunalayzer is a FAT-Client DockingFrame based Swing application for managing and displaying GPX files. 
GPX files can be imported using the application itselfs or alternativly placed directly in the $HOME/gpx directory. 

##Features
* Show all tracks as list and/or as calendar entries
* Map based visualization of tracks
* Visualization of track data such as profile, duration, distance and many other statistics
* Segment track(s) based on distances and/or durations including visualization of the segment statistics
* Highlight parts of track(s) based on distances and durations (e.g. fastest 400m or greatest distance covered within 12 minutes)
* Overlay multiple tracks (also possible when they are segmented)
* Auto-detection of similar tracks
* Waypoint support (waypoints found in the GPX are displayed when the track gets loaded)
* Common waypoint support (waypoints defined in $HOME/gpx/common.wpts will be shown on all tracks)
* Overall statistics showing aggregated statistics such as overall distance, duration, longest track, longest break, most active month/year and many more

![JRunalayzer Screenshot 1](/docs/screenshots/jrunalayzer1.png?raw=true)
JRunalayzer with calendar, track list, and statistics

![JRunalayzer Screenshot 2](/docs/screenshots/jrunalayzer2.png?raw=true)
JRunalayzer with calendar, profile, and statistics

![JRunalayzer Screenshot 3](/docs/screenshots/jrunalayzer3.png?raw=true)
JRunalayzer showing the comparison view and three segmented (by time) tracks overlayed

![JRunalayzer Screenshot 4](/docs/screenshots/jrunalayzer4.png?raw=true)
JRunalayzer showing the comparison view and three segmented (by time) tracks overlayed

![JRunalayzer Screenshot 5](/docs/screenshots/jrunalayzer5.png?raw=true)
JRunalayzer showing the segment view and three segmented (by distance) tracks overlayed

##How to run it
The JAR is a runnable JAR so you can just double click it. Alternativly you can call it on the command line and pass additionally arguments to the VM (see below): 
```
java jrunalayzer-vX.X.X.jar
```

##Tips & tricks
* If you'd like to use Nimbus L&F but it's not the default L&F of you VM you can pass -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel when running the JAR
* If your computer is behind a firewall you can pass java the environment variables http.proxyHost and http.proxyPort, e.g. -Dhttp.proxyHost=proxy.foo.bar -Dhttp.proxyPort=8080

##Current limitations
* Calendar plugin uses hard coded icon displaying all tracks as type "running"
* Only file based datasource provided (files places in $HOME/gpx)

##Plugins
JRunalayzer itself is just the Dock without any content. All views shown inside are implemented as plugins. 
JRunalayzer comes with the following base plugins: 
* Calendar plugin: Shows tracks in a calendar view
* Map plugin: Show track(s) on a map (OSM/OpenStreetMap)
* Profile plugin: Show elevation, speed and grade as chart
* Statistic plugin: Show statistics of actual loaded track
* Overall statistic plugin: Show statistics of all known tracks
*  Tracklist plugin: Show all known tracks as list including some statistics
* Track compare plugin: List tracks that are similar to the actual loaded track including some statistics
* Lap info plugin: Show statictis of the track's segments (when track is segmented)

##Technical features
* Platform independent (pure Java), runs on all Java supported platforms
* Scalable: Takes advantage of running on a multicore (CPU) machine
* Plugin/module concept (very easy to write your own plugin)
* Architecture: No frame (plugin/module) does depend on any other frame (plugin/module)
* Full i18n support
