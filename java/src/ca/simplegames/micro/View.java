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

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-24 12:09 PM)
 */
public class View {
    private Map<String, Object> config;
    private String template = null;
    private List<Map<String, Object>> controllers = null;

    public View(Map<String, Object> config) {
        if (!CollectionUtils.isEmpty(config)) {
            this.config = config;
            if (config.get(Globals.TEMPLATE) != null) {
                template = (String) config.get(Globals.TEMPLATE);
            }
            if (config.get(Globals.CONTROLLERS) != null) {
                controllers = (List<Map<String, Object>>) config.get(Globals.CONTROLLERS);
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
}
