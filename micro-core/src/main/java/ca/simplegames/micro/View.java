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

package ca.simplegames.micro;

import ca.simplegames.micro.utils.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-24 12:09 PM)
 */
@SuppressWarnings("unchecked")
public class View implements Serializable {
    private Map<String, Object> config;
    private String repositoryName = null;
    private String template = null;
    private String path = null;
    private List<Map<String, Object>> controllers = null;
    private List<Map<String, Object>> filtersBefore = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> filtersAfter = new ArrayList<Map<String, Object>>();

    public View(Map<String, Object> config) {
        if (!CollectionUtils.isEmpty(config)) {
            this.config = (Map<String, Object>) config.get(Globals.OPTIONS);

            if (config.get(Globals.REPOSITORY) != null) {
                repositoryName = (String) config.get(Globals.REPOSITORY);
            }
            if (config.get(Globals.TEMPLATE) != null) {
                template = (String) config.get(Globals.TEMPLATE);
            }
            if (config.get(Globals.PATH) != null) {
                path = (String) config.get(Globals.PATH);
            }
            if (config.get(Globals.CONTROLLERS) != null) {
                controllers = (List<Map<String, Object>>) config.get(Globals.CONTROLLERS);
            }
            if (config.get(Globals.CONTROLLER) != null) {
                controllers = Collections.singletonList((Map<String, Object>) config.get(Globals.CONTROLLER));
            }

            List<Map> viewFilters = (List<Map>) config.get(Globals.FILTERS);
            if (viewFilters != null && !viewFilters.isEmpty()) {
                // define the BEFORE and AFTER filters
                for(Map filterDef : viewFilters){
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) filterDef.entrySet().iterator().next();
                    boolean isBefore = entry.getKey().equalsIgnoreCase("before");
                    Map<String, Object> controller = (Map<String, Object>) entry.getValue();
                    String controllerName = (String) controller.get("controller");
                    Map<String, Object> controllerOptions = (Map<String, Object>) controller.get("options");

                    List<Map<String, Object>> controllers = isBefore? filtersBefore: filtersAfter;
                    controllers.add(Collections.<String, Object>singletonMap(controllerName, controllerOptions));
                }
            }
        }
    }

    public String getTemplate() {
        return template;
    }

    public List<Map<String, Object>> getControllers() {
        return controllers;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getPath() {
        return path;
    }

    public List<Map<String, Object>> getFiltersBefore() {
        return filtersBefore;
    }

    public List<Map<String, Object>> getFiltersAfter() {
        return filtersAfter;
    }
}
