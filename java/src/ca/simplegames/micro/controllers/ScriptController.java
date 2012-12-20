package ca.simplegames.micro.controllers;

import ca.simplegames.micro.SiteContext;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:50 PM)
 */
public class ScriptController implements Controller {
    private Logger log = LoggerFactory.getLogger(getClass());

    private SiteContext site;
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
        this.site = site;

        if(StringUtils.isNotBlank(controllerName) && StringUtils.isNotBlank(script)){
            this.script = script;
            try {
                language = BSFManager.getLangFromFilename(controllerName);
            } catch (BSFException e) {
                e.printStackTrace();
            }
            this.controllerName = controllerName;
        }
    }

    public Object execute(Map context, Map configuration) throws Exception {
        BSFManager bsfManager = new BSFManager();

        bsfManager.declareBean("site", site, SiteContext.class);
        bsfManager.declareBean("log", LoggerFactory.getLogger(controllerName), Logger.class);

        bsfManager.exec(language, controllerName, 0, 0, script);
        return null;
    }
}
