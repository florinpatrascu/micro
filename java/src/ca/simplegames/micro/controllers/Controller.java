package ca.simplegames.micro.controllers;

import ca.simplegames.micro.MicroContext;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:46 PM)
 */
public interface Controller extends Serializable {
    /**
     * Execute the action using the given context and the optional configuration.
     *
     * @param context       a Map containing input parameters
     * @param configuration an action specific configuration. Can be null
     * @return an Object, optional
     * @throws Exception if any problems
     */
    public Object execute(MicroContext context, Map configuration) throws Exception;
}
