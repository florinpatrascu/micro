/*
 * Copyright (c)2012. Florin T.PATRASCU
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

package ca.simplegames.micro;

import ca.simplegames.micro.utils.UrlUtilities;
import org.jrack.Context;
import org.jrack.Rack;
import org.jrack.RackResponse;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread safe context created for every new Request. Avoid serializing its instance as it may be
 * loaded with user stuff.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 11:05 AM)
 */
public class MicroContext<T> implements Context<T> {
    protected Map<String, Object> map;
    protected Context<T> rackInput;
    private boolean halt;

    public MicroContext(Map<String, Object> map) {
        this.map = map;
    }

    public MicroContext() {
        this(new ConcurrentHashMap<String, Object>(8, 0.9f, 1));
    }

    public Object getObject(String key) {
        if (key != null && map != null && map.containsKey(key)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public T get(String key) {
        if (key != null && map != null && map.containsKey(key)) {
            return (T) map.get(key);
        } else {
            return null;
        }
    }

    public Context<T> with(String key, Object value) {
        if (key != null && value != null) {
            map.put(key, value);
            if (key.equalsIgnoreCase(Globals.RACK_INPUT)) {
                rackInput = (Context<T>) value;
            }
        }
        return this;
    }

    public MicroContext<T> with(Context<String> context) {
        for (Map.Entry<String, Object> entry : context) {
            with(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Context<T> put(String key, Object value) {
        return with(key, value);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public Object remove(String key) {
        Object o = null;
        if (key != null && map != null && map.containsKey(key)) {
            o = map.remove(key);
        }
        return o;
    }

    public Iterator<Map.Entry<String, Object>> iterator() {
        return map.entrySet().iterator();
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) rackInput.get(Rack.REQUEST);
    }

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) map.get(Globals.RESPONSE);
    }

    public SiteContext getSiteContext() {
        return (SiteContext) map.get(Globals.SITE);
    }

    public boolean isHalt() {
        return halt;
    }

    public void halt() {
        halt = true;
    }

    public Context<T> getRackInput() {
        return rackInput;
    }

    public String getTemplateName() {
        return (String) get(Globals.TEMPLATE);
    }

    public RackResponse getRackResponse() {
        return (RackResponse) get(Globals.RACK_RESPONSE);
    }

    public void setRackResponse(RackResponse response) {
        with(Globals.RACK_RESPONSE, response);
    }

    public Logger getLog() {
        return (Logger) get(Globals.LOG);
    }

    public String getTemplatesRepositoryName() {
        return (String) get(Globals.MICRO_TEMPLATES_REPOSITORY_NAME);
    }

    public void setTemplatesRepositoryName(String repositoryName) {
        if (repositoryName != null && repositoryName.trim().length() > 0) {
            with(Globals.MICRO_TEMPLATES_REPOSITORY_NAME, repositoryName);
        }
    }

    public String getDefaultRepositoryName() {
        return (String) get(Globals.MICRO_DEFAULT_REPOSITORY_NAME);
    }

    public void setDefaultRepositoryName(String repositoryName) {
        if (repositoryName != null && repositoryName.trim().length() > 0) {
            with(Globals.MICRO_DEFAULT_REPOSITORY_NAME, repositoryName);
        }
    }

    /**
     * If the HTTP 1.1 client is redirected from a different verb than GET, use 303
     * instead of 302 by default. You may still pass 302 explicitly.
     * <p/>
     * todo: improve the API for specifying a secure redirect
     * too bad the scripting is unable to find the Java methods using optional arguments :(
     *
     * @param path   the path where the browser will be redirected to
     * @param secure true if redirecting to 443
     * @throws RedirectException a special exception to be intercepted by Micro so it can
     *                           halt the current process and return the control back to the JRack
     */
    public void setRedirect(String path, boolean secure, int redirectCode) throws RedirectException {
        if (path != null && !path.isEmpty()) {
            RackResponse response = getRackResponse();
            String method = getRequest() != null? getRequest().getMethod(): Globals.EMPTY_STRING;
            int rCode = redirectCode != 0 ? redirectCode :
                    (!"GET".equalsIgnoreCase(method) ? 303 : 302);

            response = null;
            response = new RackResponse(rCode)
                    .withBody(Globals.EMPTY_STRING)
                    .withContentLength(0);

            int defaultPort = getRequest() != null? getRequest().getServerPort() : 8080;
            UrlUtilities urlUtilities = new UrlUtilities(getRequest(), getResponse());

            String location = secure ?
                    urlUtilities.buildSecure(path) :
                    urlUtilities.buildStandard(path, defaultPort);

            with(Globals.RACK_RESPONSE, response.withHeader("Location", location));
            throw new RedirectException();
        }
    }

    public void setRedirect(String path, boolean secure) throws RedirectException {
        setRedirect(path, secure, 0);
    }

}
