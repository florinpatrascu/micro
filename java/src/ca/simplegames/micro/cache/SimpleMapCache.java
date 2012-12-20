package ca.simplegames.micro.cache;

import org.jrack.Context;
import org.jrack.context.MapContext;

import java.util.*;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 7:15 PM)
 */
public class SimpleMapCache implements MicroCache {
    Context<String> cache = new MapContext<String>();

    public void addCache(String cacheName) throws MicroCacheException {
    }

    public Object get(Object key) throws MicroCacheException {
        return cache.get((String) key);
    }

    public void put(Object key, Object value) throws MicroCacheException {
        cache.with((String) key, value);
    }

    public void remove(Object key) throws MicroCacheException {
        cache.remove((String) key);
    }

    public void clear() throws MicroCacheException {
        cache = new MapContext<String>();
    }

    public void destroy() throws MicroCacheException {
        clear();
    }

    public void setFlushInterval(long interval) throws MicroCacheException {
    }

    public long getFlushInterval() throws MicroCacheException {
        return 0;
    }

    // todo: please refactor
    public List getKeys() throws MicroCacheException {
        List<String> keys = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : cache) {
            keys.add(entry.getKey());
        }

        return keys;
    }

    public Object getStatistics() throws MicroCacheException {
        return null;
    }
}
