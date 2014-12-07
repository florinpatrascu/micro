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

import ca.simplegames.micro.repositories.Repository;
import freemarker.cache.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Freemarker ... I don't love you and your template caching ... is not the best. This class is because
 * we want to help the poor souls still using you. Yeah, I know you're better than Velocity, hat down.
 * <p/>
 * Please make Templates serializable and we'll have a better relationship.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-13 6:06 PM)
 */
public class MicroTemplateLoader implements TemplateLoader {
    private static final Logger log = LoggerFactory.getLogger(MicroTemplateLoader.class);
    Repository repository;

    public MicroTemplateLoader(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        try {
            return repository.read(name);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        return repository.getLastModified((String) templateSource);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        StringReader stringReader = new StringReader((String) templateSource);

        if (encoding != null) {
            try {
                stringReader = new StringReader(new String(((String) templateSource).getBytes(), encoding));
            } catch (UnsupportedEncodingException e) {
                log.warn("Unsupported encoding " + encoding + "; using default encoding");
            }
        }

        return stringReader;

    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // not needed, we're using the StringReader
        // example: http://stackoverflow.com/questions/6122013/should-i-close-a-stringreader
    }
}
