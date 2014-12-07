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

package ca.simplegames.micro;

import org.apache.commons.lang3.StringUtils;
import org.jrack.Context;
import org.jrack.JRack;
import org.jrack.RackResponse;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.File;

/**
 * This {@link JRack} is the main entry point for requests to the Micro framework.
 * It should be configured to handle all the requests for its context.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-17 4:21 PM)
 */
public class MicroFilter extends JRack {
    private Micro micro;

    @Override
    public Object init(Object config) throws Exception {
        FilterConfig filterConfig = (FilterConfig) config;
        ServletContext servletContext = filterConfig.getServletContext();

        micro = new Micro(servletContext.getRealPath("/"), servletContext,
                StringUtils.defaultString(((FilterConfig) config).getInitParameter("userClassPaths")));

        return this;
    }

    /**
     * main entry point, this is where Micro is processing all the incoming requests
     *
     * @param input the Rack input object
     * @return a Rack response see: {@link RackResponse}
     */
    public RackResponse call(Context<String> input) throws Exception {
        return micro.call(input);
    }
}
