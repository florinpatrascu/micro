/*
 * Copyright (c)2012 Florin T.Pătraşcu
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

package ca.simplegames.micro.viewers.markup;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;

import java.io.*;
import java.util.Map;

/**
 *
 * todo implement the support for rendering Markdown
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-30 7:19 PM)
 */
public class MarkupViewRenderer implements ViewRenderer {
    public void setRepository(Repository repository) {
    }

    public long render(MicroContext context, String path, InputStream in, OutputStream out)
            throws FileNotFoundException, ViewException {
        return 0;
    }

    public long render(MicroContext context, String path, Reader in, Writer out)
            throws FileNotFoundException, ViewException {
        return 0;
    }

    public void loadConfiguration(Map<String, Object> configuration) throws Exception {
    }
}
