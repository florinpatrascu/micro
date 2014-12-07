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

import org.jrack.Context;
import org.jrack.context.MapContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 7:15 PM)
 */
public class SimpleMapCache implements MicroCache {
    Map<String, Object> cache = new ConcurrentHashMap<String, Object>(8, 0.9f, 2);
    private String name = "map";

    public void addCache(String name, String ... config) throws MicroCacheException {
        this.name = name;
    }

    public Object get(Object key) throws MicroCacheException {
        return cache.get((String) key);
    }

    public void put(Object key, Object value) throws MicroCacheException {
        cache.put((String) key, value);
    }

    public void remove(Object key) throws MicroCacheException {
        cache.remove((String) key);
    }

    public void clear() throws MicroCacheException {
        cache = new ConcurrentHashMap<String, Object>(8, 0.9f, 2);
    }

    public void destroy() throws MicroCacheException {
        clear();
    }

    public void setFlushInterval(long interval) throws MicroCacheException {
    }

    public long getFlushInterval() throws MicroCacheException {
        return 0;
    }

    public List getKeys() throws MicroCacheException {
        return new ArrayList<String>(cache.keySet());
    }

    public Object getStatistics() throws MicroCacheException {
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return ((Map)cache).size();
    }
}
