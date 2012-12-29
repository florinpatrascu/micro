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

import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.utils.ClassUtils;
import ca.simplegames.micro.utils.PathUtilities;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.jrack.*;
import org.jrack.context.MapContext;
import org.jrack.utils.Mime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * This {@link JRack} is the main entry point for requests to the Micro framework.
 * It should be configured to handle all the requests for its context.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-17 4:21 PM)
 */
public class MicroFilter extends JRack {
    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String INDEX = "index";
    public static final String HTML = "html";
    private Logger log = LoggerFactory.getLogger(getClass());

    FilterConfig filterConfig;
    private SiteContext site;
    private String welcomeFile;

    @Override
    public Object init(Object config) throws Exception {
        showBanner();
        filterConfig = (FilterConfig) config;
        final ServletContext servletContext = filterConfig.getServletContext();
        final String realPath = servletContext.getRealPath("/");
        final File contextPath = new File(realPath);
        final File webInfPath = new File(contextPath, "WEB-INF");
        welcomeFile = StringUtils.defaultString(
                filterConfig.getInitParameter("filterAddsWelcomeFile"), "false").trim();

        site = new SiteContext(new MapContext<String>()
                .with(Globals.SERVLET_CONTEXT, servletContext)
                .with(Globals.SERVLET_PATH_NAME, realPath)
                .with(Globals.SERVLET_PATH, contextPath)
                .with(Globals.WEB_INF_PATH, webInfPath)
        );

        //initialize the classpath
        StringBuffer userClassPaths = new StringBuffer();
        userClassPaths.append(webInfPath.toString()).append("/lib,");
        userClassPaths.append(webInfPath.toString()).append("/classes,");

        String userClassPathsParam = StringUtils.defaultString(
                ((FilterConfig) config).getInitParameter("userClassPaths"));

        if (StringUtils.isNotBlank(userClassPathsParam)) {
            userClassPaths.append(",").append(userClassPathsParam);
        }

        ClassUtils.configureClasspath(webInfPath.toString(),
                StringUtils.split(userClassPaths.toString(), ",: "));
        configureBSF();

        site.loadApplication(webInfPath.getAbsolutePath() + "/config");
        // done with the init phase
        return this;
    }

    /**
     * main entry point, this is where Micro is processing all the incoming requests
     *
     * @param input the Rack input object
     * @return a Rack response see: {@link RackResponse}
     */
    public RackResponse call(Context<String> input) throws Exception {
        MicroContext context = new MicroContext<String>();

        input.with(Globals.MICRO_SITE, site);
        input.with(Rack.RACK_LOGGER, log);

        String pathInfo = input.get(Rack.PATH_INFO);
        context.with(Globals.RACK_INPUT, input)
                .with(Globals.MICRO_SITE, site)
                .with(Rack.RACK_LOGGER, log)
                .with(Globals.LOG, log)
                .with(Globals.REQUEST, context.getRequest())
                .with(Globals.RESPONSE, context.getResponse())
                        // .with(Globals.CONTEXT, context) <-- don't, please!
                .with(Globals.SITE, site)
                .with(Globals.PATH_INFO, pathInfo);

        for (Repository repository : site.getRepositoryManager().getRepositories()) {
            context.with(repository.getName(), repository.getRepositoryWrapper(context));
        }

        RackResponse response = new RackResponse(RackResponseUtils.ReturnCode.OK)
                //.withContentType(Mime.mimeType(ZZZZZZZZZZZZ))
                .withContentLength(0);

        context.setRackResponse(response);

        callHelpers(site.getHelperManager().getBeforeHelpers(), context);

        if (!context.isHalt()) {
            String path = input.get(JRack.PATH_INFO);
            if (StringUtils.isBlank(path)) {
                path = input.get(Rack.SCRIPT_NAME);
            }

            site.getRouteManager().call(path, context);

            if (!context.isHalt()) {
                path = maybeAppendHtmlToPath(context);
                context.with(Globals.PATH, path.replace("//", SLASH));
                final String contentType = PathUtilities.extractType((String) context.get(Globals.PATH));

                String templateName = StringUtils.defaultString(context.getTemplateName(),
                        RepositoryManager.DEFAULT_TEMPLATE_NAME);

                // calculate the Template name
                View view = (View) context.get(Globals.VIEW);
                if (view != null && StringUtils.isNotBlank(view.getTemplate())) {
                    templateName = view.getTemplate();
                } else {
                    View contentView = site.getRepositoryManager().getDefaultRepository().getView(path);
                    if (contentView != null && contentView.getTemplate() != null) {
                        view = contentView; // !!!!!!!!!!!!!!!! usr me << TODO
                        templateName = contentView.getTemplate();
                    }
                }

                String out = site.getRepositoryManager().getTemplatesRepository().getRepositoryWrapper(context).get(
                        templateName + contentType);

                response .withContentType(Mime.mimeType(contentType))
                        .withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                        .withBody(out);

                // 404
                //  return new RackResponse(HttpServletResponse.SC_NOT_FOUND)
                //          .withContentType(RackResponseUtils.CONTENT_TYPE_TEXT_HTML)
                //          .withBody(EMPTY_STRING);

            }

            if (!context.isHalt()) {
                callHelpers(site.getHelperManager().getAfterHelpers(), context);
            } else {
            }
        } else {

        }
        return response;
    }

    private String maybeAppendHtmlToPath(MicroContext context) {
        final Context rackInput = context.getRackInput();

        String path = (String) rackInput.get(JRack.PATH_INFO);
        if (StringUtils.isBlank(path)) {
            path = (String) rackInput.get(Rack.SCRIPT_NAME);
        }

        if (isFilterAddsWelcomeFile() && !path.contains(HTML)) {
            if (path.lastIndexOf(DOT) == -1) {
                if (!path.endsWith(SLASH)) {
                    path = path + SLASH;
                }
                path = path + INDEX + DOT + HTML;
            }
            context.with(Globals.PATH_INFO, path);
        }
        return path;
    }

    private void callHelpers(List<Helper> helpers, MicroContext context) {
        if (!helpers.isEmpty()) {
            for (Helper helper : helpers) {
                try {
                    helper.call(context);
                    if (context.isHalt()) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("Helper: %s, error: %s", helper, e.getMessage()));
                }
            }
        }
    }

    private void configureBSF() {
        BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", new String[]{"bsh"});
        BSFManager.registerScriptingEngine("groovy", "org.codehaus.groovy.bsf.GroovyEngine",
                new String[]{"groovy", "gy"});
        BSFManager.registerScriptingEngine("jruby19", "org.jruby.embed.bsf.JRubyEngine", new String[]{"ruby", "rb"});
    }

    private void showBanner() {
        log.info("");
        log.info(" _ __ ___ ( ) ___ _ __ ___ ");
        log.info("| '_ ` _ \\| |/ __| '__/ _ \\ ");
        log.info("| | | | | | | (__| | | (_) |");
        log.info("|_| |_| |_|_|\\___|_|  \\___/  (v" + Globals.VERSION + ")");
        log.info("= a modular micro MVC framework for Java");
        log.info("");
    }


    public boolean isFilterAddsWelcomeFile() {
        final String aTrue = "true";
        return welcomeFile.equalsIgnoreCase(aTrue);
    }
}
