package ca.simplegames.micro;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

//@RunWith(value = Suite.class)
//@Suite.SuiteClasses(value = {MicroTest.class})
//@RunWith(OrderedRunner.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)

/**
 * Micro Tester.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @version 1.0
 * @since <pre>Jan 1, 2013</pre>
 */
public class MicroTest {
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
     * Method: call(Context<String> input)
     */
    @Test
    public void testCall() throws Exception {
        Assert.fail("implement me");
    }

}
