package ca.simplegames.micro.cache;

/**
 * MicroCacheException
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:24 PM)
 */
public class MicroCacheException extends Exception {
    /**
     * Constructor for MicroCacheException.
     */
    public MicroCacheException() {
        super();
    }

    /**
     * Constructor for MicroCacheException.
     *
     * @param message
     */
    public MicroCacheException(String message) {
        super(message);
    }

    /**
     * Constructor for MicroCacheException.
     *
     * @param message
     * @param cause
     */
    public MicroCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for MicroCacheException.
     *
     * @param cause
     */
    public MicroCacheException(Throwable cause) {
        super(cause);
    }
}
