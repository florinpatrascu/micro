package ca.simplegames.micro;

import org.jrack.Context;
import org.jrack.Rack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 11:05 AM)
 */
public class MicroContext<T> implements Context<T> {
    protected Map<String, Object> map;
    protected Context<T> rackInput;

    public MicroContext(Map<String, Object> map) {
        this.map = map;
    }

    public MicroContext() {
        this(new ConcurrentHashMap<String, Object>());
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

    public HttpServletResponse getResponse(){
        return (HttpServletResponse) map.get(Globals.RESPONSE);
    }

    public SiteContext getSiteContext() {
        return (SiteContext) map.get(Globals.MICRO_SITE);
    }
}
