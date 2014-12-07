/*
 * Copyright (c)2014 Florin T.Pătraşcu
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
import ca.simplegames.micro.utils.PathUtilities;

import java.util.Map;

/**
 * testing the redirect exception from a non-scripting controller
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-12)
 */
public class RedirectTestController implements Controller {
  @Override
  public void execute(MicroContext context, Map configuration) throws ControllerException {
    String resourceType = PathUtilities.extractType((String) context.get(Globals.PATH));
    context.setRedirect("redirected" + resourceType, false);
  }

}
