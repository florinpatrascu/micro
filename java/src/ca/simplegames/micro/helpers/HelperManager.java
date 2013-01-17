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

package ca.simplegames.micro.helpers;

import ca.simplegames.micro.Helper;
import ca.simplegames.micro.SiteContext;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager responsible with the Helper registration
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:33 PM)
 */
public class HelperManager {
    private static Logger log = LoggerFactory.getLogger(Helper.class);
    private List<Helper> helpers = new ArrayList<Helper>();
    private Map<String, Helper> helpersMap = new HashMap<String, Helper>();
    private SiteContext site;

    /**
     * Create the helper manager
     *
     * @param site the SiteContext
     */
    public HelperManager(SiteContext site) {
        this.site = site;
    }

    /**
     * @return a list with all the Helpers
     */
    public List<Helper> getHelpers() {
        return helpers;
    }

    /**
     * instantiate and add a new Helper
     *
     * @param name   helper's name
     * @param config the helper configuration
     * @return a new Helper object
     * @throws Exception
     */
    public Helper addHelper(String name, Map<String, Object> config) throws Exception {
        Helper helper = (Helper) ClassUtilities.loadClass((String) config.get("class")).newInstance();
        helpers.add(helper.register(name, site, config));
        helpersMap.put(helper.getName(), helper);
        return helper;
    }

    /**
     * find a Helper by name
     *
     * @param name the name of the helper, case sensitive
     * @return an existing Helper or null if the helper doesn't exist
     */
    public Helper findHelper(String name) {
        return helpersMap.get(name);
    }

}
