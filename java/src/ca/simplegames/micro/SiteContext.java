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
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.route.RouteManager;
import ca.simplegames.micro.templates.TemplateEnginesManager;
import ca.simplegames.micro.utils.StringUtils;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;
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
import java.io.FilenameFilter;
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
    private MicroCacheManager cacheManager;
    private RepositoryManager repositoryManager;
    private ControllerManager controllerManager;
    private HelperManager helperManager;
    private Map appConfig;
    private File webInfPath;
    private RouteManager routeManager;
    private List<Extension> extensions = new ArrayList<Extension>();
    private String microEnv;
    private TemplateEnginesManager templateEnginesManager;

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
    @SuppressWarnings("unchecked")
    public SiteContext loadApplication(String configPath) throws Exception {
        File config = new File(configPath, "micro-config.yml");
        webInfPath = (File) get(Globals.WEB_INF_PATH);

        if (config.exists()) {
            with(Globals.MICRO_CONFIG_PATH, config);

            try {
                appConfig = (Map) new Yaml().load(new FileInputStream(config));
                with(Globals.MICRO_CACHE_CONFIG, appConfig.get("cache"));
                microEnv = StringUtils.defaultString(appConfig.get(Globals.MICRO_ENV), Globals.DEVELOPMENT);

                // - Cache
                cacheManager = new MicroCacheManager(this);

                log.info(String.format("Application name: %s", StringUtils.defaultString(appConfig.get("name"), "")));
                log.info(String.format("     description: %s", StringUtils.defaultString(appConfig.get("description"), "")));

                log.info("Template engines:");
                templateEnginesManager = new TemplateEnginesManager( this, appConfig);

                log.info("Repositories:");
                // - Repositories
                repositoryManager = new RepositoryManager(this);

                // - Controllers
                controllerManager = new ControllerManager(this, (Map<String, Object>) appConfig.get("controllers"));

                // - Helpers
                // log.info("Helpers:");
                File helpersConfig = new File(configPath, "helpers.yml");
                if (helpersConfig.exists()) {
                    helperManager = new HelperManager(this,
                            (List<Map<String, Object>>) new Yaml().load(new FileInputStream(helpersConfig)));
                }

                // - Extensions
                log.info("Extensions:");
                File extensionsDirectory = new File(configPath, "extensions");
                if (extensionsDirectory.exists() && extensionsDirectory.isDirectory()) {
                    // load extensions
                    for (File file : files(extensionsDirectory, ".yml")) {
                        Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(new FileInputStream(file));
                        Extension extension = (Extension) ClassUtilities.loadClass((String) yaml.get("class")).newInstance();
                        final String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                        extensions.add(extension.register(fileName, this, yaml));
                        log.info(String.format("  %s: %s", extension.getName(), file.getAbsolutePath()));
                    }
                }

                // - Routes
                File routesConfig = new File(configPath, "routes.yml");
                if (routesConfig.exists()) {
                    routeManager = new RouteManager(this,
                            (List<Map<String, Object>>) new Yaml().load(new FileInputStream(routesConfig)));
                }


                MicroContext context = new MicroContext();
                context.with(Globals.MICRO_SITE, this)
                        .with(Globals.WEB_APP_NAME, StringUtils.defaultString(appConfig.get("name"), "<name your app>"))
                        .with(Globals.WEB_APP_DESCRIPTION, StringUtils.defaultString(appConfig.get("description"),
                                "<describe your app>"));

                //add anything else to the context? If no, then execute the app' startup controller:
                controllerManager.execute(findApplicationScript(configPath), context);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        log.info(String.format("running in: '%s' mode", microEnv));
        return this;
    }

    public File[] files(File dir, final String withExtension) {
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(withExtension);
            }
        });
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

    /**
     * @return one of these running modes: development, production or test
     */
    public String getMicroEnv() {
        return StringUtils.defaultString(microEnv, Globals.DEVELOPMENT).trim();
    }

    // true if Micro runs in development mode
    public boolean isDevelopment() {
        return getMicroEnv().equalsIgnoreCase(Globals.DEVELOPMENT);
    }

    // true if Micro runs in production mode
    public boolean isProduction() {
        return getMicroEnv().equalsIgnoreCase(Globals.PRODUCTION);
    }

    // true if Micro runs in test mode
    public boolean isTest() {
        return getMicroEnv().equalsIgnoreCase(Globals.TEST);
    }

    /**
     * a simple method that can be used as an ad-hoc scripting engine.
     * <p/>
     * Example:
     * engine = site.getBSFEngine("beanshell", context, Collections.singletonMap("foo", "bar"));
     * engine.exec("complexCalculus", 0, 0, "one = 1 * 1;"); // :P
     *
     * @param language      a valid BSF language, example: 'beanshell'
     * @param context       a MicroContext that can be used to transmit parameters
     * @param configuration an optional Map containing configuration elements
     * @param log           a logger that can be used by the client code
     * @return a new BSF Engine
     * @throws Exception if the Engine cannot be created
     */
    public BSFEngine getBSFEngine(String language, MicroContext context, Map configuration, Logger log) throws Exception {
        BSFManager bsfManager = new BSFManager();
        bsfManager.setClassLoader(this.getClass().getClassLoader());

        bsfManager.declareBean("configuration", configuration, Map.class);
        bsfManager.declareBean("context", context, MicroContext.class);
        bsfManager.declareBean("site", this, SiteContext.class);
        bsfManager.declareBean("log", log, Logger.class);

        // pre-load the engine to make sure we were called right
        org.apache.bsf.BSFEngine bsfEngine = null;
        bsfEngine = bsfManager.loadScriptingEngine(language);
        return bsfEngine;
    }

    /**
     * a simple method that can be used as an ad-hoc scripting engine. This varian is using the logger
     * provided by the site.
     *
     * @param language      a valid BSF language, example: 'beanshell'
     * @param context       a MicroContext that can be used to transmit parameters
     * @param configuration an optional Map containing configuration elements
     * @return a new BSF Engine
     * @throws Exception if the Engine cannot be created
     */
    public BSFEngine getBSFEngine(String language, MicroContext context, Map configuration) throws Exception {
        return getBSFEngine(language, context, configuration, log);
    }

    public TemplateEnginesManager getTemplateEnginesManager() {
        return templateEnginesManager;
    }
}
