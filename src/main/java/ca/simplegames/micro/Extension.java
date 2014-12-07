/**
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

import java.util.Map;

/**
 * With Extensions you get to define a route, a before filter, a specific {@link ca.simplegames.micro.SiteContext} object
 * and so on.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-27 5:02 PM)
 */
public interface Extension {
  /**
   * The Micro framework calls this method at startup right after the repositories were defined.
   *
   * @param name          the name of the file this extension was loaded from, it is later used by Micro to make this
   *                      extension visible throughout the site or context. This name can be changed by the
   *                      developer at registration time.
   * @param site          the Micro "site" instance, see: {@link ca.simplegames.micro.SiteContext}
   * @param configuration a Map containing a keys and objects useful to initialize this object
   * @return self
   * @throws Exception in case something wrong happens
   */
  public Extension register(String name, SiteContext site, Map<String, Object> configuration) throws Exception;

  /**
   * @return the name of this extension
   */
  public String getName();

  /**
   * method invoked when the micro container is shutting down
   */
  public void shutdown();

}
