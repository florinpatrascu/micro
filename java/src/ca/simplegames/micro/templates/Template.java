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

package ca.simplegames.micro.templates;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import org.jrack.Context;
import org.jrack.context.MapContext;

import java.util.Map;

/**
 * Extend this class if you want to write a custom renderer
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 2:10 PM)
 */
public abstract class Template {
    private SiteContext site;
    private MicroContext context;

    /**
     * render a resource
     * @param engine
     * @param path
     * @param options
     * @return
     * @throws Exception
     */
    public abstract String render(String engine, String path, MapContext<String> options) throws Exception;
    public abstract String render(String path, MapContext<String> options) throws Exception;
}
