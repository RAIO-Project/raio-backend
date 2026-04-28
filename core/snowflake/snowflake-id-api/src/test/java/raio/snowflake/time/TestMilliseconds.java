package raio.snowflake.time;

import club.time.MillisecondsSupplier;

public final class TestMilliseconds implements MillisecondsSupplier {
    public long currentMilliseconds;
    
    public TestMilliseconds() {
        currentMilliseconds = System.currentTimeMillis();
    }
    
    @Override
    public long getAsLong() {
        return currentMilliseconds;
    }
    
    public void nextMillisecond() {
        currentMilliseconds += 1L;
    }
}
