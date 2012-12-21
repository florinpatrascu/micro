package ca.simplegames.micro;

import ca.simplegames.micro.cache.MicroCacheManager;
import ca.simplegames.micro.controllers.ControllerManager;
import ca.simplegames.micro.helpers.HelperManager;
import ca.simplegames.micro.helpers.i18n.I18NHelper;
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.jrack.Context;
import org.jrack.context.MapContext;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private MicroCacheManager cacheManager;
    private RepositoryManager repositoryManager;
    private ControllerManager controllerManager;
    private HelperManager helperManager;
    private ViewRenderer renderer;
    private Map appConfig;
    private File webInfPath;

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
        webInfPath = (File) get(Globals.WEB_INF_PATH);

        if (config.exists()) {
            with(Globals.MICRO_CONFIG_PATH, config);

            try {
                appConfig = (Map) new Yaml().load(new FileInputStream(config));
                log.info(appConfig.toString());
                with(Globals.MICRO_CACHE_CONFIG, appConfig.get("cache"));
                cacheManager = new MicroCacheManager(this);
                controllerManager = new ControllerManager(this);
                helperManager = new HelperManager(this);

                /**
                 * load the default i18N support, a default Helper
                 */

                Map<String, Object> localesModel = (Map<String, Object>) appConfig.get("locales");
                if (localesModel != null) {
                    Helper i18N = new I18NHelper();
                    helperManager.addHelper(i18N.init(this, localesModel));
                }

                MicroContext context = new MicroContext();
                context.with(Globals.MICRO_SITE, this);
                //add anything else to the context?

                repositoryManager = new RepositoryManager(this);

                controllerManager.execute(findApplicationScript(configPath), context);

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
        return helperManager.getHelpers();
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

    public Map getAppConfig() {
        return appConfig;
    }

    public HelperManager getHelperManager() {
        return helperManager;
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public File getWebInfPath() {
        return webInfPath != null ? webInfPath : new File(Globals.EMPTY_STRING);
    }
}
