/*
 * Copyright (c) 2012 the original author or authors.
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

import ca.simplegames.micro.Globals;
import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;

import java.io.File;

/**
 * Utility class for working with various request paths.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 5:30 PM)
 */
public class PathUtilities {
    private static final String WILDCARD = "*";
    public static final String DOT = ".";
    public static final char SLASH = '/';

    /**
     * Match a path which may contain a wildcard.
     *
     * @param requestPath The request path submitted by the client
     * @param testPath    The match path
     */

    public static boolean match(String requestPath, String testPath) {
        int wildcardIndex = testPath.indexOf(WILDCARD);

        if (wildcardIndex == -1) {
            return requestPath.equals(testPath);
        } else {
            if (wildcardIndex == (testPath.length() - 1)) {
                String checkString = testPath.substring(0, testPath.length() - 1);
                return requestPath.startsWith(checkString);
            } else {
                String preMatch = testPath.substring(0, wildcardIndex);
                String postMatch = testPath.substring(wildcardIndex + 1);

                return requestPath.startsWith(preMatch) && requestPath.endsWith(postMatch);
            }
        }
    }

    /**
     * Match a route.
     *
     * @param requestPath The request path submitted by the client
     * @param testPath    The match path
     */
    public static UriTemplateMatcher routeMatch(String requestPath, String testPath) {
        JaxRsUriTemplateProcessor processor = new JaxRsUriTemplateProcessor(testPath);
        UriTemplateMatcher matcher = processor.matcher();

        if (processor.matcher().matches(requestPath)) {
            return matcher;
        } else {
            return null;
        }
    }

    /**
     * Extract the page name from the given path.  The page name is the
     * name of the file in the path without its suffix.
     *
     * @param path The request path
     * @return The page name
     */

    public static String extractName(String path) {
        if (path == null) {
            return Globals.EMPTY_STRING;
        }

        File file = new File(path);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(DOT);

        if (dotIndex < 0) {
            return null;
        }

        return fileName.substring(0, dotIndex);
    }


    /**
     * Return the page type extracting it from the path.  For example:
     * index.html would return ".html" as the page type.  If the type
     * cannot be determined then this method returns null.
     *
     * @param path The path
     * @return The page type
     */

    public static String extractType(String path) {
        if (path == null) {
            return Globals.EMPTY_STRING;
        }

        File file = new File(path);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(DOT);

        if (dotIndex < 0) {
            return null;
        }

        return fileName.substring(dotIndex);
    }

    /**
     * Extract the View path from the given request path.  This method
     * will return the path from the root to the view descriptor
     * file.
     *
     * @param path The request path
     * @return The view path
     */

    public static String extractViewPath(String path) {
        File file = new File(path);
        File parentDirectory = file.getParentFile();

        String pagePath = null;

        if (parentDirectory == null) {
            pagePath = extractName(path);
        } else {
            String pageName = extractName(path);
            if (pageName != null) {
                pagePath = new File(parentDirectory.getPath(), pageName).getPath();
                pagePath = pagePath.replace(File.separatorChar, SLASH);
            }
        }

        return pagePath;
    }
}
