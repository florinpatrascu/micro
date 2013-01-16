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
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * This is a generic helper that can be used to create a helper instance from a simple Filter definition
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 9:41 PM)
 */
public class GenericFilter implements Filter {
    private String path;
    private String name;
    private String description;
    private String version;
    private String controllerName;
    private Boolean before;
    private Boolean after;
    private Map<String, Object> config;

    @SuppressWarnings("unchecked")
    public Filter init(SiteContext site, Map<String, Object> config, String type) throws Exception {
        if (type != null) {
            before = type.equalsIgnoreCase(FilterManager.BEFORE);
            after = type.equalsIgnoreCase(FilterManager.AFTER);
        }

        path = (String) config.get("path");
        name = StringUtils.defaultString((String) config.get("name"), Globals.EMPTY_STRING);
        description = (String) config.get("description");
        version = (String) config.get("version");
        controllerName = (String) config.get("controller");
        this.config = (Map<String, Object>) config.get("options");
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public boolean isBefore() {
        return before != null && before;
    }

    public boolean isAfter() {
        return after != null && after;
    }

    public String getPath() {
        return path;
    }

    public String getController() {
        return controllerName;
    }

    public void call(MicroContext context) throws Exception {
        if (context != null && controllerName != null) {
            context.getSiteContext().getControllerManager().execute(controllerName, context, config);
        }
    }
}
