package ca.simplegames.micro;

import ca.simplegames.micro.cache.MicroCacheManager;
import ca.simplegames.micro.controllers.ControllerManager;
import org.jrack.Context;
import org.jrack.context.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains configuration information for a particular Micro
 * site.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 11:13 AM)
 */
public class SiteContext extends MapContext {
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<Helper> helpers = new ArrayList<Helper>();
    private MicroCacheManager cacheManager;
    private ControllerManager controllerManager;

    public SiteContext(Context<String> env) {
        for (Map.Entry<String, Object> entry : env) {
            with(entry.getKey(), entry.getValue());
        }
    }

    public ServletContext getServletContext() {
        return (ServletContext) map.get(Globals.SERVLET_CONTEXT);
    }

    /**
     * load the application config and execute the application script if present
     *
     * @param configPath the absolute path to "WEB-INF/config/micro-config.yml"
     * @return itself
     */
    public SiteContext loadApplication(String configPath) throws Exception {
        File config = new File(configPath, "micro-config.yml");

        if (config.exists()) {
            with(Globals.MICRO_CONFIG_PATH, config);

            try {
                Map appConfig = (Map) new Yaml().load(new FileInputStream(config));
                log.info(appConfig.toString());
                with(Globals.MICRO_CACHE_CONFIG, appConfig.get("cache"));
                cacheManager = new MicroCacheManager(this);
                controllerManager = new ControllerManager(this);
                controllerManager.execute(findApplicationScript(configPath), map);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    private String findApplicationScript(String configPath) {
        String actionPath = Globals.EMPTY_STRING;

        for (String ext : new String[]{"bsh", "rb", "js"}) {
            File applicationScript = new File(configPath, "application." + ext);

            if (applicationScript.exists()) {
                actionPath = applicationScript.getAbsolutePath();
                break;
            }
        }

        return actionPath;
    }

    public List<Helper> getHelpers() {
        return helpers;
    }

    public Logger getLog() {
        return log;
    }

    public MicroCacheManager getCacheManager() {
        return cacheManager;
    }

    public ControllerManager getControllerManager() {
        return controllerManager;
    }
}
