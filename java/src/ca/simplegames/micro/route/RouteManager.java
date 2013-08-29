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
import ca.simplegames.micro.utils.Assert;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.MicroConfigFileMonitor;
import ca.simplegames.micro.utils.Reloadable;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * The Route manager is responsible for initializing various Routes at startup time, and for executing those
 * {@link Route} instances that are matching the request path at runtime
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 6:04 PM)
 */
public class RouteManager implements Reloadable {
  private Logger log = LoggerFactory.getLogger(getClass());
  private MicroConfigFileMonitor monitor;
  private SiteContext site;
  private File routesConfig;

  private List<Route> routes = new ArrayList<Route>();
  private Map<String, Route> routesMap = new HashMap<String, Route>();

  private void load(SiteContext site, List<Map<String, Object>> routeMaps) {
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
    } else {
      log.warn("Empty route config file, nothing to do.");
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void reload() throws Exception {
    routes = new ArrayList<Route>();
    routesMap = new HashMap<String, Route>();

    load(site, (List<Map<String, Object>>) new Yaml().load(new FileInputStream(routesConfig)));
  }

  @SuppressWarnings("unchecked")
  public RouteManager(SiteContext site, File routesConfig) throws Exception {
    Assert.notNull(site, "invalid initial state, you can't load the routes if the site object is null");
    Assert.notNull(routesConfig, "routes cannot be defined from a null config file");

    this.site = site;
    this.routesConfig = routesConfig;

    if (site.isDevelopment()) {
      int seconds = 2;

      log.info("The routes config file will be monitored every " + seconds + " seconds and reloaded if modified.");
      monitor = new MicroConfigFileMonitor(routesConfig, this, seconds);
      // todo: think about a future usage of the monitor field
    } else {
      load(site, (List<Map<String, Object>>) new Yaml().load(new FileInputStream(routesConfig)));
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

            // first matching route wins
            break;
          }
        }
      }
    }
  }
}
