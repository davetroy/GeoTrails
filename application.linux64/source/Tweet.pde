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
