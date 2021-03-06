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

import ca.simplegames.micro.utils.CollectionUtils;
import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.apache.wink.common.internal.uritemplate.UriTemplateProcessor;
import org.jrack.RackResponse;

import java.util.List;
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
    private String route;
    private View view;
    private Map<String, Object> config;
    private String method = Globals.EMPTY_STRING;
    private UriTemplateProcessor processor;

    /**
     * Constructor
     *
     * @param route  The route route which is used for matching. (e.g. /hello, users/{name})
     * @param config a map containing nodes in a configuration loaded from an external support,
     *               an .yml file for example?!
     */
    protected Route(String route, Map<String, Object> config) {
        this.route = route;

        if (!CollectionUtils.isEmpty(config)) {
            this.config = config;
            if (config.get("method") != null) {
                this.method = ((String) config.get("method")).trim().toUpperCase();
            }

            // todo: Should refactor the nested controllers, so that they don't collide with the ones from the View, if there is a View?!
            view = new View(config.containsKey(Globals.VIEW) ?
                    (Map<String, Object>) config.get(Globals.VIEW) : config); // arghhh >:/
        }
    }

    /**
     * Invoked when a request is made on this route's corresponding route e.g. '/hello/{name}'
     * Micro framework will stop identifying other routes and will output the response created here
     *
     * @param context The micro context created when the Rack calls
     * @return a JRack response
     */
    public abstract RackResponse call(MicroContext context)
            throws Exception;

    public View getView() {
        return view;
    }

    public String getPath() {
        return route;
    }

    public String getCompiledRoute() {
        throw new IllegalAccessError("Not yet implemented!");
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public List<Map<String, Object>> getControllers() {
        if (view != null) {
            return view.getControllers();
        }
        return null;
    }

    /**
     * @return a string containing Request method names, all uppercase, or an empty String which matches:
     *         any method
     */
    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return String.format("'%s'; %s", route, method);
    }

    /**
     * Match a route.
     *
     * @param requestPath The request path submitted by the client
     * @param testPath    The match path
     */
    public UriTemplateMatcher match(String requestPath, String testPath) {

        if (processor == null) {
            // processor = new BitWorkingUriTemplateProcessor(testPath);
            processor = new JaxRsUriTemplateProcessor(testPath);
        }

        UriTemplateMatcher matcher = processor.matcher();
        return matcher.matches(requestPath) ? matcher : null;
    }
}
