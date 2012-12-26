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

import ca.simplegames.micro.MicroContext;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:46 PM)
 */
public interface Controller extends Serializable {
    /**
     * Execute the action using the given context and the optional configuration.
     *
     * @param context       a Map containing input parameters
     * @param configuration an action specific configuration. Can be null
     * @return an Object, optional
     * @throws Exception if any problems
     */
    public Object execute(MicroContext context, Map configuration) throws Exception;
}
