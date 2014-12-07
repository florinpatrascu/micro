package ca.simplegames.micro.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Generic utility class for monitoring a file specific to a reloadable object
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-08-28 8:27 PM)
 */
public class MicroConfigFileMonitor extends TimerTask {
  private static final Logger log = LoggerFactory.getLogger(MicroConfigFileMonitor.class);
  public static final int DEFAULT_DELAY = 5000;
  private Reloadable reloadableObject;
  private boolean running = false;
  private long lastModified;
  private final Timer timer;
  private File configFile;
  private int delay;

  /**
   * @param configFile       a file that will be monitored for changes
   * @param reloadableObject the object that has to be reloaded
   * @param delay            the number of seconds between consecutive file updates checks.
   */
  public MicroConfigFileMonitor(File configFile, Reloadable reloadableObject, int delay) throws Exception {
    Assert.notNull(configFile, "the `configFile` parameter cannot be null");

    this.configFile = configFile;
    this.reloadableObject = reloadableObject;
    this.delay = delay == 0 ? DEFAULT_DELAY : delay * 1000;

    if (!configFile.exists()) {
      throw new FileNotFoundException(configFile.getAbsolutePath());
    }

    lastModified = configFile.lastModified();
    timer = new Timer();

    reloadableObject.reload();
    startMonitor();
  }

  /**
   * start monitoring the file
   */
  public void startMonitor() {
    if (!running) {
      log.info(String.format("Starting MicroConfigFileMonitor for: %s", configFile.getAbsolutePath()));
      timer.schedule(this, 0, delay);
      running = true;
    }
  }

  /**
   * Stop the monitoring thread.
   */
  public void stopMonitor() {
    if (timer != null && running) {
      log.info(String.format("Stopping MicroConfigFileMonitor for: %s", configFile.getAbsolutePath()));

      running = false;

      this.cancel();
      timer.purge(); //or .cancel();
    }
  }

  /**
   * This method should not be executed directly!
   */
  public void run() {
    long currentLastModified = configFile.lastModified();

    if (running && currentLastModified != lastModified) {

      try {
        reloadableObject.reload();
        lastModified = currentLastModified;
        log.info(String.format("%s, reloaded.", configFile.getAbsolutePath()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
