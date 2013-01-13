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
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerManager;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;
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

    @SuppressWarnings("unchecked")
    public String get(String templateEngineName, String path) throws ControllerNotFoundException, ControllerException,
            FileNotFoundException, ViewException {
        return get(templateEngineName, path, null);
    }

    @SuppressWarnings("unchecked")
    public String get(String templateEngineName, String path, Map<String, Object> options) throws ControllerNotFoundException, ControllerException,
            FileNotFoundException, ViewException {

        StringWriter writer = new StringWriter();
        View view = repository.getView(path);
        if (view != null && !CollectionUtils.isEmpty(view.getControllers())) {
            executeViewControllers(view.getControllers(), context);
        }

        ViewRenderer engine = context.getSiteContext().getTemplateEnginesManager().getEngine(templateEngineName);

        if (options != null && !options.isEmpty()) {
            context.getMap().putAll(options);
        }

        try {
            engine.render(path, repository, context, writer);
        } catch (Exception e) {
            throw new FileNotFoundException(String.format("%s not found.", path));
        }
        return writer.toString();
    }

    public String get(String path) throws ControllerNotFoundException, ControllerException, FileNotFoundException, ViewException {
        StringWriter writer = new StringWriter();
        View view = repository.getView(path);
        if (view != null && !CollectionUtils.isEmpty(view.getControllers())) {
            executeViewControllers(view.getControllers(), context);
        }
        repository.getRenderer().render(path, repository, context, writer);
        return writer.toString();
    }

    private void executeViewControllers(List<Map<String, Object>> controllers, MicroContext context)
            throws ControllerException, ControllerNotFoundException {

        if (context != null && !CollectionUtils.isEmpty(controllers)) {
            ControllerManager controllerManager = context.getSiteContext().getControllerManager();
            for (Map<String, Object> map : controllers) {
                final Map controllerMap = (Map) map.get(Globals.CONTROLLER);
                String controllerName = (String) controllerMap.get(Globals.NAME);
                if (StringUtils.isNotBlank(controllerName)) {
                    controllerManager.execute(controllerName, context, (Map) controllerMap.get(Globals.OPTIONS));
                }
            }
        }
    }
}
