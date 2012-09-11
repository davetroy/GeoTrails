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
  color trailColor;
  float lifespan;
  Tweet t = null;
  PVector loc = null;
  
  Trail(color col) {
    points = new ArrayList<Tweet>();
    trailColor = col; //color(random(255),random(255),random(255));
    lifespan = 255.0;
  }
  
  void add(Tweet t) {
     points.add(t);
     lifespan = 255.0;
  }
  
  void drawPoints() {

    noStroke();
    fill(trailColor,lifespan);
    
    Iterator<Tweet> it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      ellipse(loc.x,loc.y,10, 10);
    }    
  }
  
  void drawCurve() {
    // setup to draw the trail
    noFill();
    stroke(trailColor, lifespan);
    strokeWeight(4.0);

    beginShape();
    Iterator<Tweet> it = points.iterator();
    while (it.hasNext()) {
      t = it.next();
      loc = t.screenLocation();
      curveVertex(loc.x, loc.y);      
    }
    endShape();   
  } 
  
  void draw() {
    drawPoints();
    
    if (points.size()>1)
      drawCurve();
      
    lifespan *= 0.85;
        
    // display screen_name at end of trail
    //fill(200, t.lifespan/2.0);
    //text(t.screenName,loc.x-1, loc.y-2);
    
  }
  
  boolean isDead() {
    return (points.size()==0 || lifespan<1.0);
  }
  

}
