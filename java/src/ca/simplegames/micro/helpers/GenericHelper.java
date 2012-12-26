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

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.repositories.Repository;

import java.util.Map;

/**
 * This is a generic helper that can be used to create a helper instance from a simple Helper definition
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 9:41 PM)
 */
public class GenericHelper implements Helper {
    private String path;
    private String name;
    private String description;
    private String version;
    private Repository repository;
    private String controllerName;
    private Boolean before;
    private Map<String, Object> config;

    public Helper init(SiteContext site, Map<String, Object> config, String type) throws Exception {
        if (type != null) {
            before = type.equalsIgnoreCase(HelperManager.BEFORE);
        }

        path = (String) config.get("path");
        name = (String) config.get("name");
        description = (String) config.get("description");
        version = (String) config.get("version");
        controllerName = (String) config.get("controller");
        repository = site.getRepositoryManager().getRepository((String) config.get("repository"));

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

    public String getRepositoryName() {
        return repository == null ? Globals.EMPTY_STRING : repository.getName();
    }

    public boolean isBefore() {
        return before != null && before;
    }

    public boolean isAfter() {
        return before != null && !before;
    }

    public String getPath() {
        return path;
    }

    public String getController() {
        return controllerName;
    }

    public Object call(MicroContext context) throws Exception {
        if (context != null && controllerName != null) {
            return context.getSiteContext().getControllerManager().execute(controllerName, context, config);
        }
        return null;
    }
}
