package ca.simplegames.micro.templates;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import org.jrack.Context;
import org.jrack.context.MapContext;

import java.util.Map;

/**
 * Extend this class if you want to write a custom renderer
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 2:10 PM)
 */
public abstract class Template {
    private SiteContext site;
    private MicroContext context;

    /**
     * render a resource
     * @param engine
     * @param path
     * @param options
     * @return
     * @throws Exception
     */
    public abstract String render(String engine, String path, MapContext<String> options) throws Exception;
    public abstract String render(String path, MapContext<String> options) throws Exception;
}
