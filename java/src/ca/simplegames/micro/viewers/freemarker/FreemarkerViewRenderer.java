/*
 * Copyright (c) 2013 the original author or authors.
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

package ca.simplegames.micro.viewers.freemarker;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * View renderer using Freemarker for views
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-13 1:48 PM)
 */
public class FreemarkerViewRenderer implements ViewRenderer {
    private static final Logger log = LoggerFactory.getLogger(FreemarkerViewRenderer.class);
    private Configuration fmConfig = new Configuration();
    protected String name = "freemarker";

    @Override
    public void loadConfiguration(Map<String, Object> configuration) throws Exception {
        // todo: implement more, allow the user to initialize FM from Micro's config file
        fmConfig.setLocalizedLookup(false);
    }

    @Override
    public long render(String path, Repository repository, MicroContext context, Reader in, Writer out) throws FileNotFoundException, ViewException {

        final String fileNotFoundMessage = String.format("%s not found.", path);
        try {
            if (in == null) {
                throw new FileNotFoundException(fileNotFoundMessage);
            }

            StringWriter writer = new StringWriter();
            // todo: implement a cache, maybe?!
            Template template = new Template(path, in, fmConfig);
            template.process(context, out);
            return IO.copy(new StringReader(writer.toString()), out);

        } catch (TemplateException e) {
            throw new ViewException(e);
        } catch (IOException e) {
            throw new FileNotFoundException(fileNotFoundMessage);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
