package ca.simplegames.micro.viewers.markup;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.RedirectException;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.pegdown.PegDownProcessor;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

/**
 * Support for rendering Markdown documents
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-30 7:19 PM)
 */
public class MarkupViewRenderer implements ViewRenderer {
    PegDownProcessor pegDownProcessor = new PegDownProcessor();
    protected String name = "markdown";

    /**
     * this Viewer requires synchronization, otherwise the PEG processor is spitting errors under stress
     *
     * @param path       The path to the template
     * @param repository the repository used for extracting the resource specified by path
     * @param context    The RequestContext (ignored in this implementation)
     * @param out        The Writer to write the rendered view
     * @return
     * @throws FileNotFoundException
     * @throws ViewException
     */
    public synchronized long render(String path, Repository repository, MicroContext context, Writer out)
            throws FileNotFoundException, ViewException {

        if (repository != null && out != null) {
            try {

                String source = repository.read(path);
                String html = pegDownProcessor.markdownToHtml(source);
                return IO.copy(new StringReader(html), out);

            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(String.format("%s not found.", path));
            } catch (Exception e) {
                if (e instanceof RedirectException || e.getCause() instanceof RedirectException) {
                    throw new RedirectException();
                } else {
                    throw new ViewException(e.getMessage()); // generic??
                }
            }
        }
        return 0;    }

    @Override
    public String evaluate(MicroContext context, String text) throws ViewException {
        return pegDownProcessor.markdownToHtml(text);
    }

    public void loadConfiguration(SiteContext site, Map<String, Object> configuration) throws Exception {
    }

    @Override
    public String getName() {
        return name;
    }
}
