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
import ca.simplegames.micro.repositories.Repository;

import java.io.*;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 3:57 PM)
 */
public interface ViewRenderer {

    /**
     * Set the current repository
     *
     * @param repository the repository instance responsible with
     *                   providing acces to local resources
     */
    public void setRepository(Repository repository);

    /**
     * Render the view.
     *
     * @param context The RequestContext
     * @param path    The path to the template
     * @param in      The InputStream to read view template from
     * @param out     The OutputStream to write the rendered view
     * @return the number of bytes rendered
     * @throws ViewException
     */
    public long render(MicroContext context, String path, InputStream in,
                       OutputStream out) throws FileNotFoundException, ViewException;

    /**
     * Render the view.
     *
     * @param context The RequestContext
     * @param path    The path to the template
     * @param in      The Reader to read view template from
     * @param out     The Writer to write the rendered view
     * @return the number of bytes rendered
     * @throws ViewException
     */

    public long render(MicroContext context, String path, Reader in, Writer out) throws FileNotFoundException, ViewException;

    /**
     * Load the configuration for the view.
     *
     * @param configuration The configuration object
     */

    public void loadConfiguration(Map<String, Object> configuration) throws Exception;
}
