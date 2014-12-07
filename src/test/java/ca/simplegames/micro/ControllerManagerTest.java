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
import ca.simplegames.micro.controllers.ControllerManager;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import junit.framework.Assert;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.context.MapContext;
import org.junit.Test;

/**
 * testing various aspects of the ControllerManager
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-03-28)
 */
public class ControllerManagerTest {
  Micro micro = MicroGenericTest.micro;

  @Test
  public void testMicroIsLoaded() throws Exception {
    Assert.assertNotNull("This suite requires to have a Micro environment loaded.", micro);
    Assert.assertNotNull("The ControllerManager was not properly initialized.",
        micro.getSite().getControllerManager());

  }

  /**
   * try to execute a controller that cannot be found
   */
  @Test(expected = ControllerNotFoundException.class)
  public void testMissingController() throws Exception {
    MicroContext context = new MicroContext<String>();
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/micro-logo.png");

    ControllerManager cm = micro.getSite().getControllerManager();
    cm.execute("allien.Controller", context);
  }

  /**
   * try to execute a controller that will generate an internal exception
   */
  @Test(expected = ControllerException.class)
  public void testExceptionalController() throws Exception {

    ControllerManager cm = micro.getSite().getControllerManager();
    cm.execute("DivideByZero.bsh", new MicroContext<String>());
  }
}
