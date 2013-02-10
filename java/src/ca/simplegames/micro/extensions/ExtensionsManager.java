/*
 * Copyright (c) 2013 the original author or authors.
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

package ca.simplegames.micro.extensions;

import ca.simplegames.micro.Extension;
import ca.simplegames.micro.Micro;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.Assert;
import ca.simplegames.micro.utils.PathUtilities;
import org.jrack.utils.ClassUtilities;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manager responsible with loading and registering Micro extensions
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-16 5:48 PM)
 */
public class ExtensionsManager {
    private Map<String, Map<String, Object>> extensionsConfigMap = new HashMap<String, Map<String, Object>>();
    private Set<String> registeredExtensions = new HashSet<String>();
    private File extensionsFolder;
    private SiteContext site;

    @SuppressWarnings("unchecked")
    public ExtensionsManager(SiteContext site, File[] configFiles) throws Exception {
        Assert.notNull(site);
        Assert.notNull(configFiles);
        this.site = site;

        if (configFiles != null && configFiles.length > 0) {
            extensionsFolder = configFiles[0].getParentFile();
            if (site.getLog().isDebugEnabled()) {
                site.getLog().debug(String.format("Extensions folder used: %s/", extensionsFolder.getAbsolutePath()));
            }

            if (extensionsFolder.exists() && extensionsFolder.isDirectory()) {
                for (File configFile : configFiles) {
                    Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(new FileInputStream(configFile));
                    final String fileName = PathUtilities.extractName(configFile);
                    extensionsConfigMap.put(fileName, yaml);
                }
            }
        }
    }

    public ExtensionsManager require(String name) throws Exception { //todo: improve the Exceptions
        Extension extension = null;
        Map<String, Object> yaml = extensionsConfigMap.get(name);
        File extensionLibDir;

        if (yaml != null && !registeredExtensions.contains(name)) {
            final String extensionLibFolderName = name + "/lib";

            if (extensionsFolder == null) {
                extensionLibDir = new File(site.getApplicationConfigPath(), "/extensions/" + extensionLibFolderName);
            } else {
                extensionLibDir = new File(extensionsFolder, extensionLibFolderName);
            }

            Class[] parameters = new Class[]{URL.class};
            URLClassLoader microClassLoader = (URLClassLoader) Micro.class.getClassLoader();
            Class sysclass = URLClassLoader.class;

            if (extensionLibDir.exists() && extensionLibDir.isDirectory()) {
                for (File file : site.files(extensionLibDir, ".jar")) {
                    try {
                        Method method = sysclass.getDeclaredMethod("addURL", parameters);
                        method.setAccessible(true);
                        method.invoke(microClassLoader, file.toURI().toURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new IOException("Error, could not add URL to system classloader");
                    }
                }

                try {
                    Class classToLoad = Class.forName((String) yaml.get("class"), true, microClassLoader);
                    extension = (Extension) classToLoad.newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new Exception(String.format("Class: %s, not found.", yaml.get("class")));
                }
            }

            if (extension == null) {
                // check if the Extension is using a Micro class...
                final Class klass = ClassUtilities.loadClass((String) yaml.get("class"));
                if (klass != null) {
                    extension = (Extension) klass.newInstance();
                }
            }

            if (extension != null) {
                extension.register(name, site, yaml);

                if (registeredExtensions.isEmpty()) { //cosmetics
                    site.getLog().info("Extensions:");
                }
                registeredExtensions.add(name);
                site.getLog().info(String.format(" - %s, loaded.", extension.getName()));
            } else {
                site.getLog().error(String.format("  %s, not loaded.", name));
            }
        }
        return this;
    }

}
