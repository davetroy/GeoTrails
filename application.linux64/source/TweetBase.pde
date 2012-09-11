public class TweetBase {
  String[] tweets;
  int lastIndex;
  
  TweetBase() {
    tweets = loadStrings("trails2.csv");
    lastIndex = 0;
  }
  
  Tweet get(int index) {
    if (index<tweets.length) {
      lastIndex = index;
       return new Tweet(tweets[lastIndex]);
    } else {
      return null;
    }
  }
  
  Tweet next() {
    return this.get(lastIndex+1);    
  }
  
  ArrayList<Tweet> tweetsThrough(int maxTimestamp) {
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
