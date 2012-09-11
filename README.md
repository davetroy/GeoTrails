GeoTrails
=========

(c) 2012 Dave Troy (@davetroy)

Simple Processing sketch to allow visualization of simple geo-based trails through time.
Currently ingests a csv file of tweets in the format of:
screen_name,timestamp,lat,lon
and displays them as geo trails on a basemap.

Create a Mercator-projection basemap using TileMill (http://tilemill.com) as per:
http://tillnagel.com/2011/06/tilemill-for-processing/

Trails are displayed with a persistence-of-vision effect and gradually fade out.
The sketch is rendered at a target frame rate of 30fps and each frame loads
a certain number of seconds worth of tweets to display (currently 287 seconds).
The sketch will stop running when it runs out of tweets.