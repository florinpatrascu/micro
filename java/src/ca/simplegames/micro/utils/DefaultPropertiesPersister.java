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

import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Default implementation of the {@link PropertiesPersister} interface.
 * Follows the native parsing of <code>java.util.Properties</code>.
 * <p/>
 * <p>Allows for reading from any Reader and writing to any Writer, for example
 * to specify a charset for a properties file. This is a capability that standard
 * <code>java.util.Properties</code> unfortunately lacks up until JDK 1.5:
 * You can only load files using the ISO-8859-1 charset there.
 * <p/>
 * <p>Loading from and storing to a stream delegates to <code>Properties.load</code>
 * and <code>Properties.store</code>, respectively, to be fully compatible with
 * the Unicode conversion as implemented by the JDK Properties class. On JDK 1.6,
 * <code>Properties.load/store</code> will also be used for readers/writers,
 * effectively turning this class into a plain backwards compatibility adapter.
 * <p/>
 * <p>The persistence code that works with Reader/Writer follows the JDK's parsing
 * strategy but does not implement Unicode conversion, because the Reader/Writer
 * should already apply proper decoding/encoding of characters. If you use prefer
 * to escape unicode characters in your properties files, do <i>not</i> specify
 * an encoding for a Reader/Writer (like ReloadableResourceBundleMessageSource's
 * "defaultEncoding" and "fileEncodings" properties).
 * <p/>
 * <p>As of Spring 1.2.2, this implementation also supports properties XML files,
 * through the <code>loadFromXml</code> and <code>storeToXml</code> methods.
 * The default implementations delegate to JDK 1.5's corresponding methods,
 * throwing an exception if running on an older JDK. Those implementations
 * could be subclassed to apply custom XML handling on JDK 1.4, for example.
 *
 * @author Juergen Hoeller
 * @see java.util.Properties
 * @see java.util.Properties#load
 * @see java.util.Properties#store
 * @since 10.03.2004
 */
public class DefaultPropertiesPersister implements PropertiesPersister {

    // Determine whether Properties.load(Reader) is available (on JDK 1.6+)
    private static final boolean loadFromReaderAvailable =
            ClassUtils.hasMethod(Properties.class, "load", new Class[]{Reader.class});

    // Determine whether Properties.store(Writer, String) is available (on JDK 1.6+)
    private static final boolean storeToWriterAvailable =
            ClassUtils.hasMethod(Properties.class, "store", new Class[]{Writer.class, String.class});


    public void load(Properties props, InputStream is) throws IOException {
        props.load(is);
    }

    public void load(Properties props, Reader reader) throws IOException {
        // Fall back to manual parsing.
        doLoad(props, reader);
    }

    protected void doLoad(Properties props, Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return;
            }
            line = StringUtils.trimLeadingWhitespace(line);
            if (line.length() > 0) {
                char firstChar = line.charAt(0);
                if (firstChar != '#' && firstChar != '!') {
                    while (endsWithContinuationMarker(line)) {
                        String nextLine = in.readLine();
                        line = line.substring(0, line.length() - 1);
                        if (nextLine != null) {
                            line += StringUtils.trimLeadingWhitespace(nextLine);
                        }
                    }
                    int separatorIndex = line.indexOf("=");
                    if (separatorIndex == -1) {
                        separatorIndex = line.indexOf(":");
                    }
                    String key = (separatorIndex != -1 ? line.substring(0, separatorIndex) : line);
                    String value = (separatorIndex != -1) ? line.substring(separatorIndex + 1) : "";
                    key = StringUtils.trimTrailingWhitespace(key);
                    value = StringUtils.trimLeadingWhitespace(value);
                    props.put(unescape(key), unescape(value));
                }
            }
        }
    }

    protected boolean endsWithContinuationMarker(String line) {
        boolean evenSlashCount = true;
        int index = line.length() - 1;
        while (index >= 0 && line.charAt(index) == '\\') {
            evenSlashCount = !evenSlashCount;
            index--;
        }
        return !evenSlashCount;
    }

    protected String unescape(String str) {
        StringBuffer outBuffer = new StringBuffer(str.length());
        for (int index = 0; index < str.length();) {
            char c = str.charAt(index++);
            if (c == '\\') {
                c = str.charAt(index++);
                if (c == 't') {
                    c = '\t';
                } else if (c == 'r') {
                    c = '\r';
                } else if (c == 'n') {
                    c = '\n';
                } else if (c == 'f') {
                    c = '\f';
                }
            }
            outBuffer.append(c);
        }
        return outBuffer.toString();
    }


    public void store(Properties props, OutputStream os, String header) throws IOException {
        props.store(os, header);
    }

    public void store(Properties props, Writer writer, String header) throws IOException {
        doStore(props, writer, header);
    }

    protected void doStore(Properties props, Writer writer, String header) throws IOException {
        BufferedWriter out = new BufferedWriter(writer);
        if (header != null) {
            out.write("#" + header);
            out.newLine();
        }
        out.write("#" + new Date());
        out.newLine();
        for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            String val = props.getProperty(key);
            out.write(escape(key, true) + "=" + escape(val, false));
            out.newLine();
        }
        out.flush();
    }

    protected String escape(String str, boolean isKey) {
        int len = str.length();
        StringBuffer outBuffer = new StringBuffer(len * 2);
        for (int index = 0; index < len; index++) {
            char c = str.charAt(index);
            switch (c) {
                case ' ':
                    if (index == 0 || isKey) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '\\':
                    outBuffer.append("\\\\");
                    break;
                case '\t':
                    outBuffer.append("\\t");
                    break;
                case '\n':
                    outBuffer.append("\\n");
                    break;
                case '\r':
                    outBuffer.append("\\r");
                    break;
                case '\f':
                    outBuffer.append("\\f");
                    break;
                default:
                    if ("=: \t\r\n\f#!".indexOf(c) != -1) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(c);
            }
        }
        return outBuffer.toString();
    }


    public void loadFromXml(Properties props, InputStream is) throws IOException {
        try {
            props.loadFromXML(is);
        }
        catch (NoSuchMethodError err) {
            throw new IOException("Cannot load properties XML file - not running on JDK 1.5+: " + err.getMessage());
        }
    }

    public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
        try {
            props.storeToXML(os, header);
        }
        catch (NoSuchMethodError err) {
            throw new IOException("Cannot store properties XML file - not running on JDK 1.5+: " + err.getMessage());
        }
    }

    public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
        try {
            props.storeToXML(os, header, encoding);
        }
        catch (NoSuchMethodError err) {
            throw new IOException("Cannot store properties XML file - not running on JDK 1.5+: " + err.getMessage());
        }
    }

}