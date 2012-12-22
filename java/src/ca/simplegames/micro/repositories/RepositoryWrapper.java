package ca.simplegames.micro.repositories;

import ca.simplegames.micro.MicroContext;

import java.io.FileNotFoundException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 7:47 PM)
 */
public class RepositoryWrapper {
    private Repository repository;
    private MicroContext context;

    public RepositoryWrapper(Repository repository, MicroContext context) {
        this.repository = repository;
        this.context = context;
    }

    public String get(String path) {
        try {

            StringWriter writer = new StringWriter();
            repository.getSite().getControllerManager().executeForPath(repository, path, context);
            repository.getRenderer().render(context, path, null, writer);
            return writer.toString();

        } catch (Exception e) {
            String err = e.getMessage();
            repository.getLog().error(err);
            return String.format("Repository::%s %s",
                    repository.getName().toUpperCase(), e.getMessage().substring(e.getMessage().lastIndexOf(":")+1));
        }
    }
}
