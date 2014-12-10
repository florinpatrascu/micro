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

package ca.simplegames.micro;

import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerNotFoundException;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Map;

/**
 * The Controller interface.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 3:46 PM)
 */
public interface Controller extends Serializable {
    /**
     * Execute the controller using the given context and an optional configuration.
     *
     * @param context       a Map containing input parameters, shared with other controllers
     *                      or views. It is created for every web request and it relies on
     *                      {@link MicroContext}
     * @param configuration a Map containing various configuration options. Can be null
     * @throws ControllerException if any problems
     */
    public void execute(MicroContext context, Map configuration) throws ControllerException;
}
