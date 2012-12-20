package ca.simplegames.micro.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * The default Micro cache implementation is using Ehcache, see:
 * http://ehcache.org/, for more details
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:27 PM)
 */
public class DefaultCache implements MicroCache {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Cache cache;
    private long flushInterval;

    public void addCache(String cacheName) throws MicroCacheException {
        try {

            CacheManager manager = CacheManager.getInstance();
            cache = manager.getCache(cacheName);

            if (cache == null) {
                log.warn("Could not find a valid configuration for: " + cacheName
                        + ", using Ehcache's 'default' definition.");

                manager.addCache(cacheName);
                cache = manager.getCache(cacheName);
            }

        } catch (CacheException e) {
            throw new MicroCacheException(e);
        }
    }

    public Object get(Object key) throws MicroCacheException {
        try {

            if (key == null)
                throw new MicroCacheException("Invalid key specification: null");

            else {
                Element element = cache.get((Serializable) key);

                if (element == null) {
                    if (log.isDebugEnabled())
                        log.debug("null Element for key: [" + key + "]; (re)loading.");
                    return null;
                } else {
                    return element.getObjectValue();
                }
            }
        } catch (CacheException e) {
            throw new MicroCacheException(e);

        }
    }

    public void put(Object key, Object value) throws MicroCacheException {
        if (key != null && value != null)
            try {
                Element element = new Element((Serializable) key, (Serializable) value);
                //cache.putQuiet(element);
                cache.put(element);
            } catch (ClassCastException cce) {
                throw new MicroCacheException("(404) ", cce);
            } catch (IllegalArgumentException e) {
                throw new MicroCacheException(e);
            } catch (IllegalStateException e) {
                throw new MicroCacheException(e);
            }
        else {
            if (log.isDebugEnabled()) {
                log.debug("null key: " + key);
            }
        }
    }

    public void remove(Object key) throws MicroCacheException {
        try {
            cache.remove((Serializable) key);
        } catch (ClassCastException e) {
            throw new MicroCacheException(e);
        } catch (IllegalStateException e) {
            throw new MicroCacheException(e);
        }
    }

    public void clear() throws MicroCacheException {

        try {
            cache.removeAll();
        } catch (IllegalStateException e) {
            throw new MicroCacheException(e);
        }
    }

    public void destroy() throws MicroCacheException {
        try {
            CacheManager.getInstance().removeCache(cache.getName());
        } catch (IllegalStateException e) {
            throw new MicroCacheException(e);
        } catch (net.sf.ehcache.CacheException e) {
            throw new MicroCacheException(e);
        }
    }

    public void setFlushInterval(long interval) throws MicroCacheException {
        flushInterval = interval;
    }

    public long getFlushInterval() throws MicroCacheException {
        return flushInterval;
    }

    public List getKeys() throws MicroCacheException {
        try {
            return cache.getKeys();
        } catch (CacheException e) {
            log.error("Cannot get the keys of the elements stored in this cache");
            throw new MicroCacheException(e);
        }
    }

    public Object getStatistics() throws MicroCacheException {
        return cache.getStatistics();
    }
}
