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

package ca.simplegames.micro;

import ca.simplegames.micro.cache.MicroCacheManager;
import ca.simplegames.micro.controllers.ControllerManager;
import ca.simplegames.micro.helpers.HelperManager;
import ca.simplegames.micro.helpers.i18n.I18NHelper;
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.route.RouteManager;
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
import java.util.Collections;
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
    private Map appConfig;
    private File webInfPath;
    private RouteManager routeManager;

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
                with(Globals.MICRO_CACHE_CONFIG, appConfig.get("cache"));
                cacheManager = new MicroCacheManager(this);

                repositoryManager = new RepositoryManager(this);

                File routesConfig = new File(configPath, "routes.yml");
                if (routesConfig.exists()) {
                    routeManager = new RouteManager(this, (List<Map<String, Object>>)
                            new Yaml().load(new FileInputStream(routesConfig)));
                }

                controllerManager = new ControllerManager(this);
                helperManager = new HelperManager(this, appConfig.get(Globals.MICRO_HELPERS_CONFIG));

                /**
                 * load the default i18N support, a default Helper
                 */
                Map<String, Object> localesModel = (Map<String, Object>) appConfig.get("locales");
                if (localesModel != null && localesModel.containsKey("proprietary")) {
                    // temporary hacks
                    Helper i18N = new I18NHelper();
                    helperManager.addHelper(i18N.init(this, localesModel, HelperManager.BEFORE));
                    // now make sure the i18N filter is always first
                    Collections.swap(helperManager.getBeforeHelpers(), 0, helperManager.getBeforeHelpers().size()-1);
                    //helperManager.getHelpersMap().put(i18N.getName(), i18N);
                }

                MicroContext context = new MicroContext();
                context.with(Globals.MICRO_SITE, this);
                //add anything else to the context?

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

    public RouteManager getRouteManager() {
        return routeManager;
    }
}
