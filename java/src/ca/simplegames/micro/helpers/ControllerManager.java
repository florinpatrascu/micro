package ca.simplegames.micro.helpers;

import ca.simplegames.micro.Helper;
import ca.simplegames.micro.MicroContext;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 12:53 PM)
 */
public class ControllerManager implements Helper {

    private ServletContext site;

    public void init(ServletContext site) throws Exception {
        this.site=site;
    }

    public Object call(MicroContext context) throws Exception {
        return null;
    }

    public String toString(MicroContext context) throws Exception {
        return null;
    }
}
