package ca.simplegames.micro.helpers.i18n;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.Controller;
import org.apache.commons.lang3.StringUtils;
import org.jrack.Context;
import org.jrack.Rack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:19 PM)
 */
public class I18NHelper implements Helper {
    public static final String I18N = "i18N";
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String version = "0.1";
    private SiteContext site;
    private String intercept = "lang";
    private String scopesSrc = "context";
    private String[] scopes = {scopesSrc};
    private boolean fallbackToSystemLocale = true;
    private String defaultEncoding = "utf-8";
    private int resourceCacheRefreshInterval = 25;
    private String[] resourceBasePaths = new String[]{};
    private ReloadableResourceBundleMessageSource messageSource;
    private static final String DEFAULT_LANG = "en";
    private List<String> infoDetails = new ArrayList<String>();


    public Helper init(SiteContext site, Map<String, Object> locales) throws Exception {
        this.site = site;

        if (locales != null) {
            Map interceptConfig = (Map<String, Object>) locales.get("intercept");

            if (interceptConfig != null) {
                intercept = StringUtils.defaultString((String) interceptConfig.get("parameter_name"), "lang");
                scopesSrc = StringUtils.defaultString((String) interceptConfig.get("scope"), "context");
                scopes = StringUtils.split(scopesSrc, ",");
            }

            defaultEncoding = StringUtils.defaultString((String) locales.get("default_encoding"), Globals.UTF8);
            fallbackToSystemLocale = StringUtils.defaultString((String) locales.get("fallback_to_system_locale"),
                    "true").equalsIgnoreCase("true");
            resourceCacheRefreshInterval = Integer.parseInt(
                    StringUtils.defaultString((locales.get("resource_cache")).toString(), "10"));

            List<String> paths = (List<String>) locales.get("base_names");
            if (paths!= null && !paths.isEmpty()) {
                List<String> absPaths = new ArrayList<String>();

                for (String path : paths) {
                    File realPath = new File(path);
                    if (!realPath.exists()) {
                        realPath = new File(site.getWebInfPath().getAbsolutePath(), path);
                    }

                    try {
                        absPaths.add(realPath.toURI().toURL().toString());
                        //absPaths.add(pathConfig.getValue());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                resourceBasePaths = absPaths.toArray(new String[absPaths.size()]);
            } else {
                resourceBasePaths = new String[]{"config/locales/messages"};
            }


            //Configure the i18n
            messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setDefaultEncoding(defaultEncoding);
            messageSource.setFallbackToSystemLocale(fallbackToSystemLocale);
            messageSource.setCacheSeconds(resourceCacheRefreshInterval);
            messageSource.setBasenames(resourceBasePaths);
            infoDetails.add(String.format("  default encoding ........: %s", defaultEncoding));
            infoDetails.add(String.format("  fallback to system locale: %s", fallbackToSystemLocale));
            infoDetails.add(String.format("  cache refresh ...........: %s", resourceCacheRefreshInterval));
            infoDetails.add(String.format("  resource bundle .........: %s", Arrays.toString(resourceBasePaths)));
            infoDetails.add(String.format("  Listening for ...........: '%s'", intercept));
            infoDetails.add(String.format("       in scope(s) ........: %s", scopesSrc));

        }

        return this;
    }

    public String getName() {
        return I18N;
    }

    public String getDescription() {
        return "Micro's default internationalization support.";
    }

    public String getVersion() {
        return version;
    }

    public String getRepositoryAddress() {
        return null;
    }

    public String getPath() {
        return null;
    }

    public Map<String, Controller> getControllers() {
        return null;
    }

    public Object call(MicroContext context) throws Exception {
        Context<String> rackInput = (Context<String>) context.get(Globals.RACK_INPUT);

        log.info("Executing I18N for: " + rackInput.get(Rack.PATH_INFO));
        if (messageSource != null) {
            Locale locale = new Locale(DEFAULT_LANG);
            // detect locale
            String lang = DEFAULT_LANG;

            for (String scope : scopes) {
                if ("request".equalsIgnoreCase(scope.trim())) {
                    HttpServletRequest request = context.getRequest();
                    if (request != null) {
                        //request parameters first
                        lang = request.getParameter(intercept);
                        if (StringUtils.isNotBlank(lang)) {
                            break;
                        }

                        lang = (String) request.getAttribute(intercept);
                        if (StringUtils.isNotBlank(lang)) {
                            break;
                        }
                    }
                } else if ("session".equalsIgnoreCase(scope.trim())) {
                    HttpSession session = context.getRequest().getSession();
                    if (session != null && StringUtils.isNotBlank((String) session.getAttribute(intercept))) {
                        lang = (String) session.getAttribute(intercept);
                        break;
                    }
                } else if ("context".equalsIgnoreCase(scope.trim())) {
                    if (StringUtils.isNotBlank((String) context.get(intercept))) {
                        lang = (String) context.get(intercept);
                        break;
                    }
                }
            }

            locale = new Locale(lang != null ? lang : DEFAULT_LANG);
            final MessageSourceAccessor msa = new MessageSourceAccessor(messageSource, locale);
            context.with(I18N, msa);
            context.with(Globals.LOCALE, locale);
        } else {
            System.out.println("There is no 'messageSource' defined in your application context." +
                    " Please define one.");
        }
        return null;
    }

    @Override
    public String toString() {
        return "I18NHelper{" +
                "name='" + I18N + '\'' +
                ", info='" + getDescription() + '\'' +
                ", intercept='" + intercept + '\'' +
                ", scopesSrc='" + scopesSrc + '\'' +
                ", scopes=" + (scopes == null ? null : Arrays.asList(scopes)) +
                ", fallbackToSystemLocale=" + fallbackToSystemLocale +
                ", resourceCacheRefreshInterval=" + resourceCacheRefreshInterval +
                ", resourceBasePaths=" + Arrays.toString(resourceBasePaths) +
                '}';
    }
}
