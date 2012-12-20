package ca.simplegames.micro;

import ca.simplegames.micro.controllers.Controller;

import java.util.Map;

/**
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-18 11:17 PM)
 */
public interface Helper {

    /**
     * executed only once, when the Micro framework starts up
     *
     * @param site the Micro site object
     * @throws Exception
     */
    public Helper init(SiteContext site, Map<String, Object> config) throws Exception;

    public String getName();

    public String getDescription();

    public String getVersion();

    public String getRepositoryAddress();

    /**
     * If there is a path specified, the helper will execute only if
     * the request matches the path
     *
     * @return the path this helper is answering on
     */
    public String getPath();

    /**
     * @return a map containing Helper's own controllers; Name and Instance
     */
    public Map<String, Controller> getControllers();

    /**
     * executed on every request
     *
     * @param context the Micro context
     * @return an optional response
     * @throws Exception
     */
    public Object call(MicroContext context) throws Exception;
}
