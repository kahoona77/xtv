package de.kahoona.xtv.data

/**
 * Created by Benni on 22.04.2014.
 */
public interface BandwidthLimiter {
  public static final long OneSecond = 1000;          //< 1s == 1000ms
//  public static final long OneKbps = 1000;            //< one kilobit per second
//  public static final long OneMbps = 1000 * OneKbps;  //< one megabit per second

  public void setDownstreamKbps(long downstreamKbps);

  public void setUpstreamKbps(long upstreamKbps);

  public void setLatency(long latency);
}