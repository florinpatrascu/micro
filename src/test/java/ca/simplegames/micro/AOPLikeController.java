/*
 * Copyright (c)2014 Florin T.Pătraşcu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro;

import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerWrapper;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * class used to wrap around some test controllers and emit signals
 * during the following events:
 * - before
 * - onException
 * - after
 * It is used to test Micro's AOP poor man solution :)
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-11)
 */
public class AOPLikeController implements ControllerWrapper {

    private String controllerName;
    private Logger log;

    @Override
    public void execute(String controllerName, MicroContext context, Map configuration)
            throws ControllerException, FileNotFoundException {

        this.controllerName = controllerName;
        log = context.getLog();

        try {
            log.info("AOP::Before> " + controllerName);
            // do wrapper's own BEFORE stuff
            context.with("before", Boolean.TRUE);
            // Execute the controller:
            context.getSiteContext().getControllerManager().execute(controllerName, context, configuration);
        } catch (Exception rte) {
            onException(context);
            // if you wish: throw new ControllerException(rte);
        } finally {
            log.info("AOP::After> " + controllerName);
            // do wrapper's own AFTER stuff
            context.with("after", Boolean.TRUE);
        }
    }

    private void onException(MicroContext context) {
        log.error("error> RuntimeException: " + controllerName);
        // do wrapper stuff when errors are trapped
        context.with("error", Boolean.TRUE);
    }
}
