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
import ca.simplegames.micro.Filter;
import ca.simplegames.micro.MicroContext;
import org.jrack.Rack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-27 5:52 PM)
 */
public class I18NFilter implements Filter {

    public static final String BROWSER = "browser";
    private I18NExtension extension;

    public I18NFilter(I18NExtension extension) {
        this.extension = extension;
    }

    public void call(MicroContext context) throws Exception {
        //Context<String> rackInput = (Context<String>) context.get(Globals.RACK_INPUT);
        if (extension.getMessageSource() != null) {
            // detect the locale
            String language = I18NExtension.DEFAULT_LANG;

            for (String scope : extension.getScopes()) {
                if (Globals.REQUEST.equalsIgnoreCase(scope.trim())) {
                    HttpServletRequest request = context.getRequest();
                    if (request != null) {
                        //request parameters first
                        language = request.getParameter(extension.getIntercept());
                        if (language != null && language.trim().length() > 0) {
                            break;
                        }

                        language = (String) request.getAttribute(extension.getIntercept());
                        if (language != null && language.trim().length() > 0) {
                            break;
                        }
                    }
                } else if (Globals.SESSION.equalsIgnoreCase(scope.trim())) {
                    if (context.getRequest() != null) {
                        // make sure we're not creating new sessions
                        HttpSession session = context.getRequest().getSession(false);
                        if (session != null) {
                            final String attribute = (String) session.getAttribute(extension.getIntercept());

                            if (attribute != null && attribute.trim().length() > 0) {
                                language = attribute;
                                break;
                            }
                        }
                    }
                } else if (Globals.CONTEXT.equalsIgnoreCase(scope.trim())) {
                    final String attribute = (String) context.get(extension.getIntercept());

                    if (attribute != null && attribute.trim().length() > 0) {
                        language = attribute;
                        break;
                    }
                } else if (Globals.PARAMS.equalsIgnoreCase(scope.trim())) {
                    if (context.get(Rack.PARAMS) != null) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> params = ((Map<String, Object>) context.get(Rack.PARAMS));
                        final String intercept = (String) params.get(extension.getIntercept());

                        if (intercept != null && intercept.length()>0) {
                            final String attribute = (String) params.get(extension.getIntercept());
                            if (attribute != null && attribute.trim().length() > 0) {
                                language = attribute;
                                break;
                            }
                        }
                    }
                } else if (BROWSER.equalsIgnoreCase(scope.trim())) {
                    if (context.getRackInput() != null) {
                        final String attribute = (String) context.getRackInput().get(Rack.RACK_BROWSER_LOCALE);

                        if (attribute != null && attribute.trim().length() > 0) {
                            language = attribute;
                            break;
                        }
                    }
                }
            }

            Locale locale = new Locale(language != null ? language : I18NExtension.DEFAULT_LANG);
            final MessageSourceAccessor msa = new MessageSourceAccessor(extension.getMessageSource(), locale);
            context.with(extension.getName(), msa);
            context.with(Globals.LOCALE, locale);
        } else {
            System.out.println("There is no 'messageSource' defined in your application context." +
                    " Please define one.");
        }
    }


    public boolean isBefore() {
        return true;
    }

    public boolean isAfter() {
        return false;
    }

    public String getPath() {
        return Globals.EMPTY_STRING;
    }

    public String getController() {
        return Globals.EMPTY_STRING;
    }
}