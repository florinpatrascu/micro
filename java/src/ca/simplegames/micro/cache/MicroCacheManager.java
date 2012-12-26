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

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:26 PM)
 */
public class MicroCacheManager {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, MicroCache> cacheImplementations = new HashMap<String, MicroCache>();
    private String cacheClass;

    public MicroCacheManager(SiteContext site) {
        try {
            Map<String, Object> cacheConfig = (Map<String, Object>) site.get(Globals.MICRO_CACHE_CONFIG);

            List<String> cachesNames = (List<String>) cacheConfig.get("names");
            cacheClass = (String) cacheConfig.get("class");

            for (String cacheName : cachesNames) {
                Class aClass = ClassUtilities.loadClass(cacheClass);
                MicroCache microCache = (MicroCache) aClass.newInstance();

                microCache.addCache(cacheName);
                microCache.setFlushInterval(0); //todo implement me

                cacheImplementations.put(cacheName, microCache);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error while defining the cache system; "+e.getMessage());
        }

        site.with(Globals.MICRO_CACHE_MANAGER, this);
    }

    /**
     * returns a cache storage by name
     *
     * @param cacheName the name of the cache
     * @return the cache implementation or null if the cache name doesn't exists
     */
    public MicroCache getCache(String cacheName) {
        if (cacheName == null || cacheName.trim().length() == 0
                || !cacheImplementations.containsKey(cacheName)) {
            return null;
        }

        return (MicroCache) cacheImplementations.get(cacheName);
    }

    /**
     * @return the names of the cache currently instantiated
     */
    public String[] getAvailableCacheNames() {
        return (String[]) cacheImplementations.keySet().toArray(
                new String[cacheImplementations.keySet().size()]);
    }

    public MicroCache getCacheWithDefault(String scriptControllersCacheName) {
        if (!cacheImplementations.containsKey(scriptControllersCacheName)) {
            MicroCache microCache = null;

            try {
                Class aClass = Class.forName(cacheClass);
                microCache = (MicroCache) aClass.newInstance();
                microCache.addCache(scriptControllersCacheName);
            } catch (Exception e) {
                e.printStackTrace();
                microCache = new SimpleMapCache();
            }
            cacheImplementations.put(scriptControllersCacheName, microCache);
        }

        return cacheImplementations.get(scriptControllersCacheName);
    }
}
