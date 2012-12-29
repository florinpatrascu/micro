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
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.ClassUtils;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 4:54 PM)
 */
public class MicroResourceLoader extends ResourceLoader {

    private static final String VM_GLOBAL_LIBRARY = "VM_global_library.vm";
    private String vmGlobalLibraryPath = Globals.DEFAULT_VELOCITY_GLOBAL_LIBRARY_PATH;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Repository repository;
    private static final String VELOCITY_DEFAULT_VM_GLOBAL_LIBRARY_VM = "ca/simplegames/micro/viewers/velocity/VM_global_library.vm";

    /**
     * Initialize the ResourceLoader.
     *
     * @param configuration The ExtendedProperties object
     */

    public void init(ExtendedProperties configuration) {
    }


    /**
     * Common init method for ResourceLoaders.
     *
     * @param rs            The RuntimeServices
     * @param configuration The configuration
     */
    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs, configuration);
        vmGlobalLibraryPath = (String) rs.getProperty("micro.VM_global_library.vm.path");
        repository = (Repository) rs.getProperty("micro.resource.loader.repository");

    }

    /**
     * Get the InputStream for the resource.
     *
     * @param name The resource name
     * @return The InputStream
     * @throws org.apache.velocity.exception.ResourceNotFoundException
     *
     */

    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (VM_GLOBAL_LIBRARY.equals(name)) {
            try {
                return ClassUtils.getResourceAsStream(vmGlobalLibraryPath);
            } catch (Exception e) {
                // todo: check why there are two calls here [2012.12.20 florin]
                // log.warn(String.format("%s doesn't exist, will load ours: %s",
                //           vmGlobalLibraryPath, Globals.DEFAULT_VELOCITY_GLOBAL_LIBRARY_PATH));
                try {
                    return ClassUtils.getResourceAsStream(VELOCITY_DEFAULT_VM_GLOBAL_LIBRARY_VM);
                } catch (Exception e1) {
                    log.error("Cannot find: " + VELOCITY_DEFAULT_VM_GLOBAL_LIBRARY_VM);
                }
            }
        }

        try {
            return repository.getInputStream(name);
        } catch (Exception e) {
            throw new ResourceNotFoundException(name);
        }
    }

    /**
     * Return true if the source is modified.
     *
     * @param resource The Resource
     * @return True if it is modified
     */

    public boolean isSourceModified(Resource resource) {
        return resource.getLastModified() != getLastModified(resource);
    }

    /**
     * Return get the last modified time for the resource.
     *
     * @param resource The Resource
     * @return The last modified time
     */

    public long getLastModified(Resource resource) {
        if (VM_GLOBAL_LIBRARY.equals(resource.getName())) {
            return -1;
        }

        try {
            return repository.getLastModified(resource.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}