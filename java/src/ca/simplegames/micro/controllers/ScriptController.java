/*
 * Copyright (c)2013 Florin T.Pătraşcu
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

package ca.simplegames.micro.controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A basic wrapper around a scripting controller
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:50 PM)
 */
public class ScriptController implements Controller {
    private String language = null;
    private String script = null;
    private String controllerName;

    /**
     * Construct a new ScriptController for a given script.
     *
     * @param site   The SiteContext
     * @param script The file representing the script
     */
    public ScriptController(SiteContext site, String controllerName, String script) {

        if (StringUtils.isNotBlank(controllerName) && StringUtils.isNotBlank(script)) {
            this.script = script;
            try {
                language = BSFManager.getLangFromFilename(controllerName);
            } catch (BSFException e) {
                e.printStackTrace();
            }
            this.controllerName = controllerName;
        }
    }

    /**
     * instantiate a new BSF Engine and evaluate the script of the Controller
     *
     * @param context       a Map containing input parameters
     * @param configuration an action specific configuration. Can be null
     * @throws ControllerException
     */
    public void execute(MicroContext context, Map configuration) throws ControllerException {
        try {
            final Logger logger = LoggerFactory.getLogger(controllerName);
            // pre-load the engine to make sure we were called right
            BSFEngine bsfEngine = context.getSiteContext().getBSFEngine(language, context, configuration, logger);
            // Execute with the proper language, the fileName (for error reporting),
            // the row and column to start at, and finally the contents of the script
            try {
                // some examples: http://massapi.com/class/bs/BSFManager.html
                bsfEngine.exec(controllerName, 0, 0, script);
            } catch (Throwable e) {
                logger.error(e.getMessage());
                throw new ControllerException(
                        String.format("error while executing: %s; details: %s", controllerName, e.getMessage()));
            }
            // return bsfManager.lookupBean(Globals.SCRIPT_CONTROLLER_RESPONSE);
        } catch (Exception e) {
            throw new ControllerException(String.format("Problems loading org.apache.bsf.BSFEngine: %s", language), e);
        }

    }
}
