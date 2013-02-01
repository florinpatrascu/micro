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
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.Assert;
import ca.simplegames.micro.utils.PathUtilities;
import org.jrack.utils.ClassUtilities;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Manager responsible with loading and registering Micro extensions
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-16 5:48 PM)
 */
public class ExtensionsManager {
    private Map<String, Map<String, Object>> extensionsConfigMap = new HashMap<String, Map<String, Object>>();
    private SiteContext site;
    private Set<String> registeredExtensions = new HashSet<String>();

    @SuppressWarnings("unchecked")
    public ExtensionsManager(SiteContext site, File[] configFiles) throws Exception {
        Assert.notNull(site);
        Assert.notNull(configFiles);
        this.site = site;

        for (File configFile : configFiles) {
            Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(new FileInputStream(configFile));
            final String fileName = PathUtilities.extractName(configFile);
            extensionsConfigMap.put(fileName, yaml);
        }
    }

    public ExtensionsManager require(String name) throws Exception { //todo: improve the Exceptions
        Extension extension = null;
        Map<String, Object> yaml = extensionsConfigMap.get(name);
        if (yaml != null && !registeredExtensions.contains(name)) {

            File extensionLibDir = new File(site.getApplicationConfigPath(), "/extensions/" + name + "/lib");

            if (extensionLibDir.exists() && extensionLibDir.isDirectory()) {
                List<URL> jarUrls = new ArrayList<URL>();
                for (File file : site.files(extensionLibDir, ".jar")) {
                    jarUrls.add(file.toURI().toURL());
                }

                URLClassLoader child = new URLClassLoader(jarUrls.toArray(new URL[jarUrls.size()]),
                        this.getClass().getClassLoader());
                Class classToLoad = Class.forName((String) yaml.get("class"), true, child);
                extension = (Extension) classToLoad.newInstance();
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
