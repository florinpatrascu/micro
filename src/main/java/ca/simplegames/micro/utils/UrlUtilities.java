package ca.simplegames.micro.utils;

/**
 * Utility class for building URLs.
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 13-02-12)
 */

import ca.simplegames.micro.Globals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public class UrlUtilities {

    /**
     * The URL path separator.
     */

    public static final String URL_PATH_SEPARATOR = "/";
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * Construct a new URLUtilities class which can use the given request
     * and response objects to build URLs.
     *
     * @param request  The request object
     * @param response The response object
     */

    public UrlUtilities(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Build an HTTP URL relative to the application context using the given
     * path.
     *
     * @param path The path
     */

    public String buildStandard(String path) {
        return buildStandard(path, 0);
    }

    /**
     * Build an HTTP URL relative to the application context using the given
     * path.  This version of the <code>buildStandard</code> method allows you
     * to specify the port number.  A port number of 0 will cause the port
     * argument to be ignored.
     *
     * @param path The path
     * @param port The port
     */

    public String buildStandard(String path, int port) {
        return build(path, "http", port);
    }

    /**
     * Build an HTTPS (Secure Socket Layer) method relative to the application
     * context using the given path.
     *
     * @param path The path
     */

    public String buildSecure(String path) {
        return buildSecure(path, 0);
    }

    /**
     * Build an HTTPS (Secure Socket Layer) method relative to the application
     * context using the given path.  This version of the
     * <code>buildSecure</code> method allows you to specify the port number.
     * A port number of 0 will cause the port argument to be ignored.
     *
     * @param path The path
     * @param port The port
     */

    public String buildSecure(String path, int port) {
        return build(path, "https", port);
    }

    /**
     * Build a URL using the given path, protocol and port.  The path will be
     * relative to the current context.
     *
     * @param path     The path
     * @param protocol (i.e. http or https)
     * @param port     The port (0 to ignore the port argument)
     * @return The URL as a String
     */

    protected String build(String path, String protocol, int port) {
        String serverName = request != null ? request.getServerName() : Globals.EMPTY_STRING;
        String contextPath = request != null ? request.getContextPath() : Globals.EMPTY_STRING;

        if (!contextPath.endsWith(URL_PATH_SEPARATOR)) {
            contextPath = contextPath + URL_PATH_SEPARATOR;
        }

        if (path.startsWith(URL_PATH_SEPARATOR)) {
            path = path.substring(1);
        }

        String requestPath = contextPath + path;


        StringBuilder buffer = new StringBuilder();
        buffer.append(protocol).append("://").append(serverName);

        if (port > 0) {
            buffer.append(":").append(port);
        }

        if (!requestPath.startsWith(URL_PATH_SEPARATOR)) {
            buffer.append(URL_PATH_SEPARATOR);
        }

        return buffer.append(requestPath).toString();
    }

    /**
     * Percent-encode the given String.  This method delegates to
     * the URLEncoder.encode() method.
     *
     * @param s The String to encode
     * @return The encoded String
     * @see java.net.URLEncoder
     */

    public String encode(String s) {
        try {
            return URLEncoder.encode(s, String.valueOf(Charset.forName(Globals.UTF8)));
        } catch (UnsupportedEncodingException e) {
            return String.format("%s, Unsupported Encoding Exception: %s", s, e.getMessage());
        }
    }

    public String encode(String s, String encoding) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, encoding);
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
