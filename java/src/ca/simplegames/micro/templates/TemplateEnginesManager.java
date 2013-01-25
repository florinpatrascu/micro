package ca.simplegames.micro.templates;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.jrack.utils.ClassUtilities;

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

    @SuppressWarnings("unchecked")
    public TemplateEnginesManager(SiteContext site, Map<String, Object> config) throws Exception {
        // Initialize a repository specific View renderer

        List<Map<String, Object>> templateEngines = (List<Map<String, Object>>) site.getAppConfig().get("template_engines");
        if (templateEngines != null) {
            for (Map templateEngine : templateEngines) {
                Map<String, Object> engineConfig = (Map<String, Object>) templateEngine.get("engine");
                String name = (String) engineConfig.get("name");
                String klass = (String) engineConfig.get("class");
                Map<String, Object> options = (Map<String, Object>) engineConfig.get("options");
                boolean isDefaultEngine = StringUtils.defaultString(engineConfig.get("default"),
                        "false").trim().equalsIgnoreCase("true");

                ViewRenderer engine = (ViewRenderer) ClassUtilities.loadClass(klass).newInstance();

                engine.loadConfiguration(site, options);
                engines.put(name, engine);

                if (defaultEngine == null && isDefaultEngine) {
                    defaultEngine = engine;
                }
                site.getLog().info(String.format(" engine: %s, class: %s%s",
                        name, klass, isDefaultEngine ? ", default." : Globals.EMPTY_STRING));
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

    public Map<String, ViewRenderer> getEngines() {
        return Collections.unmodifiableMap(engines);
    }

    public ViewRenderer getEngine(String name) {
        return engines.get(name);

    }

    public ViewRenderer getDefaultEngine() {
        return defaultEngine;
    }
}
