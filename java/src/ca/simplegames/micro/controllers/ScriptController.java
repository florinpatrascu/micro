/*
 * Copyright (c)2012. Florin T.PATRASCU
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

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.jrack.context.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:50 PM)
 */
public class ScriptController implements Controller {
    private String language = null;
    private String script = null;
    private String controllerName;

    /**
     * Construct a new ScriptAction for the given script.
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

    public Object execute(MicroContext context, Map configuration) throws Exception {
        BSFManager bsfManager = new BSFManager();
        // bsfManager.setClassLoader(BSFManager.class.getClassLoader());
        bsfManager.setClassLoader(this.getClass().getClassLoader());
        // bsfManager.setClassLoader(Thread.currentThread().getContextClassLoader());
        // bsfManager.declareBean("response", context.getSiteContext(), HttpServletResponse.class);
        // bsfManager.declareBean("request", context.getRequest(), HttpServletRequest.class);
        bsfManager.declareBean("context", context, MicroContext.class);
        // bsfManager.declareBean("rack_input", context.getRackInput(), MapContext.class);
        bsfManager.declareBean("site", context.getSiteContext(), SiteContext.class);
        final Logger logger = LoggerFactory.getLogger(controllerName);
        bsfManager.declareBean("log", logger, Logger.class);

        // pre-load the engine to make sure we were called right
        org.apache.bsf.BSFEngine bsfEngine = null;
        try {
            bsfEngine = bsfManager.loadScriptingEngine(language);
        } catch (BSFException e) {
            throw new Exception("Problems loading org.apache.bsf.BSFEngine: " + language, e);
        }

        // Execute with the proper language, the fileName (for error reporting),
        // the row and column to start at, and finally the contents of the script
        try {
            // some examples: http://massapi.com/class/bs/BSFManager.html
            bsfEngine.exec(controllerName, 0, 0, script);
        } catch (Throwable e) {
            logger.error(e.getMessage());
            throw new Exception("error while executing: "+controllerName+"; details: "+e.getMessage());
        }
        return bsfManager.lookupBean(Globals.SCRIPT_CONTROLLER_RESPONSE);
    }
}
