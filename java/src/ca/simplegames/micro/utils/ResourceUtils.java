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

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility methods for resolving resource locations to files in the
 * file system. Mainly for internal use within the framework.
 * <p/>
 * <p>Consider using Spring's Resource abstraction in the core package
 * for handling all kinds of file resources in a uniform manner.
 *
 * @author Juergen Hoeller
 * @since 1.1.5
 */
public abstract class ResourceUtils {

    /**
     * Pseudo URL prefix for loading from the class path: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * URL prefix for loading from the file system: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * URL protocol for a file in the file system: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";

    /**
     * URL protocol for an entry from a jar file: "jar"
     */
    public static final String URL_PROTOCOL_JAR = "jar";

    /**
     * URL protocol for an entry from a zip file: "zip"
     */
    public static final String URL_PROTOCOL_ZIP = "zip";

    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar"
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";

    /**
     * URL protocol for an entry from an OC4J jar file: "code-source"
     */
    public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

    /**
     * Separator between JAR URL and file path within the JAR
     */
    public static final String JAR_URL_SEPARATOR = "!/";


    /**
     * Return whether the given resource location is a URL:
     * either a special "classpath" pseudo URL or a standard URL.
     *
     * @param resourceLocation the location String to check
     * @return whether the location qualifies as a URL
     * @see #CLASSPATH_URL_PREFIX
     * @see java.net.URL
     */
    public static boolean isUrl(String resourceLocation) {
        if (resourceLocation == null) {
            return false;
        }
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            return true;
        }
        try {
            new URL(resourceLocation);
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    /**
     * Resolve the given resource location to a <code>java.net.URL</code>.
     * <p>Does not check whether the URL actually exists; simply returns
     * the URL that the given location would correspond to.
     *
     * @param resourceLocation the resource location to resolve: either a
     *                         "classpath:" pseudo URL, a "file:" URL, or a plain file path
     * @return a corresponding URL object
     * @throws FileNotFoundException if the resource cannot be resolved to a URL
     */
    public static URL getURL(String resourceLocation) throws FileNotFoundException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            URL url = ClassUtils.getDefaultClassLoader().getResource(path);
            if (url == null) {
                String description = "class path resource [" + path + "]";
                throw new FileNotFoundException(
                        description + " cannot be resolved to URL because it does not exist");
            }
            return url;
        }
        try {
            // try URL
            return new URL(resourceLocation);
        } catch (MalformedURLException ex) {
            // no URL -> treat as file path
            try {
                return new File(resourceLocation).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new FileNotFoundException("Resource location [" + resourceLocation +
                        "] is neither a URL not a well-formed file path");
            }
        }
    }

    /**
     * Resolve the given resource location to a <code>java.io.File</code>,
     * i.e. to a file in the file system.
     * <p>Does not check whether the fil actually exists; simply returns
     * the File that the given location would correspond to.
     *
     * @param resourceLocation the resource location to resolve: either a
     *                         "classpath:" pseudo URL, a "file:" URL, or a plain file path
     * @return a corresponding File object
     * @throws FileNotFoundException if the resource cannot be resolved to
     *                               a file in the file system
     */
    public static File getFile(String resourceLocation) throws FileNotFoundException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            String description = "class path resource [" + path + "]";
            URL url = ClassUtils.getDefaultClassLoader().getResource(path);
            if (url == null) {
                throw new FileNotFoundException(
                        description + " cannot be resolved to absolute file path " +
                                "because it does not reside in the file system");
            }
            return getFile(url, description);
        }
        try {
            // try URL
            return getFile(new URL(resourceLocation));
        } catch (MalformedURLException ex) {
            // no URL -> treat as file path
            return new File(resourceLocation);
        }
    }

    /**
     * Resolve the given resource URL to a <code>java.io.File</code>,
     * i.e. to a file in the file system.
     *
     * @param resourceUrl the resource URL to resolve
     * @return a corresponding File object
     * @throws FileNotFoundException if the URL cannot be resolved to
     *                               a file in the file system
     */
    public static File getFile(URL resourceUrl) throws FileNotFoundException {
        return getFile(resourceUrl, "URL");
    }

    /**
     * Resolve the given resource URL to a <code>java.io.File</code>,
     * i.e. to a file in the file system.
     *
     * @param resourceUrl the resource URL to resolve
     * @param description a description of the original resource that
     *                    the URL was created for (for example, a class path location)
     * @return a corresponding File object
     * @throws FileNotFoundException if the URL cannot be resolved to
     *                               a file in the file system
     */
    public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
        Assert.notNull(resourceUrl, "Resource URL must not be null");
        if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
            throw new FileNotFoundException(
                    description + " cannot be resolved to absolute file path " +
                            "because it does not reside in the file system: " + resourceUrl);
        }
        try {
            return new File(toURI(resourceUrl).getSchemeSpecificPart());
        } catch (URISyntaxException ex) {
            // Fallback for URLs that are not valid URIs (should hardly ever happen).
            return new File(resourceUrl.getFile());
        }
    }

    /**
     * Resolve the given resource URI to a <code>java.io.File</code>,
     * i.e. to a file in the file system.
     *
     * @param resourceUri the resource URI to resolve
     * @return a corresponding File object
     * @throws FileNotFoundException if the URL cannot be resolved to
     *                               a file in the file system
     */
    public static File getFile(URI resourceUri) throws FileNotFoundException {
        return getFile(resourceUri, "URI");
    }

    /**
     * Resolve the given resource URI to a <code>java.io.File</code>,
     * i.e. to a file in the file system.
     *
     * @param resourceUri the resource URI to resolve
     * @param description a description of the original resource that
     *                    the URI was created for (for example, a class path location)
     * @return a corresponding File object
     * @throws FileNotFoundException if the URL cannot be resolved to
     *                               a file in the file system
     */
    public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
        Assert.notNull(resourceUri, "Resource URI must not be null");
        if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
            throw new FileNotFoundException(
                    description + " cannot be resolved to absolute file path " +
                            "because it does not reside in the file system: " + resourceUri);
        }
        return new File(resourceUri.getSchemeSpecificPart());
    }

    /**
     * Determine whether the given URL points to a resource in a jar file,
     * that is, has protocol "jar", "zip", "wsjar" or "code-source".
     * <p>"zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively,
     * but can be treated like jar files. The same applies to "code-source" URLs on Oracle
     * OC4J, provided that the path contains a jar separator.
     *
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR URL
     */
    public static boolean isJarURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol) ||
                (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
    }

    /**
     * Extract the URL for the actual jar file from the given URL
     * (which may point to a resource in a jar file or to a jar file itself).
     *
     * @param jarUrl the original URL
     * @return the URL for the actual jar file
     * @throws MalformedURLException if no valid jar file URL could be extracted
     */
    public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
        String urlFile = jarUrl.getFile();
        int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
        if (separatorIndex != -1) {
            String jarFile = urlFile.substring(0, separatorIndex);
            try {
                return new URL(jarFile);
            } catch (MalformedURLException ex) {
                // Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
                // This usually indicates that the jar file resides in the file system.
                if (!jarFile.startsWith("/")) {
                    jarFile = "/" + jarFile;
                }
                return new URL(FILE_URL_PREFIX + jarFile);
            }
        } else {
            return jarUrl;
        }
    }

    /**
     * Create a URI instance for the given URL, replacing spaces with
     * "%20" quotes first.
     * <p>Furthermore, this method works on JDK 1.4 as well,
     * in contrast to the <code>URL.toURI()</code> method.
     *
     * @param url the URL to convert into a URI instance
     * @return the URI instance
     * @throws URISyntaxException if the URL wasn't a valid URI
     * @see java.net.URL#toURI()
     */
    public static URI toURI(URL url) throws URISyntaxException {
        return new URI(StringUtils.replace(url.toString(), " ", "%20"));
    }

}
