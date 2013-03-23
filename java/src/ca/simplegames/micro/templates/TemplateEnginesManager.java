package ca.simplegames.micro.templates;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and helps finding instances of various Template engines
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-01-09 8:46 AM)
 */
public class TemplateEnginesManager {
    Map<String, ViewRenderer> engines = new HashMap<String, ViewRenderer>();
    private ViewRenderer defaultEngine = null;

    /**
     * Template manager responsible with loading all the required rendering engines
     *
     * @param site   a valid SiteContext instance
     * @param config a valid Micro configuration object
     * @throws Exception if there are any errors encountered
     */
    @SuppressWarnings("unchecked")
    public TemplateEnginesManager(SiteContext site, Map<String, Object> config) throws Exception {
        // Initialize a repository specific View renderer

        List<Map<String, Object>> templateEngines = (List<Map<String, Object>>) site.getAppConfig().get("template_engines");
        if (templateEngines != null) {
            for (Map templateEngine : templateEngines) {
                addTemplateEngine(site, templateEngine);
            }

            if (defaultEngine == null && !engines.isEmpty()) {
                defaultEngine = engines.entrySet().iterator().next().getValue();
            }

        } else {

            String engineClass = "ca.simplegames.micro.viewers.velocity.VelocityViewRenderer";
            try {
                defaultEngine = (ViewRenderer) ClassUtilities.loadClass(engineClass).newInstance();
                Map<String, Object> engineConfig = new HashMap<String, Object>();
                engineConfig.put("resource_cache_enabled", "true");
                engineConfig.put("resource_cache_interval", "15");
                engineConfig.put("global_macro_library", "global_library.vm");

                defaultEngine.loadConfiguration(site, engineConfig);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * add a new template engine
     *
     * @param site                     a SiteContext instance
     * @param templateEngineDefinition a Map containing the engine definitions
     * @throws Exception if the engine cannot be loaded
     */
    public void addTemplateEngine(SiteContext site, Map templateEngineDefinition) throws Exception {
        Map<String, Object> engineConfig = (Map<String, Object>) templateEngineDefinition.get("engine");
        String name = (String) engineConfig.get("name");
        String klass = (String) engineConfig.get("class");
        Map<String, Object> options = (Map<String, Object>) engineConfig.get("options");
        boolean isDefaultEngine = StringUtils.defaultString(engineConfig.get("default"),
                "false").trim().equalsIgnoreCase("true");

        ViewRenderer engine = null;
        try {
            engine = (ViewRenderer) ClassUtilities.loadClass(klass).newInstance();
            engine.loadConfiguration(site, options);
            engines.put(name, engine);

            if (isDefaultEngine) {
                defaultEngine = engine;
                if (site.getRepositoryManager() != null) {
                    List<Repository> repositories = site.getRepositoryManager().getRepositories();
                    Logger log = site.getLog();
                    log.info("Resetting the default template engine:");
                    for (Repository repository : repositories) {
                        repository.setRenderer(engine);
                        log.info(String.format(" ** repository: `%s`, using: `%s`", repository.getName(), name));
                    }
                }
            }

            site.getLog().info(String.format(" engine: %s, class: %s%s",
                    name, klass, isDefaultEngine ? ", default." : Globals.EMPTY_STRING));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return all known engins
     */
    public Map<String, ViewRenderer> getEngines() {
        return Collections.unmodifiableMap(engines);
    }

    /**
     * @param name the name of the engine
     * @return a template engine, if registered with {@code name}
     */
    public ViewRenderer getEngine(String name) {
        return engines.get(name);

    }

    // return the default rendering engine
    public ViewRenderer getDefaultEngine() {
        return defaultEngine;
    }
}
