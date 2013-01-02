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
    @SuppressWarnings("unchecked")
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