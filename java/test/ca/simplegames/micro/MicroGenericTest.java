package ca.simplegames.micro;

import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.Collections;

//@RunWith(value = Suite.class)
//@Suite.SuiteClasses(value = {MicroGenericTest.class})
//@RunWith(OrderedRunner.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)

/**
 * Micro Tester.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version 1.0
 * @since <pre>Jan 1, 2013</pre>
 */
public class MicroGenericTest {
    static Micro micro;


    @BeforeClass
    public static void setup() throws Exception {
        micro = new Micro("files", null, "../../lib", "index.html");
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
        Assert.assertTrue(RackResponse.getBodyAsString(response).contains("hello"));
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
                .with(Globals.PARAMS, Collections.singletonMap("exp",
                        new String[]{URLEncoder.encode("2+2", Globals.UTF8)}));


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
}
