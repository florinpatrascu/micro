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

package ca.simplegames.micro.route;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.Route;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

/**
 * The Route manager is responsible for initializing various Routes at startup time, and for executing those
 * {@link Route} instances that are matching the request path at runtime
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 6:04 PM)
 */
public class RouteManager {
    private Logger log = LoggerFactory.getLogger(getClass());

    private List<Route> routes = new ArrayList<Route>();
    private Map<String, Route> routesMap = new HashMap<String, Route>();

    public RouteManager(SiteContext site, List<Map<String, Object>> routeMaps) {
        if (!CollectionUtils.isEmpty(routeMaps)) {
            for (Map<String, Object> routeMap : routeMaps) {
                try {
                    String routePath = (String) routeMap.get("route");
                    Route route = new RouteWrapper(routePath, routeMap);
                    add(route);
                } catch (Exception e) {
                    log.error("cannot load the following router for: " + routeMaps);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * add a new Route
     *
     * @param route the route instance
     */
    public void add(Route route) {
        if (route != null) {
            routes.add(route);
            routesMap.put(route.getPath(), route);
        }
    }

    /**
     * method used by various admin tools
     *
     * @return a read only map containing all the route paths and their associated route instances
     */
    public Map<String, Route> getRoutesMap() {
        return Collections.unmodifiableMap(routesMap);
    }

    /**
     * assess and call one ore more {@link Route} instances that matches the request path parameter. The
     * execution flow can be interrupted if the Route implementation is requiring a full stop: aka context.halt()
     *
     * @param path    the request path
     * @param context the current micro context
     * @throws Exception if underlining Controllers or Views will throw any errors
     */
    @SuppressWarnings("unchecked")
    public void call(String path, MicroContext context) throws Exception {
        String requestedMethod = (String) context.getRackInput().get(Rack.REQUEST_METHOD);

        if (requestedMethod != null) {
            for (Route route : routes) {
                if (route.getMethod().isEmpty() || route.getMethod().contains(requestedMethod)) {

                    UriTemplateMatcher templateMatcher = route.match(path, route.getPath());

                    if (templateMatcher != null) {
                        MultivaluedMap<String, String> routeParams = templateMatcher.getVariables(true);
                        Map<String, Object> params = (Map<String, Object>) context.get(Globals.PARAMS);

                        if (CollectionUtils.isEmpty(params)) {
                            params = new HashMap<String, Object>();
                            context.with(Globals.PARAMS, params);
                        }

                        for (Map.Entry<String, List<String>> param : routeParams.entrySet()) {
                            Object paramValue = param.getValue() != null && param.getValue().size() == 1
                                    ? param.getValue().get(0) :
                                    param.getValue().toArray(new String[param.getValue().size()]);
                            params.put(param.getKey(), paramValue);
                        }

                        RackResponse response = route.call(context);
                        if (response != null) {
                            context.setRackResponse(response);
                        }

                        if (context.isHalt()) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
