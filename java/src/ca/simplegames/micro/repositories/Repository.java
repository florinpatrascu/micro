package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * A Repository is responsible for identifying resources in a given folder and it can be used
 * as a context helper for accessing a specific resource, rendered or not
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 1:58 PM)
 */
public abstract class Repository {
    private Logger log;

    private String name;
    private MicroCache cache;
    private SiteContext site;
    private String pathName;
    private File path;
    private ViewRenderer renderer;
    private boolean isDefault;

    protected Repository(String name, MicroCache cache, SiteContext site, String pathName) {
        this.name = name;
        log = LoggerFactory.getLogger("Repository::" + name.toUpperCase());

        this.cache = cache;
        this.site = site;

        this.pathName = pathName;
        path = new File(pathName);
        if (!path.exists()) {
            path = new File(site.getWebInfPath(), pathName);
        }

        if (path.exists()) {

            // Initialize the View renderer
            Map<String, Object> rendererConfig = (Map<String, Object>) site.getAppConfig().get("renderer");
            String rendererClass = "ca.simplegames.micro.viewers.velocity.VelocityViewRenderer";

            if (!CollectionUtils.isEmpty(rendererConfig)) {
                rendererClass = StringUtils.defaultString(rendererConfig.get("class"), rendererClass);
            }
            try {

                renderer = (ViewRenderer) ClassUtilities.loadClass(rendererClass).newInstance();
                renderer.setRepository(this);
                renderer.loadConfiguration(rendererConfig);

                log.info(String.format(" ... added repository: '%s' on: %s", name, path.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            log.error(String.format("You defined a repository: '%s' on: %s, but there is no content at that path; %s",
                    name, pathName, path.getAbsolutePath()));
        }
        //
    }

    public File getPath() {
        return path;
    }

    public abstract InputStream getInputStream(String name);

    public MicroCache getCache() {
        return cache;
    }

    public void setRenderer(ViewRenderer renderer) {
        this.renderer = renderer;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public SiteContext getSite() {
        return site;
    }

    public Logger getLog() {
        return log;
    }

    public String getName() {
        return name;
    }

    public String getPathName() {
        return pathName;
    }

    public RepositoryWrapper getRepositoryWrapper(MicroContext context) {
        return new RepositoryWrapper(this, context);
    }

    public ViewRenderer getRenderer() {
        return renderer;
    }

    public long getLastModified(String name) {
        return pathToFile(name).lastModified();
    }

    public File pathToFile(String name) {
        return new File(getPath(), name);
    }

    public String read(String path) throws Exception {
        String content = null;

        if (path != null) {
            final File file = pathToFile(path);

            if (cache!= null) {
                content = (String) cache.get(file.getAbsolutePath());
            }

            if (content == null) {
                final Reader reader = new InputStreamReader(new FileInputStream(file), Globals.UTF8);
                content = IO.getString(reader);

                if (cache != null) {
                    cache.put(file.getAbsolutePath(), content);
                }
            }
        }
        return content;
    }

}
