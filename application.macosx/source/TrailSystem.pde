class TrailSystem {
  Hashtable trails;
  PFont labelFont;
  String[] palette = {"FFF17479","FFF29F63","FFE7CB46","FFB6D061","FF11E686","FF72DDB3","FF60CAE9","FF8999C3","FFBA80C3","FFDD8CB4"};
  String[] palette1 = {"FFFF8D91","FFFFB47C","FFFFED72","FFC5DD77","FF33FFA3","FF8AEBC5","FF78D9F5","FF9DACD3","FFCA95D1","FFEDA3C8"};
  color[] colors;
  int colorIndex;
  
  TrailSystem() {
    colors = new color[palette.length];
    colorIndex = 0;
    trails = new Hashtable();
    labelFont = createFont("Helvetica", 12);
    for (int i=0; i<palette.length; i++) {
      colors[i] = color(unhex(palette[i]));
    }
  }
  
  color nextColor() {
    if (colorIndex>=colors.length) colorIndex=0;
    return colors[colorIndex++];
  }
  
  Trail findOrCreateTrail(String screenName) {
    Trail trail = (Trail)trails.get(screenName);
    if (trail == null) {
      trail = new Trail(nextColor());
      trails.put(screenName, trail);
    }
    return trail;
  }

  void addTweets(ArrayList<Tweet> tweets) {    
    Iterator<Tweet> it = tweets.iterator();
    while (it.hasNext()) {
      Tweet t = it.next();
      Trail trail = findOrCreateTrail(t.screenName);
      trail.add(t);
    }   
  }
  
  int size() {
    return trails.size();
  }
  
  void draw() {
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
