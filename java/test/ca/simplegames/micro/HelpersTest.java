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

import ca.simplegames.micro.helpers.HelloHelper;
import ca.simplegames.micro.utils.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-17 8:44 PM)
 */
public class HelpersTest {
    static MicroContext<String> context = new MicroContext<String>();
    Micro micro = MicroGenericTest.micro;

    @Test
    public void testDemoHelper() throws Exception {

        HelloHelper helper = (HelloHelper) micro.getSite().getHelperManager().findHelper("hello").getInstance(context);
        Assert.isTrue(helper.getName().equals("Huston"));
        Assert.isTrue(helper.getHello("Mona").equals("Hello Mona!"));
    }
}
