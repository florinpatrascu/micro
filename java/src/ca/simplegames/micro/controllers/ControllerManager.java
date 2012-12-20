package ca.simplegames.micro.controllers;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.cache.MicroCacheException;
import ca.simplegames.micro.cache.MicroCacheManager;
import org.apache.bsf.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jrack.utils.ClassUtilities;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 12:53 PM)
 */
public class ControllerManager {

    private SiteContext site;
    private MicroCache cachedScriptControllers;

    public ControllerManager(SiteContext site) {
        this.site = site;
        cachedScriptControllers = site.getCacheManager()
                .getCacheWithDefault(Globals.SCRIPT_CONTROLLERS_CACHE_NAME);
    }

    public Object execute(String actionName, Map context) throws Exception {
        return execute(actionName, context, null);
    }

    public Object execute(String controllerName, Map context, Map configuration) throws Exception {
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

    public Controller findController(String path) throws ControllerNotFoundException {
        List<Helper> helpers = site.getHelpers();

        // Checking if the controller is available via any helpers
        if (helpers != null && !helpers.isEmpty()) {
            for (Helper helper : helpers) {
                if (helper.getControllers().containsKey(path)) {
                    return helper.getControllers().get(path);
                }
            }
        }

        ScriptController scriptController = null;
        try {
            scriptController = (ScriptController) cachedScriptControllers.get(path);
        } catch (MicroCacheException ignored) {
        }

        if(scriptController == null){
            File controllerFile = new File(path);
            if(controllerFile.exists()){
                try {
                    scriptController = new ScriptController(site,path,
                            IOUtils.getStringFromReader(new FileReader(controllerFile)));

                    cachedScriptControllers.put(path, scriptController);

                } catch (IOException e) {
                    throw new ControllerNotFoundException(path, e);
                } catch (MicroCacheException e) {
                    throw new ControllerNotFoundException(path, e);
                }
            }

            if(scriptController!= null){
                return scriptController;
            }
        }else{
            return scriptController;
        }

        try {
            return (Controller) ClassUtilities.loadClass(path).newInstance();
        } catch (Exception e) {
            throw new ControllerNotFoundException(path, e);
        }
    }
}
