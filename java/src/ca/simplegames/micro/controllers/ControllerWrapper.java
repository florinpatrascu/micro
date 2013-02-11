package ca.simplegames.micro.controllers;

import ca.simplegames.micro.MicroContext;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Interface defining the contract terms for implementing a ControllerWrapper
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-11)
 */
public interface ControllerWrapper {

    /**
     * execute a controller specified by name. The name can be a path to a {@link ScriptController} or the
     * name of a Java {@link ca.simplegames.micro.Controller}
     *
     * @param name          the name of the controller
     * @param context       a runtime context {@link MicroContext}
     * @param configuration the controller configuration as defined by the user
     * @throws ControllerException   if any errors are triggered during the controller evaluation
     * @throws FileNotFoundException if the controller was not found
     */
    public void execute(String name, MicroContext context, Map configuration)
            throws ControllerException, FileNotFoundException;
}
