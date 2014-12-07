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

package ca.simplegames.micro.utils;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is simulating the 'params' from Rack, collecting all the parameters from the
 * Request and from the Session; **if** the Session is enabled!
 * <p/>
 * It will also receive the Route parameters and the Request attributes.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-03-03 4:12 PM)
 */
public class ParamsFactory {

    /**
     * @param context for getting access to the Request elements
     * @return a new Map containing the captured parameters, names and values
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> capture(MicroContext context) {
        Map<String, Object> params = null;

        if (context != null) {
            params = (Map<String, Object>) context.get(Globals.PARAMS);

            final HttpServletRequest request = context.getRequest();

            if (request != null) {
                // request Parameters
                Enumeration names = request.getParameterNames();
                if (names.hasMoreElements() && params == null) {
                    params = new HashMap<String, Object>();
                }

                while (names.hasMoreElements()) {
                    Object name = names.nextElement();
                    String[] values = request.getParameterValues(String.valueOf(name));
                    Object value = values != null && values.length == 1 ? values[0] : values;
                    if (value != null) {
                        params.put(String.valueOf(name), value.toString());
                    }
                }

                // request Attributes
                names = request.getAttributeNames();
                if (names.hasMoreElements() && params == null) {
                    params = new HashMap<String, Object>();
                }
                while (names.hasMoreElements()) {
                    Object name = names.nextElement();
                    Object value = request.getAttribute(String.valueOf(name));
                    if (value != null) {
                        params.put(String.valueOf(name), value.toString());
                    }
                }

                // session Attributes, if the Session is enabled
                final HttpSession session = request.getSession(false);
                if (session != null) {
                    names = session.getAttributeNames();
                    if (names.hasMoreElements() && params == null) {
                        params = new HashMap<String, Object>();
                    }
                    while (names.hasMoreElements()) {
                        Object name = names.nextElement();
                        Object value = session.getAttribute(String.valueOf(name));
                        if (value != null) {
                            params.put(String.valueOf(name), value.toString());
                        }
                    }
                }
            }
        }
        return params == null ? Collections.EMPTY_MAP : params;
    }
}
