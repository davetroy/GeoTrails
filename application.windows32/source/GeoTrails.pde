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

void setup() {
  size(979,1080);
  frameRate(30);
  smooth();
  
  mapImage = loadImage("baltimore_basemap.png");
  
  mercatorMap = new MercatorMap(979, 1080,
       39.4057, 39.1687,-76.757, -76.4782);

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

void draw() {
  
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
