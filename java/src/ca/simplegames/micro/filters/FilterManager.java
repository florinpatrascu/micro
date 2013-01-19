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
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Filter manager
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:33 PM)
 */
public class FilterManager {
    public static final String BEFORE = "before";
    public static final String AFTER = "after";

    private List<Filter> beforeFilters = new ArrayList<Filter>();
    private List<Filter> afterFilters = new ArrayList<Filter>();

    public FilterManager() {
    }

    @SuppressWarnings("unchecked")
    public FilterManager(SiteContext site, List<Map<String, Object>> config) {
        if (!CollectionUtils.isEmpty(config)) {
            //load filters from config
            for (Map<String, Object> filterConfig : config) {
                for (Map.Entry<String, Object> entry : filterConfig.entrySet()) {
                    try {
                        String filterType = entry.getKey();
                        Map<String, Object> filterDefinition = (Map<String, Object>) entry.getValue();
                        addFilter(createFilterFromModel(filterDefinition, filterType));
                    } catch (Exception e) {
                        site.getLog().error("cannot load the following filter: " + filterConfig);
                        e.printStackTrace();
                    }
                }
            }
        }
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

    public Filter createFilterFromModel(Map<String, Object> model, String type) throws Exception {
        Filter filter = null;
        if (!CollectionUtils.isEmpty(model)) {
            filter = new FilterWrapper(model, type);
        }
        return filter;
    }

    public List<Filter> getBeforeFilters() {
        return beforeFilters;
    }

    public List<Filter> getAfterFilters() {
        return afterFilters;
    }
}
