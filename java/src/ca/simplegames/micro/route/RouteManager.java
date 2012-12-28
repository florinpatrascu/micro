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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            //load helpers from config
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

    private void add(Route route) {
        if(route!=null){
            routes.add(route);
            routesMap.put(route.getPath(), route);
        }
    }

    public void call(String path, MicroContext context) throws Exception {
        for (Route route : routes) {
            UriTemplateMatcher templateMatcher = PathUtilities.routeMatch(path, route.getPath());
            if (templateMatcher != null) {
                try {
                    context.with(Globals.PARAMETERS, templateMatcher.getVariables(true));
                } catch (IllegalStateException e) {
                    log.error(e.getMessage()); //todo: improve the error message
                }
                route.call(context);
                if(context.isHalt()){
                    break;
                }
            }
        }
    }
}
