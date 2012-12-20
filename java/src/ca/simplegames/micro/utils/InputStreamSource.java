package ca.simplegames.micro.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple interface for objects that are sources for an {@link java.io.InputStream}.
 * <p/>
 * <p>This is the base interface for Spring's more extensive {@link Resource} interface.
 *
 * @author Juergen Hoeller
 * @see java.io.InputStream
 * @see Resource
 * @since 20.01.2004
 */
public interface InputStreamSource {

    /**
     * Return an {@link java.io.InputStream}.
     * <p>It is expected that each call creates a <i>fresh</i> stream.
     * <p>This requirement is particularly important when you consider an API such
     * as JavaMail, which needs to be able to read the stream multiple times when
     * creating mail attachments. For such a use case, it is <i>required</i>
     * that each <code>getInputStream()</code> call returns a fresh stream.
     *
     * @throws java.io.IOException if the stream could not be opened
     */
    InputStream getInputStream() throws IOException;

}