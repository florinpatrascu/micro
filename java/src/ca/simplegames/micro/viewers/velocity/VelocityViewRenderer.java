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
import ca.simplegames.micro.utils.ResourceUtils;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
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
@SuppressWarnings("unchecked")
public class VelocityViewRenderer implements ViewRenderer, LogChute {
    private static final Logger log = LoggerFactory.getLogger(VelocityViewRenderer.class);
    private static final String DEFAULT_PROPERTIES_PATH = "WEB-INF/classes/velocity.properties";

    // Velocity 1.x properties key names that can contains paths.
    private static final String[] velocityKeys = {
            "runtime.log", "file.resource.loader.path", "velocimacro.library"
    };
    public static final String MICRO_DEFAULT_VELOCITY_PROPERTIES = "ca/simplegames/micro/viewers/velocity/velocity.properties";

    //private final VelocityEngine velocityEngine = new VelocityEngine();
    private Properties velocityProperties = new Properties();

    private boolean resourceCacheEnabled = false;
    private int resourceCacheInterval = 2;
    private SiteContext site;
    protected String name = "velocity";

    public void loadConfiguration(SiteContext site, Map<String, Object> configuration) throws Exception {
        setResourceCacheEnabled(StringUtils.defaultString(configuration.get("resource_cache_enabled"), "true"));
        setResourceCacheInterval(StringUtils.defaultString(configuration.get("resource_cache_interval"), "20"));
        this.site = site;

        try {
            File velocityPropertiesFile = ResourceUtils.getFile( new File(site.getApplicationPath(),
                    StringUtils.defaultString(configuration.get("velocity_properties"),
                            MICRO_DEFAULT_VELOCITY_PROPERTIES)).getAbsolutePath());

            loadVelocityProperties(velocityPropertiesFile.exists()?
                    new java.io.FileInputStream(velocityPropertiesFile):null);

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

    public long render(String path, Repository repository, MicroContext context, Writer out)
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

    /**
     * load an initial set of Velocity properties
     *
     * @param in the input stream for the "velocity.properties" configuration file
     * @throws IOException
     */
    private void loadVelocityProperties(InputStream in) throws IOException {
        try {
            if (in == null) {
                in = ClassUtilities.getResourceAsStream(MICRO_DEFAULT_VELOCITY_PROPERTIES);
            }

            velocityProperties.load(in);
        } finally {
            IO.close(in); // don't worry, it won't blow if in.null? :)
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

    @Override
    public void init(RuntimeServices runtimeServices) throws Exception {
        log.info("Initializing Micro Velocity Engine ...");
    }

    @Override
    public void log(int level, String s) {
        log(level, s, null);
    }

    @Override
    public void log(int level, String message, Throwable throwable) {
        switch (level) {
            case LogChute.DEBUG_ID:
                log.debug(message);
                break;
            case LogChute.ERROR_ID:
                log.error(message);
                break;
            case LogChute.INFO_ID:
                log.info(message);
                break;
            case LogChute.TRACE_ID:
                log.trace(message);
                break;
            case LogChute.WARN_ID:
                log.warn(message);
            default:
                break;
        }
    }

    @Override
    public boolean isLevelEnabled(int level) {
        switch (level) {
            case LogChute.DEBUG_ID:
                return log.isDebugEnabled();
            case LogChute.ERROR_ID:
                return log.isErrorEnabled();
            case LogChute.INFO_ID:
                return log.isInfoEnabled();
            case LogChute.TRACE_ID:
                return log.isTraceEnabled();
            case LogChute.WARN_ID:
                return log.isWarnEnabled();
            default:
                return false;
        }
    }
}
