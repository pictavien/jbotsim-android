![Build Status](https://travis-ci.org/pictavien/jbotsim-android.svg?branch=develop)
![License](https://img.shields.io/badge/license-LGPL%20&ge;%203.0-informational.svg)

# Android port of [JBotSim](http://jbotsim.io) library

This library is a simple implementation of an [Android Activity](jbotsim-ui-android/src/main/java/io/jbotsim/ui/android/AndroidViewerActivity.java) 
used as a viewer for JBotSim topologies. It simply mimics behaviors of the `JViewer` class of JBotSim.

The project is divided in three parts:

* [`jbotsim-ui-android`](jbotsim-ui-android) contains the implementation of the viewer for Android Platform.
* [`jbotsim-examples`](jbotsim-examples) is a port of JBotSim examples in a single application.
* [`simple-viewer`](simple-viewer) is a kind of template to implement a viewer for specific nodes. It can be used as
an entry-point for whom wants to port an algorithm on this platform

 
