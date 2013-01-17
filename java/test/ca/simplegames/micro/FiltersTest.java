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
import org.jrack.utils.Mime;
import org.junit.Test;

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
}
