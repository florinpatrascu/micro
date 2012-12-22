package ca.simplegames.micro.route;

import ca.simplegames.micro.Route;
import ca.simplegames.micro.SiteContext;
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

    private List<Route> routeControllers = new ArrayList<Route>();
    private Map<String, Route> routeControllersMap = new HashMap<String, Route>();
    private SiteContext site;

    public RouteManager(SiteContext site, Map<String, Object> config) {
        this.site = site;
    }

    public RouteManager addRouteController(Route route, Map<String, Object> config) throws Exception{

        return this;
    }

}
