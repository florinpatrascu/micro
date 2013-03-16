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
import ca.simplegames.micro.utils.PathUtilities;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 6:04 PM)
 */
public class RouteManager {
    private Logger log = LoggerFactory.getLogger(getClass());

    private List<Route> routes = new ArrayList<Route>();
    private Map<String, Route> routesMap = new HashMap<String, Route>();
    //private Map<Uri, Route> compiledRoutesMap = new HashMap<String, Route>();
    private SiteContext site;

    public RouteManager(SiteContext site, List<Map<String, Object>> routeMaps) {
        this.site = site;
        if (!CollectionUtils.isEmpty(routeMaps)) {
            //load filters from config
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

    public void add(Route route) {
        if (route != null) {
            routes.add(route);
            routesMap.put(route.getPath(), route);
        }
    }

    @SuppressWarnings("unchecked")
    public void call(String path, MicroContext context) throws Exception {
        String requestedMethod = (String) context.getRackInput().get(Rack.REQUEST_METHOD);

        if (requestedMethod != null) {
            for (Route route : routes) {
                if (route.getMethod().isEmpty() || route.getMethod().contains(requestedMethod)) {

                    // todo: Add support for reusing compiled templates
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
