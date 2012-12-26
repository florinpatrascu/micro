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

package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.cache.MicroCache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 6:42 PM)
 */
public class FSRepository extends Repository {
    protected FSRepository(String name, MicroCache cache, SiteContext site, String path, String configPathName) {
        super(name, cache, site, path, configPathName);
    }

    @Override
    public InputStream getInputStream(String name) {
        try {
            return new ByteArrayInputStream(read(name).getBytes(Globals.UTF8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
