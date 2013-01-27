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

import junit.framework.Assert;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.jrack.context.MapContext;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

/**
 * Suite dedicated to testing the Filters support
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-16 8:54 PM)
 */
public class FiltersTest {
    Micro micro = MicroGenericTest.micro;

    @Test
    public void testMicroIsLoaded() throws Exception {
        Assert.assertNotNull("This suite requires to have a Micro environment loaded.", micro);
    }


    @Test
    public void testBeforeFilters() throws Exception {
        Context<String> input = new MapContext<String>()
                .with(Rack.PATH_INFO, "/private/repositories/micro")
                .with(Rack.REQUEST_METHOD, "GET");

        RackResponse response = micro.call(input);
        Assert.assertTrue(String.format("Expected a successful response status, we got: %d, instead",
                response.getStatus()), response.getStatus() == HttpServletResponse.SC_OK);
        Assert.assertTrue("Expecting a set of user roles in the current input",
                RackResponse.getBodyAsString(response).contains("userRoles"));
    }

    @Test
    public void testAfterFilters() throws Exception {
        Context<String> input = new MapContext<String>()
                .with(Rack.PATH_INFO, "/download/")
                .with(Rack.REQUEST_METHOD, "GET");

        RackResponse response = micro.call(input);
        Assert.assertTrue(String.format("Expected a successful response status, we got: %d, instead",
                response.getStatus()), response.getStatus() == HttpServletResponse.SC_OK);

        Assert.assertTrue("Download activity must be logged",
                ((MicroContext) input.getObject(Globals.CONTEXT))
                        .get("downloadActivity").equals("logged"));
    }
}
