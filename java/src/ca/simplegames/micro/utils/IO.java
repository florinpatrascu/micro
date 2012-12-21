/*
 * Copyright 2004-2007 the original author or authors.
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
 *
 */


package ca.simplegames.micro.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * Simple utility methods for file and stream copying.
 * All copy methods use a block size of 4096 bytes,
 * and close all affected streams when done.
 * <p/>
 * <p>Mainly for use within the framework,
 * but also useful for application code.
 *
 * @author Juergen Hoeller
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 06.10.2003
 */
public abstract class IO {

    private static final Log log = LogFactory.getLog(IO.class);

    public static final int BUFFER_SIZE = 4096;

    //---------------------------------------------------------------------
    // Copy methods for java.io.File
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given input File to the given output File.
     *
     * @param in  the file to copy from
     * @param out the file to copy to
     * @return the number of bytes copied
     * @throws java.io.IOException in case of I/O errors
     */
    public static long copy(File in, File out) throws IOException {
        return copy(new BufferedInputStream(new FileInputStream(in)),
                new BufferedOutputStream(new FileOutputStream(out)));
    }

    /**
     * Copy the contents of the given byte array to the given output File.
     *
     * @param in  the byte array to copy from
     * @param out the file to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, File out) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(in);
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
        copy(inStream, outStream);
    }

    /**
     * Copy the contents of the given input File into a new byte array.
     *
     * @param in the file to copy from
     * @return the new byte array that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(File in) throws IOException {
        return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.OutputStream
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param in  the stream to copy from
     * @param out the stream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        try {
            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            IO.close(in);
            IO.close(out);
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     * Closes the stream when done.
     *
     * @param in  the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws IOException in case of I/O errors
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        try {
            out.write(in);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException ex) {
                log.warn("Could not close OutputStream", ex);
            }
        }
    }

    /**
     * Copy the contents of the given InputStream into a new byte array.
     * Closes the stream when done.
     *
     * @param in the stream to copy from
     * @return the new byte array that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        copy(in, out);
        return out.toByteArray();
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.Writer
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param in  the Reader to copy from
     * @param out the Writer to copy to
     * @return the number of characters copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(Reader in, Writer out) throws IOException {
        try {
            int byteCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            IO.close(out);
            IO.close(in);
        }
    }

    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     *
     * @param in the reader to copy from
     * @return the String that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(InputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing stream: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(Reader s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing reader: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(OutputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing stream: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(Writer s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing writer: " + e.getMessage());
            }
        }
    }

    /**
     * Read the data from the given file into a byte array and return
     * the array.
     *
     * @param file The file
     * @return The byte array
     * @throws IOException
     */

    public static byte[] readData(File file) throws IOException {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream();

            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            return out.toByteArray();
        } finally {
            IO.close(in);
            IO.close(out);
        }
    }


    /**
     * Write the byte array to the given file.
     *
     * @param file The file to write to
     * @param data The data array
     * @throws IOException
     */

    public static void writeData(File file, byte[] data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
        } finally {
            close(out);
        }
    }

    /**
     * Read the data from the given reader and return it is a single String.
     *
     * @param in The Reader
     * @return The String
     * @throws IOException
     */

    public static String getString(Reader in) throws IOException {
        return copyToString(in);
    }

    /**
     * Read the data from the given InputStream and return it is a single String.
     *
     * @param in The InputStream
     * @return The String
     * @throws IOException
     */
    public static String getString(InputStream in) throws IOException {
        return IO.copyToString(new BufferedReader(new InputStreamReader(in)));
    }

    public static long copy(String s, OutputStream out) throws IOException {
        return copy(new StringReader(s), new BufferedWriter(new OutputStreamWriter(out)));
    }
}
