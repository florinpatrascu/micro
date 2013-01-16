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

package ca.simplegames.micro.filters;

import ca.simplegames.micro.Filter;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:33 PM)
 */
public class FilterManager {
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final String BEFORE = "before";
    public static final String AFTER = "after";

    private SiteContext site;
    private List<Filter> beforeFilters = new ArrayList<Filter>();
    private List<Filter> afterFilters = new ArrayList<Filter>();

    @SuppressWarnings("unchecked")
    public FilterManager(SiteContext site, List<Map<String, Object>> config) {
        this.site = site;
        if (!CollectionUtils.isEmpty(config)) {
            //load filters from config
            for (Map<String, Object> helperConfig : config) {
                for (Map.Entry<String, Object> entry : helperConfig.entrySet()) {
                    try {
                        String helperType = entry.getKey();
                        Map<String, Object> helperDefinition = (Map<String, Object>) entry.getValue();
                        Filter filter = createHelperFromModel(helperDefinition, helperType);
                        addFilter(filter);
                    } catch (Exception e) {
                        log.error("cannot load the following helper: " + helperConfig);
                        e.printStackTrace();
                    }

                }
            }

        }
    }

    public Filter createHelperFromModel(Map<String, Object> model, String type) throws Exception {
        Filter filter = null;
        if (!CollectionUtils.isEmpty(model)) {
            filter = new GenericFilter();
            //filter.init(site, model, StringUtils.defaultString(type, Globals.EMPTY_STRING).trim());
        }
        return filter;
    }

    public Filter addFilter(Filter filter) {
        if (filter != null) {
            if (filter.isBefore()) {
                beforeFilters.add(filter);
            } else if (filter.isAfter()) {
                afterFilters.add(filter);
            }
        }
        return filter;
    }

    public List<Filter> getBeforeFilters() {
        return beforeFilters;
    }

    public List<Filter> getAfterFilters() {
        return afterFilters;
    }

    public List<Filter> getPathFilters(String inputPath, MicroContext context) {
        List<Filter> matchingFilters = new ArrayList<Filter>();
        // todo: implement the getPathFilters method
        return matchingFilters;
    }
}
