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
