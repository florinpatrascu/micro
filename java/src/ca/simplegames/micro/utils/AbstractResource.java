/*
 * Copyright (c) 2009. Florin T.PATRASCU
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

package ca.simplegames.micro.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Convenience base class for {@link Resource} implementations,
 * pre-implementing typical behavior.
 * <p/>
 * <p>The "exists" method will check whether a File or InputStream can
 * be opened; "isOpen" will always return false; "getURL" and "getFile"
 * throw an exception; and "toString" will return the description.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 */
public abstract class AbstractResource implements Resource {

    /**
     * This implementation checks whether a File can be opened,
     * falling back to whether an InputStream can be opened.
     * This will cover both directories and content resources.
     */
    public boolean exists() {
        // Try file existence: can we find the file in the file system?
        try {
            return getFile().exists();
        }
        catch (IOException ex) {
            // Fall back to stream existence: can we open the stream?
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            }
            catch (Throwable isEx) {
                return false;
            }
        }
    }

    /**
     * This implementation always returns <code>false</code>.
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that the resource cannot be resolved to a URL.
     */
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    /**
     * This implementation builds a URI based on the URL returned
     * by {@link #getURL()}.
     */
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        }
        catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that the resource cannot be resolved to an absolute file path.
     */
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    /**
     * This implementation throws a FileNotFoundException, assuming
     * that relative resources cannot be created for this resource.
     */
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    /**
     * This implementation always throws IllegalStateException,
     * assuming that the resource does not carry a filename.
     */
    public String getFilename() throws IllegalStateException {
        throw new IllegalStateException(getDescription() + " does not carry a filename");
    }


    /**
     * This implementation returns the description of this resource.
     *
     * @see #getDescription()
     */
    public String toString() {
        return getDescription();
    }

    /**
     * This implementation compares description strings.
     *
     * @see #getDescription()
     */
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
    }

    /**
     * This implementation returns the description's hash code.
     *
     * @see #getDescription()
     */
    public int hashCode() {
        return getDescription().hashCode();
	}

}
