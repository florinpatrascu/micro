package ca.simplegames.micro.cache;

import java.util.List;

/**
 * A simple cache interface
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:22 PM)
 */
public interface MicroCache {

    /**
     * Add or creates a new cache with a given name
     *
     * @param cacheName the name of the cache
     * @throws MicroCacheException
     */
    public void addCache(String cacheName) throws MicroCacheException;


    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     * @throws MicroCacheException
     */
    public Object get(Object key) throws MicroCacheException;


    /**
     * Puts an object into the cache.
     *
     * @param key   a serializable} key
     * @param value a Serializable value
     * @throws MicroCacheException if the parameters are not {@link java.io.Serializable} or another {@link Exception} occurs.
     */
    public void put(Object key, Object value) throws MicroCacheException;


    /**
     * Removes the element which matches the key.
     * <p/>
     * If no element matches, nothing is removed and no Exception is thrown.
     *
     * @param key the key of the element to remove
     * @throws MicroCacheException
     */
    public void remove(Object key) throws MicroCacheException;

    /**
     * Remove all elements in the cache, but leave the cache
     * <p/>
     * in a useable state.
     *
     * @throws MicroCacheException
     */
    public void clear() throws MicroCacheException;


    /**
     * Remove the cache and make it unuseable.
     *
     * @throws MicroCacheException
     */
    public void destroy() throws MicroCacheException;

    /**
     * define the cache flush interval
     *
     * @param interval
     * @throws MicroCacheException
     */
    public void setFlushInterval(long interval) throws MicroCacheException;

    /**
     * retrieves the flushing interval
     *
     * @throws MicroCacheException
     */
    public long getFlushInterval() throws MicroCacheException;

    /**
     * @return
     * @throws MicroCacheException
     */
    public List getKeys() throws MicroCacheException;

    /**
     * obtain statistics on the current cache
     *
     * @return a "statistics" value object if supported
     * @throws MicroCacheException
     */
    public Object getStatistics() throws MicroCacheException;
}
