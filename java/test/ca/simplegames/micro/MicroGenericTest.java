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

import ca.simplegames.micro.viewers.ViewRenderer;
import org.apache.bsf.BSFEngine;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * Micro Tester.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version 1.0
 * @since <pre>Jan 1, 2013</pre>
 */
public class MicroGenericTest {
  public static final String MICRO_IS_SHUTTING_DOWN = "micro_is_shutting_down";
  public static Micro micro;


  @BeforeClass
  public static void setup() throws Exception {
    micro = new Micro("files", null, "../../lib");
    Assert.assertNotNull(micro);
    Assert.assertNotNull("Micro 'site' initialization failed", micro.getSite().getWebInfPath());
    Assert.assertTrue("Micro is not pointing to the correct test web app",
        micro.getSite().getWebInfPath().getAbsolutePath().contains("files/WEB-INF"));
    Assert.assertTrue("Micro test web app is not properly defined",
        micro.getSite().getWebInfPath().exists());
  }


  /**
   * testing the welcome page
   * <p/>
   * Method: call(Context<String> input)
   */
  @Test
  public void testCall() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/index.html");

    RackResponse response = micro.call(input);
    Assert.assertTrue(RackResponse.getBodyAsString(response).contains("Hello"));
  }

  /**
   * test if the result is properly localized; using the rack locale
   * <p/>
   * Method: testCallWithContextLocale(Context<String> input)
   */
  @Test
  public void testCallWithContextLocale() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/index.html")
        .with(Rack.RACK_BROWSER_LOCALE, "de");

    RackResponse response = micro.call(input);
    Assert.assertTrue("Context based localization failed",
        RackResponse.getBodyAsString(response).contains("Grüß Gott!"));
  }

  /**
   * testing the support provided by the {@link ca.simplegames.micro.extensions.i18n.I18NFilter}, the
   * default localization support
   *
   * @throws Exception
   */
  @Test
  public void testI18N() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.PARAMS, Collections.singletonMap("language", "en"))
        .with(Rack.PATH_INFO, "/index.html")
        .with(Rack.REQUEST_METHOD, "GET");

    // English
    RackResponse response = micro.call(input);
    Assert.assertTrue("Expecting: Hello", RackResponse.getBodyAsString(response).contains("Hello"));

    // Romanian
    input.with(Rack.PARAMS, Collections.singletonMap("language", "ro"));
    response = micro.call(input);
    Assert.assertTrue("Expecting: Bună!", RackResponse.getBodyAsString(response).contains("Bună!"));

    // German
    input.with(Rack.PARAMS, Collections.singletonMap("language", "de"));
    response = micro.call(input);
    Assert.assertTrue("Expecting: Grüß Gott!", RackResponse.getBodyAsString(response).contains("Grüß Gott!"));

  }

  /**
   * test dynamic content produced by a controller
   *
   * @throws Exception
   */
  @Test
  public void testSomeDynamicContent() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/result.html")
        .with(Rack.PARAMS, Collections.singletonMap("exp", URLEncoder.encode("2+2", Globals.UTF8)));

    RackResponse response = micro.call(input);
    Assert.assertTrue("Can't use the request parameters",
        RackResponse.getBodyAsString(response).contains("=4"));
  }

  /**
   * test dynamic content produced by a controller
   *
   * @throws Exception
   */
  @Test
  public void testTemplates() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/index.txt");

    RackResponse response = micro.call(input);
    Assert.assertTrue("Wrong template",
        RackResponse.getBodyAsString(response).contains("TXT DEFAULT TEMPLATE, content: Some text."));

    input.with(Rack.PATH_INFO, "/another_text.txt");
    response = micro.call(input);

    Assert.assertTrue("Wrong Alternate template",
        RackResponse.getBodyAsString(response)
            .contains("TXT ALTERNATE TEMPLATE, content: This is just another text."));
  }

  /**
   * test the availability of the BSF Engine via "site"
   *
   * @throws Exception
   */
  @Test
  public void testBSFEngineIntegration() throws Exception {
    Context context = new MicroContext();


    BSFEngine engine = micro.getSite().getBSFEngine("beanshell",
        (MicroContext) context.with("unu", 1),
        Collections.singletonMap("foo", "bar"));

    engine.exec("complexCalculus", 0, 0,
        "context.with(\"one\", context.get(\"unu\") * 1);" +
            "log.info(\"One is: \" + context.get(\"one\"));"); // :P
    Assert.assertEquals("BSFEngine failure", 1, context.get("one"));
  }

  /**
   * testing if the Markdown support works well
   *
   * @throws Exception
   */
  @Test
  public void testMarkdownViewer() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/index.md");

    RackResponse response = micro.call(input);
    Assert.assertTrue(RackResponse.getBodyAsString(response)
        .contains("<h3>Markdown</h3><p>This is a simple markdown document</p>"));
  }

  @Test
  public void markdownContextEval() throws Exception{
    ViewRenderer markdown = micro.getSite().getTemplateEnginesManager().getEngine("markdown");
    MicroContext context = new MicroContext<String>();
    Assert.assertTrue("Not the expected Markdown result",
        markdown.evaluate(context, "**Test**").equals("<p><strong>Test</strong></p>"));
  }

  @Test
  public void testScriptingControllersRedirectToUrl() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/redir.html");

    RackResponse response = micro.call(input);
    Assert.assertTrue(response.getStatus() == HttpServletResponse.SC_SEE_OTHER);
    String location = response.getHeaders().get("Location");
    Assert.assertTrue("Invalid redirection control", location.contains("redirected.html"));
    Assert.assertTrue("The response must be empty", RackResponse.getBodyAsString(response).isEmpty());
  }


  @Test
  public void testRoutesRedirectToUrl() throws Exception {
    Context<String> input = new MapContext<String>()
        .with(Rack.REQUEST_METHOD, "GET")
        .with(Rack.PATH_INFO, "/redir/me");

    RackResponse response = micro.call(input);
    Assert.assertTrue(response.getStatus() == HttpServletResponse.SC_SEE_OTHER);
    String location = response.getHeaders().get("Location");
    Assert.assertTrue(location.contains("redirected")); // no extension in this case
    Assert.assertTrue(RackResponse.getBodyAsString(response).isEmpty());
  }

  /**
   * testing if the optional shutdown Controller is properly executed *when* available
   * @throws Exception
   */
  @Test
  public void testShutdown() throws Exception {
    Assert.assertNull("The site must be clean, in preparation for this test",
        micro.getSite().getObject(MICRO_IS_SHUTTING_DOWN));

    micro.shutdown();

    Assert.assertNotNull("The test 'shutdown.bsh' controller was not triggered",
        micro.getSite().getObject(MICRO_IS_SHUTTING_DOWN));
  }

  /**
   * this is an important test to check if the multiple actions can safely use
   * their own configuration object and use it in a shared context
   *
   * @throws Exception
   */
  @Test
  public void testCloseableBSF() throws Exception {
    final SiteContext site = micro.getSite();
    final String canonicalName = TestController.class.getCanonicalName();

    Context context = new MicroContext<String>()
        .with(Globals.LOG, site.getLog())
        .with(Globals.SITE, site);

    site.getControllerManager().execute("Foo.bsh", (MicroContext) context, Collections.singletonMap("foo", "bar"));
    Assert.assertTrue("Foo is not Bar, broken configuration",
        ((String) context.get("foo")).equalsIgnoreCase("bar"));

    site.getControllerManager().execute("Bat.bsh", (MicroContext) context, Collections.singletonMap("Bat", "Man"));
    Assert.assertTrue("Bat is not the Man, c'mon man",
        ((String) context.get("Bat")).equalsIgnoreCase("Man"));

    site.getControllerManager().execute("ca.simplegames.micro.TestController",
        (MicroContext) context, Collections.singletonMap("class", canonicalName));
    Assert.assertTrue("Can't execute Java class",
        ((String) context.get("class")).equalsIgnoreCase(canonicalName));
  }
}
