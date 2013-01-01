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

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * see:
 * http://stackoverflow.com/questions/3089151/specifying-an-order-to-junit-4-tests-at-the-method-level-not-class-level
 */
public class OrderedRunner extends BlockJUnit4ClassRunner {

    public OrderedRunner(Class klass) throws org.junit.runners.model.InitializationError {
        super(klass);
    }

    @Override
    protected List computeTestMethods() {
        List list = super.computeTestMethods();
        List copy = new ArrayList(list);
        Collections.sort(copy, new Comparator() {

            public int compare(Object o1, Object o2) {
                FrameworkMethod a = (FrameworkMethod) o1;
                FrameworkMethod b = (FrameworkMethod) o2;
                return a.getName().compareTo(b.getName());
            }
        });
        return copy;
    }
}