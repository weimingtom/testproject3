package org.ozsoft.httpclient;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

/**
 * Simple HTTP client.
 * 
 * @author Oscar Stigter
 */
public class HttpClient {

    /**
     * Whether to use an HTTP proxy.
     */
    private boolean useProxy = false;

    /**
     * The proxy host.
     */
    private String proxyHost = "";

    /**
     * The proxy port.
     */
    private int proxyPort = 8080;

    /**
     * The username for proxy authentication.
     */
    private String proxyUsername = "";

    /**
     * The password for proxy authentication.
     */
    private String proxyPassword = "";

    /**
     * Constructor.
     */
    public HttpClient() {
        // Empty implementation.
    }

    /**
     * Sets whether to use a proxy.
     * 
     * @param useProxy
     *            True if using a proxy, otherwise false.
     */
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    /**
     * Sets the proxy's hostname.
     * 
     * @param proxyHost
     *            The proxy's hostname.
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Sets the proxy's port.
     * 
     * @param proxyPort
     *            The proxy's port.
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * Sets the username used for proxy authentication.
     * 
     * @param username
     *            The username.
     */
    public void setProxyUsername(String username) {
        this.proxyUsername = username;
    }

    /**
     * Sets the password used for proxy authentication.
     * 
     * @param password
     *            The password.
     */
    public void setProxyPassword(String password) {
        this.proxyPassword = password;
    }

    /**
     * Creates and returns an HTTP OPTIONS request.
     * 
     * @param url
     *            The URL.
     * 
     * @return The OPTIONS request.
     */
    public HttpRequest createOptionsRequest(String url) {
        return new HttpOptions(this, url);
    }

    /**
     * Creates and returns an HTTP HEAD request.
     * 
     * @param url
     *            The URL.
     * 
     * @return The HEAD request.
     */
    public HttpRequest createHeadRequest(String url) {
        return new HttpHead(this, url);
    }

    /**
     * Creates and returns an HTTP GET request.
     * 
     * @param url
     *            The URL.
     * 
     * @return The GET request.
     */
    public HttpRequest createGetRequest(String url) {
        return new HttpGet(this, url);
    }

    /**
     * Creates and returns an HTTP POST request.
     * 
     * @param url
     *            The URL.
     * @param body
     *            The request body.
     * 
     * @return The POST request.
     */
    public HttpRequest createPostRequest(String url, InputStream body) {
        return new HttpPost(this, url, body);
    }

    /**
     * Creates and returns an HTTP PUT request.
     * 
     * @param url
     *            The URL.
     * 
     * @return The DELETE request.
     */
    public HttpRequest createPutRequest(String url, InputStream body) {
        return new HttpPut(this, url, body);
    }

    /**
     * Creates and returns an HTTP DELETE request.
     * 
     * @param url
     *            The URL.
     * 
     * @return The DELETE request.
     */
    public HttpRequest createDeleteRequest(String url) {
        return new HttpDelete(this, url);
    }

    /**
     * Updates the proxy settigns.
     */
    /* package */void updateProxySettings() {
        Properties properties = System.getProperties();
        if (useProxy) {
            properties.put("http.proxyHost", proxyHost);
            properties.put("http.proxyPort", String.valueOf(proxyPort));
            Authenticator.setDefault(new BasicAuthenticator(proxyUsername, proxyPassword));
        } else {
            properties.remove("http.proxyHost");
            properties.remove("http.proxyPort");
        }
        System.setProperties(properties);
    }

    /**
     * HTTP Basic authenticator based on an username and password.
     * 
     * @author Oscar Stigter
     */
    private static class BasicAuthenticator extends Authenticator {

        /** The username. */
        private final String username;

        /** The password. */
        private final String password;

        /**
         * Constructs a HTTP Basic authenticator.
         * 
         * @param username
         *            The username.
         * @param password
         *            The password.
         */
        public BasicAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.net.Authenticator#getPasswordAuthentication()
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }

    }

}
