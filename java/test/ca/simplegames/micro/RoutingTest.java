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

import ca.simplegames.micro.utils.ResponseUtils;
import junit.framework.Assert;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.jrack.utils.Mime;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test file for everything about routing in Micro
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-25 1:38 PM)
 */
public class RoutingTest {
  Micro micro = MicroGenericTest.micro;

  @Test
  public void testMicroIsLoaded() throws Exception {
    Assert.assertNotNull("This suite requires to have a Micro environment loaded.", micro);
  }

  /**
   * test static content (binary) served from a dynamic repository, using the support of
   * {@link ca.simplegames.micro.controllers.BinaryContent}
   *
   * @throws Exception
   */
  @Test
  public void testBinaryContent() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/micro-logo.png");

    RackResponse response = micro.call(input);
    Assert.assertTrue("Can't load binary content from a dynamic repository",
        RackResponse.getBodyAsBytes(response).length == 6898);

    Assert.assertTrue("Invalid Content-Type header",
        RackResponse.getHeaders(response).get("Content-Type")
            .equalsIgnoreCase(Mime.mimeType(".png")));
  }

  @Test
  public void testParamLessRoutes() throws Exception {

    micro.getSite().getRouteManager().add(new Route("/micro/files",
        Collections.<String, Object>singletonMap("foo", "bar")) {
      @Override
      public RackResponse call(MicroContext context) throws Exception {
        context.halt();
        return ResponseUtils.standardHtml("/micro/files");
      }
    });

    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/micro/files");

    RackResponse response = micro.call(input);
    Assert.assertEquals("Invalid response", "/micro/files",
        RackResponse.getBodyAsString(response, Charset.forName("UTF-8")));
  }

  @Test
  public void testParametrizedRoutes() throws Exception {

    micro.getSite().getRouteManager().add(new Route("/micro/{name}/{version:.*}",
        Collections.<String, Object>emptyMap()) {

      @Override
      public RackResponse call(MicroContext context) throws Exception {
        String name = (String) context.getParams().get("name");
        String version = (String) context.getParams().get("version");
        Map<String, Object> microDetails = new HashMap<String, Object>();
        microDetails.put("name", name);
        microDetails.put("version", version);

        JSONObject json = new JSONObject(Collections.singletonMap("micro", microDetails));
        context.halt();
        return ResponseUtils.standardJson(json.toString());
      }
    });

    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/micro/µ/0.1.2");

    RackResponse response = micro.call(input);
    Assert.assertEquals("Invalid response", "{\"micro\":{\"name\":\"µ\",\"version\":\"0.1.2\"}}",
        RackResponse.getBodyAsString(response, Charset.forName("UTF-8")));
  }

  /**
   * testing a declared Route that serve a View
   *
   * @throws Exception
   */
  @Test
  public void testViewsOnRoute() throws Exception {

    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/view/Micro");

    RackResponse response = micro.call(input);
    Assert.assertEquals("Invalid response", "Micro",
        RackResponse.getBodyAsString(response, Charset.forName("UTF-8")));
  }
}
