package ca.simplegames.micro;

import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import ca.simplegames.micro.utils.PathUtilities;

import java.util.Map;

/**
 * testing the redirect exception from a non-scripting controller
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-12)
 */
public class RedirectTestController implements Controller {
    @Override
    public void execute(MicroContext context, Map configuration) throws ControllerException {
        String resourceType = PathUtilities.extractType((String) context.get(Globals.PATH));
        context.setRedirect("redirected" + resourceType, false);
    }

}
