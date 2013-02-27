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

package ca.simplegames.micro.viewers;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.repositories.Repository;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 3:57 PM)
 */
public interface ViewRenderer {

    /**
     * Render the view.
     *
     * @param path       The path to the template
     * @param repository the repository used for extracting the resource specified by path
     * @param context    The RequestContext
     * @param out        The Writer to write the rendered view
     * @return the number of bytes rendered
     * @throws ViewException
     */

    public long render(String path, Repository repository, MicroContext context, Writer out)
            throws FileNotFoundException, ViewException, ControllerException;

    /**
     * Load the configuration for the view.
     *
     * @param site          the SiteContext instance
     * @param configuration The configuration object
     */

    public void loadConfiguration(SiteContext site, Map<String, Object> configuration) throws Exception;

    /**
     * @return the name of the rendering engine
     */
    public String getName();
}
