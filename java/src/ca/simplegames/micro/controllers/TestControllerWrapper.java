package ca.simplegames.micro.controllers;

import ca.simplegames.micro.MicroContext;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Test controller that acts as a simple BEFORE and AFTER aspect
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-11 )
 */
public class TestControllerWrapper implements ControllerWrapper {

    private String controllerName;
    private Logger log;

    @Override
    public void execute(String controllerName, MicroContext context, Map configuration) throws ControllerException, FileNotFoundException {
        this.controllerName = controllerName;
        log = context.getLog();

        try {
            log.info("BEFORE: " + controllerName);
            // do wrapper's own BEFORE stuff
            // Execute the controller:
            context.getSiteContext().getControllerManager().execute(controllerName, context, configuration);
        } catch (RuntimeException rte) {
            onException();
            throw new ControllerException(rte.getMessage());
        } catch (ControllerNotFoundException e) {
            onException();
            throw new FileNotFoundException(controllerName);
        } finally {
            log.info("AFTER: " + controllerName);
            // do wrapper's own AFTER stuff
        }
    }

    private void onException() {
        log.error("RuntimeException: " + controllerName);
        // do wrapper stuff when errors are trapped
    }

    // `finalize` here only used for a weird profiling session, please don't use finalize in
    // your implementations, unless you know what you're doing
    // -----------
    // protected void finalize() throws Throwable  {
    //  super.finalize();
    // }
}
