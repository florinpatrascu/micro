/*
 * Copyright (c) 2013 Florin T.PATRASCU
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

/**
 * An example of Helper implementation
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-15 11:13 PM)
 */
public class HelloHelper extends Helper {

    public String getHello() {
        String name = Globals.EMPTY_STRING;
        if (options != null) {
            name = (String) options.get("name");
        }
        return String.format("Hello %s!", name);
    }

    public String getName() {
        return (String) options.get("name");
    }
}