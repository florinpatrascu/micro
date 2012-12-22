package ca.simplegames.micro.route.stats;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.Route;
import org.jrack.RackResponse;

import java.util.Map;

/**
 * Provides a simple system info
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 5:33 PM)
 */
public class StatsRoute extends Route {
    /**
     * Constructor
     *
     * @param path   The route path which is used for matching. (e.g. /hello, users/{name})
     * @param config a map containing nodes in a configuration loaded from an external support,
     *               an .yml file for example?!
     */
    protected StatsRoute(String path, Map<String, Object> config) {
        super(path, config);
    }

    @Override
    public RackResponse call(MicroContext context) {
        return null;
    }
}
