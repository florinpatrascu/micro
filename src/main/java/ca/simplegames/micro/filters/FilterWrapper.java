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

package ca.simplegames.micro.filters;

import ca.simplegames.micro.Filter;
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import ca.simplegames.micro.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.jrack.Context;
import org.jrack.JRack;
import org.jrack.Rack;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a generic helper that can be used to create a helper instance from a simple Filter definition
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 9:41 PM)
 */
public class FilterWrapper implements Filter {
    private Boolean before;
    private Boolean after;
    private String path;
    private List<Map<String, Object>> controllers = null;
    private JaxRsUriTemplateProcessor processor;

    @SuppressWarnings("unchecked")
    public FilterWrapper(Map<String, Object> config, String type) {
        if (type != null) {
            before = type.equalsIgnoreCase(FilterManager.BEFORE);
            after = type.equalsIgnoreCase(FilterManager.AFTER);
        }
        path = (String) config.get("path");

        if (config.get(Globals.CONTROLLERS) != null) {
            controllers = (List<Map<String, Object>>) config.get(Globals.CONTROLLERS);
        } else if (config.get(Globals.CONTROLLER) != null) {
            Map<String, Object> controller = new HashMap<String, Object>();
            controller.put(Globals.NAME, config.get(Globals.CONTROLLER));
            controller.put(Globals.OPTIONS, config.get(Globals.OPTIONS));
            controllers = Collections.singletonList(controller);
        }

    }

    public boolean isBefore() {
        return before != null && before;
    }

    public boolean isAfter() {
        return after != null && after;
    }

    @SuppressWarnings("unchecked")
    public void call(MicroContext context) throws ControllerNotFoundException, ControllerException {
        if (context != null) {
            // check if there is a path defined for the filter and verify is matching the request
            if (path != null) {
                Context<String> input = (Context<String>) context.get(Globals.RACK_INPUT);
                String requestPath = input.get(JRack.PATH_INFO);
                if (StringUtils.isBlank(requestPath)) {
                    requestPath = input.get(Rack.SCRIPT_NAME);
                }

                UriTemplateMatcher templateMatcher = match(requestPath, path);

                if (templateMatcher != null) {
                    try {
                        MultivaluedMap<String, String> routeParams = templateMatcher.getVariables(true);
                        Map<String, String[]> params = (Map<String, String[]>) context.get(Globals.PARAMS);

                        if (CollectionUtils.isEmpty(params)) {
                            params = new HashMap<String, String[]>();
                            context.with(Globals.PARAMS, params);
                        }

                        for (Map.Entry<String, List<String>> param : routeParams.entrySet()) {
                            params.put(param.getKey(),
                                    param.getValue().toArray(new String[param.getValue().size()]));
                        }

                        executeControllers(context);
                    } catch (IllegalStateException e) {
                        context.getLog().error(e.getMessage()); //todo: improve the error message
                    }
                }
            } else {
                executeControllers(context);
            }
        }
    }

    private void executeControllers(MicroContext context) throws ControllerNotFoundException, ControllerException {
        if (!CollectionUtils.isEmpty(controllers)) {
            SiteContext site = context.getSiteContext();
            for (Map<String, Object> controllerMap : controllers) {
                site.getControllerManager().execute((String) controllerMap.get(Globals.NAME),
                        context, (Map) controllerMap.get(Globals.OPTIONS));
                if (context.isHalt()) {
                    break;
                }
            }
        }

    }

    /**
     * Match a filter path. Temporarily redundant!
     *
     * @param requestPath The request path submitted by the client
     * @param testPath    The match path
     */
    private UriTemplateMatcher match(String requestPath, String testPath) {

        if (processor == null) {
            processor = new JaxRsUriTemplateProcessor(testPath);
        }

        UriTemplateMatcher matcher = processor.matcher();
        return matcher.matches(requestPath) ? matcher : null;
    }
}
