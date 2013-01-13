/*
 * Copyright (c)2013 Florin T.Pătraşcu
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

import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.utils.ClassUtils;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.PathUtilities;
import ca.simplegames.micro.viewers.ViewException;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.jrack.*;
import org.jrack.context.MapContext;
import org.jrack.utils.Mime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * A micro MVC implementation for Java web applications
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-01 4:22 PM)
 */
public class Micro {
    private Logger log = LoggerFactory.getLogger(getClass());
    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String INDEX = "index";
    public static final String HTML = "html";
    public static final String HTML_EXTENSION = DOT + HTML;
    public static final String DEFAULT_CONTENT_TYPE = Mime.mimeType(HTML_EXTENSION);

    private SiteContext site;
    private String welcomeFile;

    public Micro(String path, ServletContext servletContext, String userClassPaths, String welcomeFile) throws Exception {
        final File applicationPath = new File(path);
        final File webInfPath = new File(applicationPath, "WEB-INF");
        this.welcomeFile = welcomeFile;

        showBanner();

        site = new SiteContext(new MapContext<String>()
                .with(Globals.SERVLET_CONTEXT, servletContext)
                .with(Globals.SERVLET_PATH_NAME, path)
                .with(Globals.SERVLET_PATH, applicationPath)
                .with(Globals.WEB_INF_PATH, webInfPath)
        );

        //initialize the classpath
        StringBuilder cp = new StringBuilder();
        cp.append(webInfPath.toString()).append("/lib,");
        cp.append(webInfPath.toString()).append("/classes,");

        if (StringUtils.isNotBlank(userClassPaths)) {
            cp.append(",").append(userClassPaths);
        }

        String resources = ClassUtils.configureClasspath(webInfPath.toString(),
                StringUtils.split(cp.toString(), ",: "));
        if (log.isDebugEnabled()) {
            log.info("classpath: " + resources);
        }
        configureBSF();

        site.loadApplication(webInfPath.getAbsolutePath() + "/config");
        // done with the init phase
        //log.info("⦿‿⦿\n");
    }

    public RackResponse call(Context<String> input) {

        MicroContext context = new MicroContext<String>();

        //try {
        input.with(Globals.SITE, site);
        input.with(Rack.RACK_LOGGER, log);

        String pathInfo = input.get(Rack.PATH_INFO);
        context.with(Globals.RACK_INPUT, input)
                .with(Globals.SITE, site)
                .with(Rack.RACK_LOGGER, log)
                .with(Globals.LOG, log)
                .with(Globals.REQUEST, context.getRequest())
                .with(Globals.MICRO_ENV, site.getMicroEnv())
                        // .with(Globals.CONTEXT, context) <-- don't, please!
                .with(Globals.PARAMS, input.get(Rack.PARAMS)) //<- just a convenience
                .with(Globals.SITE, site)
                .with(Globals.PATH_INFO, pathInfo);

        for (Repository repository : site.getRepositoryManager().getRepositories()) {
            context.with(repository.getName(), repository.getRepositoryWrapper(context));
        }

        RackResponse response = new RackResponse(RackResponseUtils.ReturnCode.OK)
                .withContentType(null)
                .withContentLength(0);  // a la Sinatra, they're doing it right

        context.setRackResponse(response);

        try {
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
                    final String pathBasedContentType = PathUtilities.extractType((String) context.get(Globals.PATH));

                    String templateName = StringUtils.defaultString(context.getTemplateName(),
                            RepositoryManager.DEFAULT_TEMPLATE_NAME);

                    // calculate the Template name
                    View view = (View) context.get(Globals.VIEW);
                    if (view != null && StringUtils.isNotBlank(view.getTemplate())) {
                        templateName = view.getTemplate();
                    } else {
                        view = site.getRepositoryManager().getDefaultRepository().getView(path);
                        if (view != null && view.getTemplate() != null) {
                            templateName = view.getTemplate();
                        }
                    }

                    // Render the Default Template. The template will pull out the View, the result being sent out as
                    // the Template body merged with the View's own content. Controllers are executed *before*
                    // rendering the Template *and before* rendering the View, but only if there are any View or Template
                    // Controllers defined by the user.
                    String out = site.getRepositoryManager().getTemplatesRepository().getRepositoryWrapper(context)
                            .get(templateName + pathBasedContentType);

                    response.withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                            .withBody(out);
                }

                if (!context.isHalt()) {
                    callHelpers(site.getHelperManager().getAfterHelpers(), context);
                }
            }

            return context.getRackResponse().withContentType(getContentType(context));

        } catch (ControllerNotFoundException e) {
            return badJuju(context, HttpServletResponse.SC_NO_CONTENT, e);
        } catch (ControllerException e) {
            e.printStackTrace();
            return badJuju(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        } catch (FileNotFoundException e) {
            return badJuju(context, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (ViewException e) {
            return badJuju(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
        // Experimental!!!!!!
        //        } finally {
        //            // this is an experimental trick that will save some processing time required by BSF to load
        //            // various engines.
        //            @SuppressWarnings("unchecked")
        //            CloseableThreadLocal<BSFManager> closeableBsfManager = (CloseableThreadLocal<BSFManager>)
        //                    context.get(Globals.CLOSEABLE_BSF_MANAGER);
        //            if(closeableBsfManager!=null){
        //                closeableBsfManager.close();
        //            }
        //    }
    }

    /**
     * This method will do the following:
     * - extract the resource file extension for the given path
     * - check if there are any user defined mime types and use those otherwise
     * will use the default Mime detection mechanism {@see Mime.mimeType}
     * - if the response however has the Content-type defined already then the
     * resulting content type is the one in the response.
     *
     * @param context the current context
     * @return a String representing the content type value
     */
    @SuppressWarnings("unchecked")
    private String getContentType(MicroContext context) {
        RackResponse response = context.getRackResponse();
        String responseContentType = response.getHeaders().get(Globals.HEADERS_CONTENT_TYPE);
        final String ext = PathUtilities.extractType((String) context.get(Globals.PATH));
        String contentType = Mime.mimeType(ext);

        if (responseContentType != null) {
            contentType = responseContentType;
        } else if (site.getUserMimeTypes() != null && site.getUserMimeTypes().containsKey(ext)) {
            contentType = site.getUserMimeTypes().get(ext);
        }

        // verify the charset
        Map<String, String[]> params = (Map<String, String[]>) context.get(Globals.PARAMS);
        if (!CollectionUtils.isEmpty(params) && params.get(Globals.CHARSET) != null
                && !contentType.contains(Globals.CHARSET)) {
            contentType = String.format("%s;%s", contentType, params.get(Globals.CHARSET)[0]);
        }

        return contentType;
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
        log.info("= a modular micro MVC Java framework");
        log.info("");
    }

    public SiteContext getSite() {
        return site;
    }

    // todo improve me, por favor
    private RackResponse badJuju(MicroContext context, int status, Exception e) {
        context.with(Globals.ERROR, e);
        try {
            String baddie = site.getRepositoryManager().getTemplatesRepository()
                    .getRepositoryWrapper(context)
                    .get(status + HTML_EXTENSION);

            return new RackResponse(status).withContentType(Mime.mimeType(HTML_EXTENSION))
                    .withContentLength(baddie.getBytes(Charset.forName(Globals.UTF8)).length)
                    .withBody(baddie);

        } catch (Exception e1) {
            return new RackResponse(status)
                    .withHeader("Content-Type", (Mime.mimeType(".html")))
                    .withBody(Globals.EMPTY_STRING)
                    .withContentLength(0);
        }
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
                String welcomeFile = StringUtils.defaultString(site.getWelcomeFile(), INDEX + DOT + HTML);
                path = path + welcomeFile;
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

    public boolean isFilterAddsWelcomeFile() {
        final String aTrue = "true";
        return welcomeFile.equalsIgnoreCase(aTrue);
    }

    public static class PoweredBy {
        public String getName() {
            return Globals.FRAMEWORK_NAME;
        }

        public String getVersion() {
            return Globals.VERSION;
        }

        public String toString() {
            return String.format("%s version: %s", getName(), getVersion());
        }
    }
}
