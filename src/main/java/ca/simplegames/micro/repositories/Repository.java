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

package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.View;
import ca.simplegames.micro.cache.MicroCache;
import ca.simplegames.micro.cache.MicroCacheException;
import ca.simplegames.micro.templates.TemplateEnginesManager;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.utils.PathUtilities;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * A Repository is responsible for identifying resources in a given folder and it can be used
 * as a context helper for accessing a specific resource, rendered or not
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 1:58 PM)
 */
public abstract class Repository {
    public static final String VIEW_CACHE_KEY_FORMAT = "%s::%s";
    private Logger log;

    private String name;
    private MicroCache cache;
    private SiteContext site;
    private String pathName;
    private File path;
    private ViewRenderer renderer;
    private File config;
    private boolean isDefault;
    private Map<String, ViewConfigElements> viewConfigPathMap = new HashMap<String, ViewConfigElements>();

    /**
     * creates a new Repository object
     *
     * @param name           the name of the repository
     * @param cache          a dedicated cache
     * @param site           the SiteContext of the app
     * @param pathName       the path to the root of the repository
     * @param configPathName the folder name of the config folder (if any)
     * @param engineName     the template engine name used by default when not specified by the user
     */
    protected Repository(String name, MicroCache cache, SiteContext site, String pathName, String configPathName, String engineName) {
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

            if (configPathName != null) {
                config = new File(path, configPathName);
            }

            final TemplateEnginesManager templateEnginesManager = site.getTemplateEnginesManager();
            renderer = engineName != null ?
                    templateEnginesManager.getEngine(engineName) :
                    templateEnginesManager.getDefaultEngine();

            site.getLog().info(String.format(" ** '%s'", name));
            site.getLog().info(String.format("   - path....: '%s'", path.getAbsolutePath()));
            site.getLog().info(String.format("   - renderer: '%s'", renderer.getName()));

            if (cache != null) {
                site.getLog().info(String.format("   - cache...: '%s'", cache.getName()));
            }
            if (config != null && config.exists() && config.isDirectory()) {
                site.getLog().info(String.format("   - config..: '%s'", config.getAbsolutePath()));
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

    public abstract InputStream getInputStream(String name) throws Exception;

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

            if (cache != null) {
                content = (String) cache.get(file.getAbsolutePath());
            }

            if (content == null) {
                if (file.exists()) {
                    final Reader reader = getReader(path);

                    content = IO.getString(reader);
                    if (cache != null) {
                        cache.put(file.getAbsolutePath(), content);
                    }
                } else {
                    throw new FileNotFoundException(file.getAbsolutePath());
                }
            }
        }
        return content;
    }

    /**
     * obtain a Reader from the path.
     * Mr. Developer - please don't forget to close it if you're using it.
     * You can use {@link IO}.close, for example
     *
     * @param path the path to a resource in this repository
     * @return the Reader or null if the path is null
     * @throws Exception a File not found exception if there is no such a resource
     */
    public Reader getReader(String path, Charset encoding) throws Exception {

        if (path != null) {
            final File file = pathToFile(path);
            if (file.exists()) {
                return new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
        }
        return null;
    }

    public Reader getReader(String path) throws Exception {
        return getReader(path, Charset.forName(Globals.UTF8));
    }

    /**
     * @param name a resource name; a view template name
     * @return a {@link View} instance
     */
    @SuppressWarnings("unchecked")
    public View getView(String name) {
        if (config != null) {
            ViewConfigElements viewConfigElements = viewConfigPathMap.get(name);

            if (viewConfigElements == null) {
                File viewConfig = new File(config, PathUtilities.extractViewPath(name) + Globals.YML_EXTENSION);
                viewConfigElements = new ViewConfigElements(viewConfig.getAbsolutePath(), viewConfig.exists());
                viewConfigPathMap.put(name, viewConfigElements);
            }

            String key = String.format(VIEW_CACHE_KEY_FORMAT, getName(), name);
            Map viewModel = null;

            // don't hit the cache unnecessarily if the config was never there!
            if (cache != null && viewConfigElements.isConfigFileExists()) {
                try {
                    viewModel = (Map) cache.get(key);
                } catch (MicroCacheException e) {
                    log.error(String.format("Cannot cache the view: %s", viewConfigElements.getConfigFullPath()));
                    e.printStackTrace();
                }
            }

            // there is no ViewModel if the config file doesn't exist
            if (viewModel == null && viewConfigElements.isConfigFileExists()) {
                try {
                    viewModel = (Map) new Yaml().load(
                            new FileInputStream(new File(viewConfigElements.getConfigFullPath())));
                    if (cache != null) {
                        try {
                            cache.put(key, viewModel);
                        } catch (MicroCacheException e) {
                            log.error(String.format("Cannot use the cache for retrieving this view: %s",
                                    viewConfigElements.getConfigFullPath()));
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    log.error("cannot load the configuration from: " + name);
                    e.printStackTrace();
                }
            }

            if (viewModel != null) {
                return new View(viewModel);
            }
        }
        return null;
    }

    /**
     * internal class used as a shortcut to avoid File name calculations; a performance optimization
     */
    class ViewConfigElements {
        private String configFullPath;
        private boolean configFileExists = false;

        ViewConfigElements(String configFullPath, boolean configFileExists) {
            this.configFullPath = configFullPath;
            this.configFileExists = configFileExists;
        }

        public String getConfigFullPath() {
            return configFullPath;
        }

        public boolean isConfigFileExists() {
            return configFileExists;
        }
    }
}
