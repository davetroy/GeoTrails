
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
