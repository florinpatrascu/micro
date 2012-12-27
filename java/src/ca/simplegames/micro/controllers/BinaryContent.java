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
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.PathUtilities;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.utils.Mime;

import java.io.File;
import java.util.Map;

/**
 * Basic controller that can be used as a default binary content streamer. Useful in those
 * scenarios where a developer would like to serve data from a private (dynamic) repository
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 7:15 PM)
 */
public class BinaryContent implements Controller {

    public static final String CONFIG_ELEMENT_MIME_TYPES = "mime_types";

    public Object execute(MicroContext context, Map configuration) throws Exception {
        SiteContext site = context.getSiteContext();

        File content = site.getRepositoryManager().getDefaultRepository()
                .pathToFile((String) context.get(Globals.PATH_INFO));

        RackResponse rackResponse = context.getRackResponse();

        final String fileType = PathUtilities.extractType(content.getAbsolutePath());
        rackResponse.withBody(content)
                .withContentLength(content.length());

        if (configuration != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> customMimeTypes = (Map<String, String>) configuration.get(CONFIG_ELEMENT_MIME_TYPES);
            if (!CollectionUtils.isEmpty(customMimeTypes) && customMimeTypes.containsKey(fileType)) {
                rackResponse.withContentType(customMimeTypes.get(fileType));
            }
        } else {
            rackResponse.withHeader(Rack.HTTP_CONTENT_TYPE, Mime.mimeType(fileType));
        }
        context.halt();
        return rackResponse; // in case anybody wants it?
    }
}
