package ca.simplegames.micro;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-18 11:17 PM)
 */
public interface Helper {
    public void init(ServletContext site) throws Exception;

    public Object call(MicroContext context) throws Exception;

    public String toString(MicroContext context) throws Exception;
}
