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

package ca.simplegames.micro.helpers;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.MicroContext;
import org.jrack.utils.ClassUtilities;

import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-17 7:05 PM)
 */
public class HelperWrapper {
    private Class klass;
    private String name;
    private Map<String, Object> options;
    // todo: private boolean isSingleton = false;

    public HelperWrapper(String name, Map<String, Object> configuration) throws ClassNotFoundException {
        this.name = name;
        this.options = (Map<String, Object>) configuration.get(Globals.OPTIONS);
        klass = ClassUtilities.loadClass((String) configuration.get("class"));
    }

    public Helper getInstance(MicroContext context) throws Exception {
        Helper helper = (Helper) klass.newInstance();
        helper.register(context, options);
        return helper;
    }

    public String getName() {
        return name;
    }
}
