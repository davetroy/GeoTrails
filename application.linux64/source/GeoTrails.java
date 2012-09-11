import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class GeoTrails extends PApplet {

// GeoTrails.pde
// (c) 2012 David Troy (@davetroy)
//
// Simple sketch to allow visualization of simple geo-based trails through time.
// Currently ingests a csv file of tweets in the format of:
// screen_name,timestamp,lat,lon
// and displays them as geo trails on a basemap (specified in this file)
// Trails are displayed with a persistence-of-vision effect and gradually fade out.
// The sketch is rendered at a target frame rate of 30fps and each frame loads
// a certain number of seconds worth of tweets to display (currently 287 seconds).
// The sketch will stop running when it runs out of tweets.

PImage mapImage;
MercatorMap mercatorMap;
PVector lastLoc;
TweetBase db;
Tweet current;
int currentTime;
TrailSystem trailSystem;
PFont legendFont, legendFont2;

public void setup() {
  size(979,1080);
  frameRate(30);
  smooth();
  
  mapImage = loadImage("baltimore_basemap.png");
  
  mercatorMap = new MercatorMap(979, 1080,
       39.4057f, 39.1687f,-76.757f, -76.4782f);

  legendFont = createFont("HelveticaNeue-Bold", 24);
  legendFont2 = createFont("HelveticaNeue-Light", 18);
  tint(100,100,100);
  lastLoc = new PVector(0,0);
  image(mapImage,0,0,width,height);
  
  db = new TweetBase();
  current = db.get(0);
  currentTime = current.timestamp;
  
  trailSystem = new TrailSystem();
  
}

public void draw() {
  
  // draw the base map
  image(mapImage,0,0,width,height);

  // grab tweets for this time interval
  currentTime += 287;
  ArrayList<Tweet> newTweets = db.tweetsThrough(currentTime);
  
  // stop if there are no more tweets to display
  if (newTweets.size()==0)
    exit();
    
  // add new tweets and draw the trails
  trailSystem.addTweets(newTweets);
  trailSystem.draw();

  // generate formatted date
  Date time = new java.util.Date((long)currentTime*1000);
  DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
  String reportDate = df.format(time);

  
  // draw the legends
  fill(255,255);
  //text(tweets.size(), 30, 25);
  textFont(legendFont);
  text("Baltimore Area Geocoded Tweets", 30,40);
  textFont(legendFont2);
  text(trailSystem.size(), 700, 40);
  text(reportDate, 30, 70);
  fill(255,128);
  text("Visualization by Dave Troy (@davetroy)", 30,1050);

  
  // save frames to make a movie later (warning! slow, large disk usage)
  //saveFrame("output/frames####.tiff");
}
/**
 * Utility class to convert between geo-locations and Cartesian screen coordinates.
 * Can be used with a bounding box defining the map section.
 *
 * (c) 2011 Till Nagel, tillnagel.com
 */
public class MercatorMap {
  
  public static final float DEFAULT_TOP_LATITUDE = 80;
  public static final float DEFAULT_BOTTOM_LATITUDE = -80;
  public static final float DEFAULT_LEFT_LONGITUDE = -180;
  public static final float DEFAULT_RIGHT_LONGITUDE = 180;
  
  /** Horizontal dimension of this map, in pixels. */
  protected float mapScreenWidth;
  /** Vertical dimension of this map, in pixels. */
  protected float mapScreenHeight;

  /** Northern border of this map, in degrees. */
  protected float topLatitude;
  /** Southern border of this map, in degrees. */
  protected float bottomLatitude;
  /** Western border of this map, in degrees. */
  protected float leftLongitude;
  /** Eastern border of this map, in degrees. */
  protected float rightLongitude;

  private float topLatitudeRelative;
  private float bottomLatitudeRelative;
  private float leftLongitudeRadians;
  private float rightLongitudeRadians;

  public MercatorMap(float mapScreenWidth, float mapScreenHeight) {
    this(mapScreenWidth, mapScreenHeight, DEFAULT_TOP_LATITUDE, DEFAULT_BOTTOM_LATITUDE, DEFAULT_LEFT_LONGITUDE, DEFAULT_RIGHT_LONGITUDE);
  }
  
  /**
   * Creates a new MercatorMap with dimensions and bounding box to convert between geo-locations and screen coordinates.
   *
   * @param mapScreenWidth Horizontal dimension of this map, in pixels.
   * @param mapScreenHeight Vertical dimension of this map, in pixels.
   * @param topLatitude Northern border of this map, in degrees.
   * @param bottomLatitude Southern border of this map, in degrees.
   * @param leftLongitude Western border of this map, in degrees.
   * @param rightLongitude Eastern border of this map, in degrees.
   */
  public MercatorMap(float mapScreenWidth, float mapScreenHeight, float topLatitude, float bottomLatitude, float leftLongitude, float rightLongitude) {
    this.mapScreenWidth = mapScreenWidth;
    this.mapScreenHeight = mapScreenHeight;
    this.topLatitude = topLatitude;
    this.bottomLatitude = bottomLatitude;
    this.leftLongitude = leftLongitude;
    this.rightLongitude = rightLongitude;

    this.topLatitudeRelative = getScreenYRelative(topLatitude);
    this.bottomLatitudeRelative = getScreenYRelative(bottomLatitude);
    this.leftLongitudeRadians = getRadians(leftLongitude);
    this.rightLongitudeRadians = getRadians(rightLongitude);
  }

  /**
   * Projects the geo location to Cartesian coordinates, using the Mercator projection.
   *
   * @param geoLocation Geo location with (latitude, longitude) in degrees.
   * @returns The screen coordinates with (x, y).
   */
  public PVector getScreenLocation(PVector geoLocation) {
    float latitudeInDegrees = geoLocation.x;
    float longitudeInDegrees = geoLocation.y;

    return new PVector(getScreenX(longitudeInDegrees), getScreenY(latitudeInDegrees));
  }

  private float getScreenYRelative(float latitudeInDegrees) {
    return log(tan(latitudeInDegrees / 360f * PI + PI / 4));
  }

  protected float getScreenY(float latitudeInDegrees) {
    return mapScreenHeight * (getScreenYRelative(latitudeInDegrees) - topLatitudeRelative) / (bottomLatitudeRelative - topLatitudeRelative);
  }
  
  private float getRadians(float deg) {
    return deg * PI / 180;
  }

  protected float getScreenX(float longitudeInDegrees) {
    float longitudeInRadians = getRadians(longitudeInDegrees);
    return mapScreenWidth * (longitudeInRadians - leftLongitudeRadians) / (rightLongitudeRadians - leftLongitudeRadians);
  }
}

// Trail.pde
// (c) 2012 David Troy (@davetroy)
//
// wrapper for an ArrayList of points that contains individual tweets
// trails belong to a TrailSystem (like a particle belongs to a ParticleSystem)
// a trail starts out with a lifespan (and opacity) of 255.0 and then decays down to 0
// when it reaches 0, it is considered dead and is removed from the trailsystem.
// Every time a new point is added to the trail its lifespan is (arbitrarily) renewed to 255.0.

public class Trail {
  ArrayList<Tweet> points;
  int trailColor;
  float lifespan;
  Tweet t = null;
  PVector loc = null;
  
  Trail(int col) {
    points = new ArrayList<Tweet>();
    trailColor = col; //color(random(255),random(255),random(255));
    lifespan = 255.0f;
  }
  
  public void add(Tweet t) {
     points.add(t);
     lifespan = 255.0f;
  }
  
  public void drawPoints() {

    noStroke();
    fill(trailColor,lifespan);
    
    Iterator<Tweet> it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      ellipse(loc.x,loc.y,10, 10);
    }    
  }
  
  public void drawCurve() {
    // setup to draw the trail
    noFill();
    stroke(trailColor, lifespan);
    strokeWeight(4.0f);

    beginShape();
    Iterator<Tweet> it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      curveVertex(loc.x, loc.y);      
    }
    endShape();   
  } 
  
  public void draw() {
    drawPoints();
    
    if (points.size()>1)
      drawCurve();
      
    lifespan *= 0.85f;
        
    // display screen_name at end of trail
    //fill(200, t.lifespan/2.0);
    //text(t.screenName,loc.x-1, loc.y-2);
    
  }
  
  public boolean isDead() {
    return (points.size()==0 || lifespan<1.0f);
  }
  

}
// TrailSystem.pde
// (c) 2012 David Troy (@davetroy)
//
// TrailSystem is a wrapper for a Hashtable that tracks trails for individual objects
// in this case, the keys are screenNames associated with tweets. We keep all of the
// active trails in the TrailSystem. When a trail finally fades out and dies, we remove
// it from the system. The system is also responsible for rotating through our color palette.
// (Two palettes provided, courtesy of Friends of the Web, Baltimore -- one dark, one light --
// note that hex colors are provided in 32-bit alpha+rgb order format.)

class TrailSystem {
  Hashtable trails;
  PFont labelFont;
  String[] palette = {"FFF17479","FFF29F63","FFE7CB46","FFB6D061","FF11E686","FF72DDB3","FF60CAE9","FF8999C3","FFBA80C3","FFDD8CB4"};
  String[] palette1 = {"FFFF8D91","FFFFB47C","FFFFED72","FFC5DD77","FF33FFA3","FF8AEBC5","FF78D9F5","FF9DACD3","FFCA95D1","FFEDA3C8"};
  int[] colors;
  int colorIndex;
  
  TrailSystem() {
    colors = new int[palette.length];
    colorIndex = 0;
    trails = new Hashtable();
    labelFont = createFont("Helvetica", 12);
    for (int i=0; i<palette.length; i++) {
      colors[i] = color(unhex(palette[i]));
    }
  }
  
  public int nextColor() {
    if (colorIndex>=colors.length) colorIndex=0;
    return colors[colorIndex++];
  }
  
  public Trail findOrCreateTrail(String screenName) {
    Trail trail = (Trail)trails.get(screenName);
    if (trail == null) {
      trail = new Trail(nextColor());
      trails.put(screenName, trail);
    }
    return trail;
  }

  public void addTweets(ArrayList<Tweet> tweets) {    
    Iterator<Tweet> it = tweets.iterator();
    while (it.hasNext()) {
      Tweet t = it.next();
      Trail trail = findOrCreateTrail(t.screenName);
      trail.add(t);
    }   
  }
  
  public int size() {
    return trails.size();
  }
  
  public void draw() {
    Iterator<Trail> it = trails.values().iterator();
    while (it.hasNext()) {
      Trail tr = it.next();
      textFont(labelFont);

      tr.draw();
      
      // remove the trail from the system if it is dead
      if (tr.isDead()) {
        it.remove();
      }
    }
    
  }
  
}
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
    lifespan=255.0f;
  }
  
  public PVector screenLocation() {
    return mercatorMap.getScreenLocation(new PVector(lat, lon));
  }
  
  public boolean isDead() {
    if (lifespan <= 0.0f) {
      return true;
    } else {
      return false;
    }
  }

}
// TweetBase.pde
// (c) 2012 David Troy (@davetroy)
//
// Acts as a data model and factory for storing, retrieving, and making Tweets, which can
// then be added to Trails and into the TrailSystem. TweetBase allows us to retrieve a span
// of tweets generated over a timestamp range (tweetsThrough) and to return those as an
// ArrayList of fully constituted tweets.

public class TweetBase {
  String[] tweets;
  int lastIndex;
  
  TweetBase() {
    tweets = loadStrings("trails2.csv");
    lastIndex = 0;
  }
  
  public Tweet get(int index) {
    if (index<tweets.length) {
      lastIndex = index;
       return new Tweet(tweets[lastIndex]);
    } else {
      return null;
    }
  }
  
  public Tweet next() {
    return this.get(lastIndex+1);    
  }
  
  public ArrayList<Tweet> tweetsThrough(int maxTimestamp) {
    ArrayList<Tweet> tweets = new ArrayList<Tweet>();
    Tweet tweet;
    do {
      tweet = this.next();
      if (tweet != null) {
        tweets.add(tweet);
      }
    } while ( (tweet != null) && (tweet.timestamp<=maxTimestamp) );
    return tweets;
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GeoTrails" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
