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

package ca.simplegames.micro.helpers;

import bsh.StringUtil;
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.PathUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:33 PM)
 */
public class HelperManager {
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final String BEFORE = "before";
    public static final String AFTER = "after";

    private SiteContext site;
    private List<Helper> helpers = new ArrayList<Helper>();
    private Map<String, Helper> helpersMap = new HashMap<String, Helper>();
    private List<Helper> beforeHelpers = new ArrayList<Helper>();
    private List<Helper> afterHelpers = new ArrayList<Helper>();
    private List<Helper> pathHelpers = new ArrayList<Helper>();

    public HelperManager(SiteContext site, Object config) {
        this.site = site;
        List<Map<String, Object>> helpersConfig = (List<Map<String, Object>>) config;
        if (!CollectionUtils.isEmpty(helpersConfig)) {
            //load helpers from config
            log.warn("TODO: implement me - ca.simplegames.micro.helpers.HelperManager#HelperManager");
            for (Map<String, Object> helperConfig : helpersConfig) {
                for (Map.Entry<String, Object> entry : helperConfig.entrySet()) {
                    try {
                        String helperType = entry.getKey();
                        Map<String, Object> helperDefinition = (Map<String, Object>) entry.getValue();

                    } catch (Exception e) {
                        log.error("cannot load the following helper: "+helperConfig);
                        e.printStackTrace();
                    }


                }
            }

        }
    }

    public Helper addHelper(Map<String, Object> model) {
        if (model != null && !model.isEmpty()) {
            Helper helper = null;

            return addHelper(helper);
        } else {
            return null;
        }
    }

    public Helper addHelper(Helper helper) {
        if (helper != null) {
            if (helper.isBefore()) {
                    beforeHelpers.add(helper);
            } else if (helper.isAfter()) {
                afterHelpers.add(helper);
            } else if(StringUtils.isNotBlank(helper.getPath())) {
                pathHelpers.add(helper);
            } else {
                helpers.add(helper);
            }
            helpersMap.put(helper.getName().toLowerCase(), helper);
        }
        return helper;
    }

    public List<Helper> getHelpers() {
        return helpers;
    }

    public Map<String, Helper> getHelpersMap() {
        return helpersMap;
    }

    public List<Helper> getBeforeHelpers() {
        return beforeHelpers;
    }

    public List<Helper> getAfterHelpers() {
        return afterHelpers;
    }

    public List<Helper> getPathHelpers(String inputPath, MicroContext context) {
        List<Helper> matchingHelpers = new ArrayList<Helper>();

        for(Helper helper: pathHelpers){
            UriTemplateMatcher templateMatcher = PathUtilities.routeMatch(inputPath, helper.getPath());
            if(templateMatcher!= null){
                context.with(Globals.PARAMETERS, templateMatcher.getVariables());
                matchingHelpers.add(helper);
            }
        }

        return matchingHelpers;
    }
}
