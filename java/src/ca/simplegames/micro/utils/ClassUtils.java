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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 1:30 PM)
 */
public class ClassUtils {
    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class.getName());

    /**
     * Adds the specified path to the java library path
     * See: http://fahdshariff.blogspot.ca/2011/08/changing-java-library-path-at-runtime.html
     *
     * @param userPath the path to add
     * @throws Exception
     */
    public static void addLibraryPath(String userPath) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(userPath)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = userPath;
        usrPathsField.set(null, newPaths);
    }


    /**
     * Configure the classpath for use by scripting languages. This will
     * add the WEB-INF/classes directory, all JAR and ZIP files in the
     * WEB-INF/lib directory and any user defined library to the classpath.
     *
     * @param rootPathName the root path used for calculating user class paths if
     *                     they are relative
     * @param paths        an array with paths containing Java libraries
     * @return a String containing the file names loaded as resources
     */
    public static String configureClasspath(String rootPathName, String[] paths) {
        StringBuilder resources = new StringBuilder();

        for (String path : paths) {
            File classPath = new File(path);

            // check if the path is absolute
            if (!classPath.exists()) {
                classPath = new File(rootPathName, path);
            }

            // try again. Check this time if it is relative to the rootPathName
            if (classPath.exists()) {
                log.info("Loading classes from: " + classPath.toString());

                if (classPath.isDirectory()) {
                    File[] files = classPath.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().toLowerCase().endsWith(".jar") ||
                                    file.getName().toLowerCase().endsWith(".zip") ||
                                    file.getName().toLowerCase().endsWith(".xml")) {
                                final String absolutePath = file.getAbsolutePath();
                                try {
                                    addLibraryPath(absolutePath);
                                    resources.append(absolutePath).append(", ");
                                } catch (Exception e) {
                                    log.error("cannot access: " + absolutePath);
                                }
                            }
                        }
                    }
                } else if (classPath.isFile()) {
                    try {
                        addLibraryPath(classPath.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("cannot access: " + classPath.getAbsolutePath());
                    }

                }//else ... think about

            } else {
                log.error("Loading classes from: " + classPath.toString() + ", failed.");
            }
        }
        return resources.toString();
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you absolutely need a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            log.debug("Cannot access thread context ClassLoader - falling back to system class loader", ex);
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * Determine whether the given class has a method with the given signature.
     * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
     *
     * @param clazz      the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     * @return whether the class has a corresponding method
     * @see java.lang.Class#getMethod
     */
    public static boolean hasMethod(Class clazz, String methodName, Class[] paramTypes) {
        return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
    }

    /**
     * Determine whether the given class has a method with the given signature,
     * and return it if available (else return <code>null</code>).
     * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
     *
     * @param clazz      the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     * @return the method, or <code>null</code> if not found
     * @see java.lang.Class#getMethod
     */
    public static Method getMethodIfAvailable(Class clazz, String methodName, Class[] paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Load the specified resource.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The resource name
     * @param requestor The object requesting the resource or null
     * @return The resource URL or null
     */

    public static URL getResource(String name, Object requestor) {
        Class requestorClass = null;
        if (requestor == null) {
            requestorClass = ClassUtils.class;
        } else {
            requestorClass = requestor.getClass();
        }
        return getResource(name, requestorClass);
    }

    /**
     * Load the specified resource.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The resource name
     * @param requestor The class of the object requesting the resource or null
     * @return The resource URL or null
     */

    public static URL getResource(String name, Class requestor) {
        URL resource;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        resource = cl.getResource(name);
        if (resource == null) {
            cl = requestor.getClass().getClassLoader();
            resource = cl.getResource(name);
        }
        return resource;
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then ClassUtilities class
     * loader is used.
     *
     * @param name The resource name
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name) {
        return getResourceAsStream(name, null);
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The class name
     * @param requestor The object requesting the resource or null
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name,
                                                  Object requestor) {
        Class requestorClass = null;
        if (requestor == null) {
            requestorClass = ClassUtils.class;
        } else {
            requestorClass = requestor.getClass();
        }
        return getResourceAsStream(name, requestorClass);
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The class name
     * @param requestor The class of the object requesting the resource or null
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name,
                                                  Class requestor) {
        InputStream resourceStream = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        resourceStream = cl.getResourceAsStream(name);
        if (resourceStream == null) {
            cl = requestor.getClass().getClassLoader();
            resourceStream = cl.getResourceAsStream(name);
        }
        return resourceStream;
    }
}
