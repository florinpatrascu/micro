package ca.simplegames.micro.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
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
     */
    public static void configureClasspath(String rootPathName, String[] paths) {
        for (String path : paths) {
            log.info("path: " + path);
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
                                    file.getName().toLowerCase().endsWith(".zip")) {
                                try {
                                    addLibraryPath(file.getAbsolutePath());
                                } catch (Exception e) {
                                    log.error("cannot access: " + file.getAbsolutePath());
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
    }
}
