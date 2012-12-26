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

package ca.simplegames.micro.viewers.velocity;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jrack.utils.ClassUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * ViewRenderer which uses the Velocity template engine from the Apache
 * Jakarta group to render content.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 2.0
 */

public class VelocityViewRenderer implements ViewRenderer {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_PROPERTIES_PATH = "WEB-INF/velocity.properties";

    // Here are the names of Velocity 1.x properties that can contains paths.
    private static final String[] velocityKeys = {
            "runtime.log", "file.resource.loader.path", "velocimacro.library"
    };

    private Repository repository;
    private final VelocityEngine velocityEngine = new VelocityEngine();
    private Properties velocityProperties = new Properties();

    private boolean resourceCacheEnabled = false;
    private int resourceCacheInterval = 2;


    public void loadConfiguration(Map<String, Object> configuration) throws Exception {
        setResourceCacheEnabled(StringUtils.defaultString(configuration.get("resource_cache_enabled"), "true"));
        setResourceCacheInterval(StringUtils.defaultString(configuration.get("resource_cache_interval"), "20"));

        try {
            loadVelocityProperties(
                    ClassUtilities.getResourceAsStream(
                            "ca/simplegames/micro/viewers/velocity/velocity.properties"), configuration);

            velocityEngine.setProperty("micro.VM_global_library.vm.path",
                    StringUtils.defaultString(configuration.get("global_macro_library"),
                            Globals.DEFAULT_VELOCITY_GLOBAL_LIBRARY_PATH));

            init();

        } catch (IOException e) {
            throw new Exception("IO error: " + e, e);
        }
    }

    public void init() throws Exception {
        ExtendedProperties eprops = new ExtendedProperties();
        eprops.putAll(velocityProperties);
        eprops.addProperty(RuntimeConstants.RESOURCE_LOADER, "micro");

        eprops.setProperty("micro.resource.loader.description", "Micro internal resource loader.");
        eprops.setProperty("micro.resource.loader.class", "ca.simplegames.micro.viewers.velocity.MicroResourceLoader");
        eprops.setProperty("micro.resource.loader.repository", repository);

        if (resourceCacheEnabled) {
            eprops.setProperty("micro.resource.loader.cache", "true");
            eprops.setProperty("micro.resource.loader.modificationCheckInterval",
                    Integer.toString(getResourceCacheInterval()));
        }

        // Apply properties to VelocityEngine.
        velocityEngine.setExtendedProperties(eprops);
        try {
            velocityEngine.init();
            log.warn("todo: Set servlet context");
            //velocityEngine.setApplicationAttribute(ServletContext.class.getName(), sitelet.getServletContext());
        } catch (Exception ex) {
            log.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
            throw new VelocityException(ex.getMessage());
        }
        //log.info("Resource loader: " + velocityEngine.getProperty(VelocityEngine.RESOURCE_LOADER));

    }

    public long render(MicroContext context, String path, Reader in, Writer out) throws Exception {

        StringWriter writer = new StringWriter();

        try {
            VelocityViewContext viewContext = new VelocityViewContext(context);
            velocityEngine.mergeTemplate(path, Globals.UTF8, viewContext, writer);

            return IO.copy(new StringReader(writer.toString()), out);

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("%s not found.", path));

        } catch (IOException e) {
            log.error(path + ", IO exception: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }


    public long render(MicroContext context, String path, InputStream in, OutputStream out) throws Exception {
        return render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));
    }

    private void loadVelocityProperties(InputStream in, Map<String, Object> configuration) throws IOException {
        try {
            velocityProperties.load(in);
        } finally {
            IO.close(in);
        }
    }

    public boolean isResourceCacheEnabled() {
        return resourceCacheEnabled;
    }

    public void setResourceCacheEnabled(boolean resourceCacheEnabled) {
        this.resourceCacheEnabled = resourceCacheEnabled;
    }

    public void setResourceCacheEnabled(String resourceCacheEnabled) {
        setResourceCacheEnabled("true".equals(resourceCacheEnabled));
    }

    public int getResourceCacheInterval() {
        return resourceCacheInterval;
    }

    public void setResourceCacheInterval(int resourceCacheInterval) {
        this.resourceCacheInterval = resourceCacheInterval;
    }

    public void setResourceCacheInterval(String resourceCacheInterval) {
        if (resourceCacheInterval != null) {
            setResourceCacheInterval(Integer.parseInt(resourceCacheInterval));
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
