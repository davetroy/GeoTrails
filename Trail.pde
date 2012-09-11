
public class Trail {
  ArrayList<Tweet> points;
  color trailColor;
  float lifespan;
  
  Trail(color col) {
    points = new ArrayList<Tweet>();
    trailColor = col; //color(random(255),random(255),random(255));
    lifespan = 255.0;
  }
  
  void add(Tweet t) {
     points.add(t);
     lifespan = 255.0;
  }
    
  void draw() {
    
//    if (points.size()<3)
//      return;

    Tweet t = null;
    PVector loc = null;

    noStroke();
        
    // put a circle for the first point on the trail
//    t = points.get(0);
//    loc = t.screenLocation();
//    ellipse(loc.x,loc.y,10, 10);

    fill(trailColor,lifespan);
    
    Iterator<Tweet> it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      ellipse(loc.x,loc.y,10, 10);
    } 
    
    // setup to draw the trail
    noFill();
    stroke(trailColor, lifespan);
    strokeWeight(4.0);

    beginShape();
    it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      
      curveVertex(loc.x, loc.y);
      
      t.lifespan *= 0.9;
      
//      if (t.isDead()) {
//        it.remove();
//      }
    }
    endShape();

    lifespan -= 10.0;
    
    
    // display screen_name at end of trail
    //fill(200, t.lifespan/2.0);
    //text(t.screenName,loc.x-1, loc.y-2);
    
  }
  
  boolean isDead() {
    return (points.size()==0 || lifespan<1.0);
  }
  

}
