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

/**
 * A Helper will be invoked every time Micro is receiving a call from Rack
 * The Helper can also be mapped on a specific route.
 * <p/>
 * There are two types of Helpers:
 * - before: acting before any other Micro Controllers can act
 * - after: acting after Micro finished processing the response
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013.01.13 11:17 PM)
 */
public interface Helper {

    /**
     * @return true if this helper must be invoked before the call
     */
    public boolean isBefore();

    /**
     * @return true if this helper must be invoked after the call
     */
    public boolean isAfter();

    /**
     * If there is a path specified, the helper will execute only if
     * the request matches the path
     *
     * @return the path this helper is answering on
     */
    public String getPath();

    /**
     * @return the name of the controller used by this Helper
     */
    public String getController();

    /**
     * executed on every request
     *
     * @param context the Micro context
     * @throws Exception
     */
    public void call(MicroContext context) throws Exception;
}
