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

import org.jrack.RackResponse;
import org.jrack.utils.Mime;

import javax.servlet.http.HttpServletResponse;

/**
 * a bunch of methods to help rendering various contents.
 * Will deprecate the {@link org.jrack.RackResponseUtils}
 * <p/>
 * - work in progress -
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-03-15 6:00 PM)
 */
public class ResponseUtils {

    public static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    public static final String JSON_TYPE = ".json";

    public static RackResponse standardHtml(String body) {
        return new RackResponse(HttpServletResponse.SC_OK)
                .withContentType(CONTENT_TYPE_TEXT_HTML)
                .withBody(body);
    }

    public static RackResponse standardJson(String body) {
        return new RackResponse(HttpServletResponse.SC_OK)
                .withContentType(Mime.mimeType(JSON_TYPE))
                .withBody(body);
    }
}
