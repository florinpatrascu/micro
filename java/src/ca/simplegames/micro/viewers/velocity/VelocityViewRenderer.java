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
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
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

    //private final VelocityEngine velocityEngine = new VelocityEngine();
    private Properties velocityProperties = new Properties();

    private boolean resourceCacheEnabled = false;
    private int resourceCacheInterval = 2;
    private SiteContext site;
    protected String name = "velocity";

    public void loadConfiguration(Map<String, Object> configuration) throws Exception {
        setResourceCacheEnabled(StringUtils.defaultString(configuration.get("resource_cache_enabled"), "true"));
        setResourceCacheInterval(StringUtils.defaultString(configuration.get("resource_cache_interval"), "20"));
        site = (SiteContext) configuration.get("micro.site");

        try {
            loadVelocityProperties(
                    ClassUtilities.getResourceAsStream(
                            "ca/simplegames/micro/viewers/velocity/velocity.properties"), configuration);

            Velocity.setProperty("micro.VM_global_library.vm.path",
                    StringUtils.defaultString(configuration.get("global_macro_library"),
                            Globals.DEFAULT_VELOCITY_GLOBAL_LIBRARY_PATH));
            //Velocity.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM, log );
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
        // eprops.setProperty("micro.resource.loader.repository", repository);

        if (resourceCacheEnabled) {
            eprops.setProperty("micro.resource.loader.cache", "true");
            eprops.setProperty("micro.resource.loader.modificationCheckInterval",
                    Integer.toString(getResourceCacheInterval()));
        }

        // Apply properties to VelocityEngine.
        Velocity.setExtendedProperties(eprops);
        try {
            Velocity.init();
            // velocityEngine.setApplicationAttribute(ServletContext.class.getName(), site.getServletContext());
        } catch (Exception ex) {
            log.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
            throw new VelocityException(ex.getMessage());
        }
        //log.info("Resource loader: " + velocityEngine.getProperty(VelocityEngine.RESOURCE_LOADER));

    }

    public long render(String path, Repository repository, MicroContext context, Reader in, Writer out)
            throws FileNotFoundException, ViewException {

        StringWriter writer = new StringWriter();
        VelocityViewContext viewContext = new VelocityViewContext(context);

        try {
            //velocityEngine.mergeTemplate(path, Globals.UTF8, viewContext, writer);
            Velocity.evaluate(viewContext, writer, path, repository.read(path));
            return IO.copy(new StringReader(writer.toString()), out); // doing this just to compute the size of the result :(
        } catch (ResourceNotFoundException e) {
            throw new FileNotFoundException(String.format("%s not found.", path));
        } catch (Exception e) {
            throw new ViewException(e);
        }
    }


    public long render(String path, Repository repository, MicroContext context, InputStream in, OutputStream out)
            throws FileNotFoundException, ViewException {
        return render(path, repository, context, new InputStreamReader(in), new OutputStreamWriter(out));
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

    @Override
    public String getName() {
        return name;
    }
}
