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
     * @throws Exception
     */
    public long render(MicroContext context, String path, InputStream in,
                       OutputStream out) throws Exception;

    /**
     * Render the view.
     *
     * @param context The RequestContext
     * @param path    The path to the template
     * @param in      The Reader to read view template from
     * @param out     The Writer to write the rendered view
     * @return the number of bytes rendered
     * @throws Exception
     * @throws java.io.IOException
     */

    public long render(MicroContext context, String path, Reader in, Writer out) throws Exception;

    /**
     * Load the configuration for the view.
     *
     * @param configuration The configuration object
     */

    public void loadConfiguration(Map<String, Object> configuration) throws Exception;
}
