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

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Micro;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
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
    protected String name = "freemarker";

    @Override
    public void loadConfiguration(SiteContext site, Map<String, Object> configuration) throws Exception {
    }

    @Override
    public long render(String path, Repository repository, MicroContext context, Writer out)
            throws FileNotFoundException, ViewException {

        try {

            Configuration fmConfig = new Configuration();
            fmConfig.setTemplateLoader(new MicroTemplateLoader(repository));
            fmConfig.setLocalizedLookup(false);
            fmConfig.setWhitespaceStripping(context.getSiteContext().isProduction());
            // NOT! fmConfig.setClassForTemplateLoading(Micro.class, Globals.EMPTY_STRING);

            StringWriter writer = new StringWriter();

            Template template = fmConfig.getTemplate(path, Globals.UTF8);
            template.process(context, out);

            return IO.copy(new StringReader(writer.toString()), out);

        } catch (TemplateException e) {
            throw new ViewException(e);
        } catch (IOException e) {
            throw new FileNotFoundException(String.format("%s not found.", path));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ViewException(e.getMessage()); // generic??
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
