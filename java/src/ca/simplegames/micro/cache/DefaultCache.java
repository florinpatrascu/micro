/*
 * Copyright (c)2012. Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro.cache;

import ca.simplegames.micro.utils.ResourceUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URL;
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
    private String name;
    private long flushInterval;

    public void addCache(String name, String... config) throws MicroCacheException {
        this.name = name;
        try {

            CacheManager manager;
            if (config != null && config[0] != null) {
                try {
                    // URL url = Micro.class.getResource(config[0]);
                    // new File(ResourceUtils.toURI(url).getSchemeSpecificPart());
                    URL url = ResourceUtils.getURL(config[0]);
                    manager = CacheManager.newInstance(url);
                } catch (Exception e) {
                    manager = CacheManager.getInstance();
                }
            } else {
                manager = CacheManager.getInstance();
            }

            cache = manager.getCache(name);

            if (cache == null) {
                log.info(String.format(
                        "Cannot find a user configuration for: '%s', will use 'defaultCache' definition.",
                        name));

                manager.addCache(name);
                cache = manager.getCache(name);
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
                throw new MicroCacheException("Class cast error: ", cce);
            } catch (IllegalArgumentException e) {
                throw new MicroCacheException(e);
            } catch (IllegalStateException e) {
                throw new MicroCacheException(e);
            }
        else {
            if (log.isDebugEnabled()) {
                log.debug(String.format("null key/value: %s/%s", key, value));
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

    public String getName() {
        return name;
    }
}
