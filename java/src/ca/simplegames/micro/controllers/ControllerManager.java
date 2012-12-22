package ca.simplegames.micro.controllers;

import ca.simplegames.micro.*;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.cache.MicroCacheException;
import ca.simplegames.micro.cache.MicroCacheManager;
import ca.simplegames.micro.repositories.Repository;
import org.apache.bsf.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 12:53 PM)
 */
public class ControllerManager {
    private Logger log = LoggerFactory.getLogger(getClass());
    private SiteContext site;
    private MicroCache cachedScriptControllers;

    public ControllerManager(SiteContext site) {
        this.site = site;
        cachedScriptControllers = site.getCacheManager()
                .getCacheWithDefault(Globals.SCRIPT_CONTROLLERS_CACHE_NAME);
    }

    public Object execute(String actionName, MicroContext context) throws Exception {
        return execute(actionName, context, null);
    }

    public Object execute(String controllerName, MicroContext context, Map configuration) throws Exception {
        if(StringUtils.isBlank(controllerName)){
            throw new ControllerNotFoundException("Invalid controller name: "+controllerName);
        }

        Controller controller = findController(controllerName);
        Object result = null;

        if (controller != null) {
            ScriptController scriptController = (ScriptController) cachedScriptControllers.get(controllerName);

            if(scriptController!= null){
                 result =  scriptController.execute(context, configuration);
            }else{
                Class[] paramTypes = {Map.class, Map.class};
                Object[] params = {context, configuration};
                Method execute = controller.getClass().getMethod("execute", paramTypes);
                result =  execute.invoke(params);
            }
        }

        return result;
    }

    /**
     * Find an action with the given name.  The name may be the name of an
     * action registered with the ActionManager at startup, an action from a
     * helper, a partial file path rooted in the action root directory or a
     * fully qualified Java class.
     *
     * @param path the name of the action, can be absolute path to a scripting file or a
     *             class reference
     * @return the controller object.
     * @throws ControllerNotFoundException if the controller is not found
     */

    public Controller findController(String path) throws Exception {
        List<Helper> helpers = site.getHelpers();

        // Checking if the controller is available via any helpers
        if (helpers != null && !helpers.isEmpty()) {
            for (Helper helper : helpers) {
                if (helper.getControllers()!= null && helper.getControllers().containsKey(path)) {
                    return helper.getControllers().get(path);
                }
            }
        }


        try {
            return (Controller) ClassUtilities.loadClass(path).newInstance();
        } catch (Exception e) {
            // throw new ControllerNotFoundException(path, e);
            ScriptController scriptController = null;
            try {
                scriptController = (ScriptController) cachedScriptControllers.get(path);
            } catch (MicroCacheException ignored) {
                throw new Exception(path, e);
            }

            if(scriptController == null){
                File controllerFile = new File(path);
                if(controllerFile.exists()){
                    try {
                        scriptController = new ScriptController(site,path,
                                IOUtils.getStringFromReader(new FileReader(controllerFile)));

                        cachedScriptControllers.put(path, scriptController);
                        return scriptController;

                    } catch (Exception ex) {
                        throw new ControllerNotFoundException(path, ex);
                    }
                }else{
                    throw new ControllerNotFoundException(path, e);
                }
            }else{
                return scriptController;
            }
        }
    }

    public void executeForPath(Repository repository, String path, MicroContext context) {
        log.info(String.format("for repository: %s, and path: %s", StringUtils.defaultString(repository.getName()), path));
        log.warn("todo: implement me: ca.simplegames.micro.controllers.ControllerManager#executeForPath");
    }
}
