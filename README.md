TrackMyself-Automatic-Tracking-Positioning-System-through-RESTful-webservices-and-Android-app
=========================================================================================

The project consists in the tracking of the user's location through an ANDROID app that communicates with a remote database (MySQL) through RESTful webservices built with JAVA Enterprise 7 (using the Glassfish 4 container).

The Android app allows you to capture and collect, with various options, the datas about user's current location. The app can also show the map with the routes created with the points stored in the remote database.

The app is designed to work offline: after the user has logged in, the app can collect the data in a local database (SQLite); when the device goes back online, all caches in the local database will be sent to the remote database through the RESTful webservices. The communication between the app and the webservices is done with messages formatted according to the JSON standard.

The app has several working modes and can manage multiple users: when the app is started for the first time, a screen allows the user to log in to the service or sign up using an other screen.

After logged in, the app runs with default preferences that the user can change. 

Options are divided into three groups:

  -	Tracking:
    -	Enable tracking
    -	Position acquisition rate
  -	Network:
    -	Automatic map update 
    -	Output data-rate (to the server)
    -	Show the route of the last xx minutes/hours (customizable)
  -	Map:
    -	Route drawing mode (driving, walking, free).
    -	Default zoom level
    -	Map Mode (Map, Earth, Hybrid)
    -	Tracking color

The app has also a Service that runs in background; it sends the data to the server and collects the data about the current location.

This project was developed by Alessio Oglialoro and Daniele Saitta.
