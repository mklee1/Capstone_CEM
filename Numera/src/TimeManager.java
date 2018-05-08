
public class TimeManager {
    private final long startTime;
    
    public TimeManager() {
    	startTime = System.currentTimeMillis();
    }
    
    public long elapsedTime() {
    	return System.currentTimeMillis() - startTime;
    }
    
    public long elapsedSeconds() {
    	return elapsedTime() / 1000;
    }
    
    public long secondsDisplay() {
    	return elapsedSeconds() % 60;
    }
    
    public long elapsedMinutes() {
    	return elapsedSeconds() / 60;
    }
}
