package ca.simplegames.micro;

import ca.simplegames.micro.controllers.Controller;

import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-18 11:17 PM)
 */
public interface Helper {
    public void init(SiteContext site) throws Exception;

    public String getName();

    public String getDescription();

    public String getVersion();

    public String getUrlAddress();

    public Map<String, Controller> getControllers();

    public Object call(MicroContext context) throws Exception;

    public String toString(MicroContext... context) throws Exception;
}
