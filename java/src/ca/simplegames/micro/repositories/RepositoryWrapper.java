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

package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.View;
import ca.simplegames.micro.controllers.ControllerManager;
import ca.simplegames.micro.utils.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 7:47 PM)
 */
public class RepositoryWrapper {
    private Repository repository;
    private MicroContext context;

    public RepositoryWrapper(Repository repository, MicroContext context) {
        this.repository = repository;
        this.context = context;
    }

    public String get(String path) {
        try {

            StringWriter writer = new StringWriter();
            View view = repository.getView(path);
            if (view != null && !CollectionUtils.isEmpty(view.getControllers())) {
                executeViewControllers(view.getControllers(), context);
            }
            repository.getRenderer().render(context, path, null, writer);
            return writer.toString();

        } catch (Exception e) {
            String err = e.getMessage();
            repository.getLog().error(err);
            return String.format("Repository::%s; %s",
                    repository.getName().toUpperCase(), e.getMessage());
        }
    }

    private void executeViewControllers(List<Map<String, Object>> controllers, MicroContext context) throws Exception {

        if (context != null && !CollectionUtils.isEmpty(controllers)) {
            ControllerManager controllerManager = context.getSiteContext().getControllerManager();
            for (Map<String, Object> map : controllers) {
                final Map controllerMap = (Map) map.get(Globals.CONTROLLER);
                String controllerName = (String) controllerMap.get(Globals.NAME);
                if (StringUtils.isNotBlank(controllerName)) {
                    context.getSiteContext().getControllerManager().execute(
                            controllerName, context, (Map) controllerMap.get(Globals.OPTIONS));
                }
            }
        }
    }
}
