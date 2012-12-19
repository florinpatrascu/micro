package ca.simplegames.micro;

import org.jrack.Context;
import org.jrack.context.MapContext;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * This class contains configuration information for a particular Micro
 * site.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 11:13 AM)
 */
public class SiteContext extends MapContext<String>{

    public SiteContext(Context<String> env) {
        for (Map.Entry<String, Object> entry : env) {
            with(entry.getKey(), entry.getValue());
        }
    }

    public ServletContext getServletContext(){
        return (ServletContext) map.get(Globals.SERVLET_CONTEXT);
    }
}
