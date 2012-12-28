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

package ca.simplegames.micro.extensions.i18n;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.Helper;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.Extension;
import ca.simplegames.micro.helpers.HelperManager;
import ca.simplegames.micro.utils.Assert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 10:19 PM)
 */
@SuppressWarnings("unchecked")
public class I18NExtension implements Extension {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String version = "0.1";
    private String intercept = "lang";
    private String scopesSrc = "context";
    private String[] scopes = {scopesSrc};
    private boolean fallbackToSystemLocale = true;
    private String defaultEncoding = "utf-8";
    private int resourceCacheRefreshInterval = 25;
    private String[] resourceBasePaths = new String[]{};
    private ReloadableResourceBundleMessageSource messageSource;
    public static final String DEFAULT_LANG = "en";
    private List<String> infoDetails = new ArrayList<String>();
    private String name = "i18N";

    public Extension register(String name, SiteContext site, Map<String, Object> locales) throws Exception {
        Assert.notNull(name, "The name of the extension must not be null!");
        this.name = StringUtils.defaultIfBlank(name, "i18N");

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
            if (paths != null && !paths.isEmpty()) {
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

            Helper i18N = new I18NHelper(this);
            final HelperManager helperManager = site.getHelperManager();

            helperManager.addHelper(i18N);
            // now make sure the i18N filter is always first (if present)
            Collections.swap(helperManager.getBeforeHelpers(), 0, helperManager.getBeforeHelpers().size() - 1);

            infoDetails.add(String.format("  default encoding ........: %s", defaultEncoding));
            infoDetails.add(String.format("  fallback to system locale: %s", fallbackToSystemLocale));
            infoDetails.add(String.format("  cache refresh ...........: %s", resourceCacheRefreshInterval));
            infoDetails.add(String.format("  resource bundle .........: %s", Arrays.toString(resourceBasePaths)));
            infoDetails.add(String.format("  Listening for ...........: '%s'", intercept));
            infoDetails.add(String.format("       in scope(s) ........: %s", scopesSrc));
        }

        return this;
    }

    public ReloadableResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public String[] getScopes() {
        return scopes;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public String getIntercept() {
        return intercept;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "I18NExtension{" +
                "infoDetails=" + infoDetails +
                '}';
    }
}
