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

package ca.simplegames.micro;

import ca.simplegames.micro.repositories.Repository;
import org.jrack.RackResponse;

import java.util.Map;

/**
 * A Route is used for url-matching functions.
 * <p/>
 * When a request is made, if present, the matching routes 'call' method is invoked.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 2:31 PM)
 */
public abstract class Route {
    private String path;
    private Repository repository;
    private String controller;
    private String page;
    private Map<String, Object> config;

    /**
     * Constructor
     *
     * @param path   The route path which is used for matching. (e.g. /hello, users/{name})
     * @param config a map containing nodes in a configuration loaded from an external support,
     *               an .yml file for example?!
     */
    protected Route(String path, Map<String, Object> config) {
        this.path = path;
        this.config = config;
    }

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello/{name}'
     * Micro framework will stop identifying other routes and will output the response created here
     *
     * @param context The micro context created when the Rack calls
     * @return a JRack response
     */
    public abstract RackResponse call(MicroContext context);

    /**
     * Returns this route's path
     */
    public String getPath() {
        return this.path;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getController() {
        return controller;
    }

    public String getPage() {
        return page;
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}
