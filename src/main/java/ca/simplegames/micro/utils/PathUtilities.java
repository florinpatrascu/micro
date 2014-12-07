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

import java.io.File;

/**
 * Utility class for working with various request paths.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013.01.13 5:30 PM)
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
     * Extract the page name from the given path.  The page name is the
     * name of the file in the path without its extension.
     *
     * @param path The request path
     * @return The view name
     */

    public static String extractName(String path) {
        return path != null ? extractName(new File(path)) : Globals.EMPTY_STRING;
    }

    /**
     * Extract the page name from the given file.  The page name is the
     * name of the file in the path without its extension.
     *
     * @param file a file
     * @return The view name
     */

    public static String extractName(File file) {
        String name = Globals.EMPTY_STRING;

        if (file != null) {
            // correct but too expensive :(
            // name = file.getName().replaceFirst(Globals.FILE_EXTENSION_MATCHER, Globals.EMPTY_STRING);
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf(DOT);

            if (dotIndex >= 0) {
                name = fileName.substring(0, dotIndex);
            }
        }

        return name;
    }

    /**
     * Return the page type extracting it from the path.  For example:
     * index.html would return ".html" as the page type.  If the type
     * cannot be determined then this method returns null.
     *
     * @param path the path
     * @return the view type
     */

    public static String extractType(String path) {
        if (path == null) {
            return Globals.EMPTY_STRING;
        }

        int dotIndex = path.lastIndexOf(DOT);
        int sepPos = path.lastIndexOf(File.separator);
        if (dotIndex < 0 || dotIndex < sepPos) {
            return Globals.EMPTY_STRING;
        }

        return path.substring(dotIndex);
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
