/*
 * Copyright (c)2013 Florin T.Pătraşcu
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

import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * testing everything in relation to the Views
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-11)
 */
public class ViewsTest {
    static MicroContext<String> context = new MicroContext<String>();
    Micro micro = MicroGenericTest.micro;

    @Before
    public void testMicroIsLoaded() throws Exception {
        Assert.assertNotNull("The View suite requires the Micro environment.", micro);
    }

    @Test
    public void testWrappedControllers() throws Exception {
        Context<String> input = new MapContext<String>()
                .with(Rack.REQUEST_METHOD, "GET")
                .with(Rack.PATH_INFO, "/view_with_wrappers.html");

        RackResponse response = micro.call(input);
        String body = RackResponse.getBodyAsString(response);

        Assert.assertTrue(body.contains("Before: true"));
        Assert.assertTrue(body.contains("Wrapped with: love"));
        Assert.assertTrue(body.contains("After: true"));

        input.with(Rack.PATH_INFO, "/view_with_wrappers.html")
                .with(Rack.PARAMS, Collections.singletonMap("_why", new String[]{"o_O"}));

        response = micro.call(input);
        body = RackResponse.getBodyAsString(response);

        Assert.assertTrue(body.contains("Before: true"));
        Assert.assertTrue("Errare humanum est!",
                body.contains("Errare humanum est, perseverare diabolicum!"));
        Assert.assertTrue(!body.contains("Wrapped with: love"));
        Assert.assertTrue(body.contains("After: true"));
    }
}
