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
    Repository repository = null;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public long render(MicroContext context, String path, Reader in, Writer out)
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

    public long render(MicroContext context, String path, InputStream in, OutputStream out)
            throws FileNotFoundException, ViewException {
        return render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));
    }
}