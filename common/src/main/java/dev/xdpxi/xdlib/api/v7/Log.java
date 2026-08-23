package dev.xdpxi.xdlib.api.v7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Per-modid logger. Create one instance per mod (typically in your init method) and reuse it:
 * {@code public static final Log LOG = Log.create("mymodid");}
 */
public class Log {
  private final Logger logger;

  private Log(String modId) {
    this.logger = LoggerFactory.getLogger(modId);
  }

  public static Log create(String modId) {
    return new Log(modId);
  }

  public void trace(String message, Object... args) {
    logger.trace(message, args);
  }

  public void debug(String message, Object... args) {
    logger.debug(message, args);
  }

  public void info(String message, Object... args) {
    logger.info(message, args);
  }

  public void warn(String message, Object... args) {
    logger.warn(message, args);
  }

  public void error(String message, Object... args) {
    logger.error(message, args);
  }

  public void error(String message, Throwable throwable) {
    logger.error(message, throwable);
  }
}
