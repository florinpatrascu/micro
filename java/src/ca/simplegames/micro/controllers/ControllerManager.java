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

import ca.simplegames.micro.*;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.cache.MicroCacheException;
import org.apache.bsf.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The ControllerManager - responsible for finding, caching, instantiating and executing Micro controllers
 * <p/>
 * Only the Scripting is cached and only if Micro runs in 'production' mode.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 12:53 PM)
 */
public class ControllerManager {
    public static final String EXECUTE_METHOD = "execute";
    private Logger log = LoggerFactory.getLogger(getClass());
    private SiteContext site;
    private MicroCache cachedScriptControllers;
    private Set<File> pathsToControllers = new HashSet<File>();

    public ControllerManager(SiteContext site, Map<String, Object> config) {
        this.site = site;
        if (site.isProduction()) {
            cachedScriptControllers = site.getCacheManager().getCacheWithDefault(
                    StringUtils.defaultString((String) config.get("cache"),
                            Globals.SCRIPT_CONTROLLERS_CACHE_NAME).trim());
        }
        pathsToControllers.add(new File(site.getWebInfPath(), "controllers"));
    }

    public void execute(String controllerName, MicroContext context) throws Exception {
        execute(controllerName, context, null);
    }

    public void execute(String controllerName, MicroContext context, Map configuration) throws ControllerException, ControllerNotFoundException {
        if (StringUtils.isBlank(controllerName)) {
            throw new ControllerNotFoundException("Invalid controller name: " + controllerName);
        }

        final String controllerNotFoundMessage = String.format("%s, not found!", controllerName);
        Controller controller = findController(controllerName);

        if (controller != null) {
            if (controller instanceof ScriptController) {
                try {
                    controller.execute(context, configuration);
                } catch (FileNotFoundException e) {
                    throw new ControllerException(controllerNotFoundMessage);
                }
            } else {
                try {
                    Class[] paramTypes = {MicroContext.class, Map.class};
                    Object[] params = {context, configuration};
                    Method method = controller.getClass().getDeclaredMethod(EXECUTE_METHOD, paramTypes);
                    // ... Object result =
                    method.invoke(controller, params);
                } catch (Exception e) {
                    if (e.getCause() instanceof RedirectException) {
                        throw new RedirectException(); // must be a better way :'(
                    } else {
                        log.error(String.format("%s, error: %s", controllerName, e.getMessage()));
                        e.printStackTrace();
                        throw new ControllerException(e.getMessage());
                    }
                }

            }        } else {
            throw new ControllerException(controllerNotFoundMessage);
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

    public Controller findController(String name) throws ControllerNotFoundException {
        // todo: think if we need to check if there are any Extensions providing controllers?

        try {
            return (Controller) ClassUtilities.loadClass(name).newInstance();
        } catch (Exception e) {
            // throw new ControllerNotFoundException(path, e);
            ScriptController scriptController = null;

            if (cachedScriptControllers != null) {
                try {
                    scriptController = (ScriptController) cachedScriptControllers.get(name);
                } catch (MicroCacheException ignored) {
                    throw new ControllerNotFoundException(name, e);
                }
            }

            if (scriptController == null) {

                File controllerFile = new File(name);
                if (!controllerFile.exists()) {
                    // maybe it exists in the app controllers?
                    for(File path: pathsToControllers){
                        controllerFile = new File(path, name);
                        if(controllerFile.exists()){
                            break;
                        }
                    }
                }


                if (controllerFile.exists()) {
                    try {
                        scriptController = new ScriptController(site, name,
                                IOUtils.getStringFromReader(new FileReader(controllerFile)));

                        if (cachedScriptControllers != null) {
                            cachedScriptControllers.put(name, scriptController);
                        }
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


    public void addPathToControllers(File path) {
        if (path != null && path.exists()) {
            pathsToControllers.add(path);
        }
    }
}