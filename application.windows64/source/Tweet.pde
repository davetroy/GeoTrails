// Tweet.pde
// (c) 2012 David Troy (@davetroy)
//
// A simple class for each tweet. For the purposes of trails we are only storing
// a unix timestamp, a screenName, and a latitude and longitude. An optional lifespan
// is specified here but is not used. It could be used for aging out and removing a tweet
// from a trail. Each tweet acts as a waypoint on a gps trail. Trails with a length of
// one tweet (waypoint) just act as single markers and are plotted accordingly.

public class Tweet {
  int timestamp;
  float lat, lon;
  String screenName;
  float lifespan;
  
  Tweet(String rowString) {
    String[] row = split(rowString, ',');
    screenName = row[0];
    timestamp = parseInt(row[1]);
    lat = parseFloat(row[2]);
    lon = parseFloat(row[3]);
    lifespan=255.0;
  }
  
  PVector screenLocation() {
    return mercatorMap.getScreenLocation(new PVector(lat, lon));
  }
  
  boolean isDead() {
    if (lifespan <= 0.0) {
      return true;
    } else {
      return false;
    }
  }

}
