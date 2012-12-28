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

package ca.simplegames.micro.controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.cache.MicroCacheException;
import org.apache.bsf.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 12:53 PM)
 */
public class ControllerManager {
    public static final String EXECUTE_METHOD = "execute";
    private Logger log = LoggerFactory.getLogger(getClass());
    private SiteContext site;
    private MicroCache cachedScriptControllers;
    private File pathToAppControllers;

    public ControllerManager(SiteContext site) {
        this.site = site;
        cachedScriptControllers = site.getCacheManager()
                .getCacheWithDefault(Globals.SCRIPT_CONTROLLERS_CACHE_NAME);
        pathToAppControllers = new File(site.getWebInfPath(), "controllers");
    }

    public void execute(String controllerName, MicroContext context) throws Exception {
        execute(controllerName, context, null);
    }

    public void execute(String controllerName, MicroContext context, Map configuration) throws Exception {
        if (StringUtils.isBlank(controllerName)) {
            throw new ControllerNotFoundException("Invalid controller name: " + controllerName);
        }

        Controller controller = findController(controllerName);
        Object result = null;

        if (controller != null) {
            ScriptController scriptController = (ScriptController) cachedScriptControllers.get(controllerName);

            if (scriptController != null) {
                scriptController.execute(context, configuration);
            } else {
                Class[] paramTypes = {MicroContext.class, Map.class};
                Object[] params = {context, configuration};
                Method method = controller.getClass().getDeclaredMethod(EXECUTE_METHOD, paramTypes);
                result = method.invoke(controller, params);
            }
        }
    }

    /**
     * Find a controller with the given name.  The name may be the name of an
     * action registered with the ControllerManager at startup, a file path rooted in the
     * controller's root directory or a fully qualified Java class.
     *
     * @param name the name of the controller, can be am absolute path to a
     *             scripting file or a class reference
     * @return the controller object.
     * @throws ControllerNotFoundException if the controller is not found
     */

    public Controller findController(String name) throws Exception {
        // todo: think if we need to check if there are any Extensions providing controllers?

        try {
            return (Controller) ClassUtilities.loadClass(name).newInstance();
        } catch (Exception e) {
            // throw new ControllerNotFoundException(path, e);
            ScriptController scriptController;
            try {
                scriptController = (ScriptController) cachedScriptControllers.get(name);
            } catch (MicroCacheException ignored) {
                throw new Exception(name, e);
            }

            if (scriptController == null) {
                File controllerFile = new File(name);
                if (!controllerFile.exists()) {
                    // maybe it exists in the app controllers?
                    controllerFile = new File(getPathToAppControllers(), name);
                }

                if (controllerFile.exists()) {
                    try {
                        scriptController = new ScriptController(site, name,
                                IOUtils.getStringFromReader(new FileReader(controllerFile)));

                        cachedScriptControllers.put(name, scriptController);
                        return scriptController;

                    } catch (Exception ex) {
                        throw new ControllerNotFoundException(name, ex);
                    }
                } else {
                    throw new ControllerNotFoundException(name, e);
                }
            } else {
                return scriptController;
            }
        }
    }

    public File getPathToAppControllers() {
        return pathToAppControllers;
    }
}
