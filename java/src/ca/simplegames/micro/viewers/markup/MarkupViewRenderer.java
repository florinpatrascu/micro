package ca.simplegames.micro.viewers.markup;

import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.IO;
import ca.simplegames.micro.viewers.ViewException;
import ca.simplegames.micro.viewers.ViewRenderer;
import org.pegdown.PegDownProcessor;

import java.io.*;
import java.util.Map;

/**
 * todo implement the support for rendering Markdown
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
     * @param context    The RequestContext
     * @param in         The Reader to read view template from
     * @param out        The Writer to write the rendered view
     * @return
     * @throws FileNotFoundException
     * @throws ViewException
     */
    public synchronized long render(String path, Repository repository, MicroContext context, Reader in, Writer out)
            throws FileNotFoundException, ViewException {

        if (repository != null && out != null) {
            try {

                String source = repository.read(path);
                String html = pegDownProcessor.markdownToHtml(source);
                return IO.copy(new StringReader(html), out);

            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(String.format("%s not found.", path));
            } catch (Exception e) {
                throw new ViewException(e);
            }
        }
        return 0;
    }

    public void loadConfiguration(Map<String, Object> configuration) throws Exception {
    }

    public long render(String path, Repository repository, MicroContext context, InputStream in, OutputStream out)
            throws FileNotFoundException, ViewException {
        return render(path, repository, context, new InputStreamReader(in), new OutputStreamWriter(out));
    }

    @Override
    public String getName() {
        return name;
    }
}