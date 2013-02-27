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
import ca.simplegames.micro.helpers.HelperWrapper;
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

    public static final String TOOLS = "Tools";
    public static Context tools = new MicroContext()
            .with("PathUtilities", new PathUtilities())
            .with("StringUtils", new StringUtils());

    private SiteContext site;
    private String welcomeFile;

    public Micro(String path, ServletContext servletContext, String userClassPaths) throws Exception {
        final File applicationPath = new File(path);
        final File webInfPath = new File(applicationPath, "WEB-INF");

        showBanner();

        site = new SiteContext(new MapContext<String>()
                .with(Globals.SERVLET_CONTEXT, servletContext)
                .with(Globals.SERVLET_PATH_NAME, path)
                .with(Globals.SERVLET_PATH, applicationPath)
                .with(Globals.WEB_INF_PATH, webInfPath)
        );

        welcomeFile = site.getWelcomeFile();

        //initialize the classpath
        StringBuilder cp = new StringBuilder();
        if (new File(webInfPath, "/lib").exists()) {
            cp.append(webInfPath.toString()).append("/lib,");
        }

        if (new File(webInfPath, "/classes").exists()) {
            cp.append(webInfPath.toString()).append("/classes,");
        }

        if (StringUtils.isNotBlank(userClassPaths)) {
            cp.append(",").append(userClassPaths);
        }

        String resources = ClassUtils.configureClasspath(webInfPath.toString(),
                StringUtils.split(cp.toString(), ",:"));
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
                .with(Globals.PATH_INFO, pathInfo)
                .with(TOOLS, tools);

        input.with(Globals.CONTEXT, context); // mostly for helping the testing effort

        for (Repository repository : site.getRepositoryManager().getRepositories()) {
            context.with(repository.getName(), repository.getRepositoryWrapper(context));
        }

        RackResponse response = new RackResponse(RackResponseUtils.ReturnCode.OK)
                .withContentType(null)
                .withContentLength(0);  // a la Sinatra, they're doing it right

        context.setRackResponse(response);

        try {
            // inject the Helpers into the current context
            List<HelperWrapper> helpers = site.getHelperManager().getHelpers();
            if (!helpers.isEmpty()) {
                for (HelperWrapper helper : helpers) {
                    if (helper != null) {
                        context.with(helper.getName(), helper.getInstance(context));
                    }
                }
            }

            if (site.getFilterManager() != null) {
                callFilters(site.getFilterManager().getBeforeFilters(), context);
            }

            if (!context.isHalt()) {
                String path = input.get(JRack.PATH_INFO);
                if (StringUtils.isBlank(path)) {
                    path = input.get(Rack.SCRIPT_NAME);
                }

                if (site.getRouteManager() != null) {
                    site.getRouteManager().call(path, context);
                }

                if (!context.isHalt()) {
                    path = (String) context.get(Globals.PATH);
                    if (path == null) { // user not deciding the PATH
                        path = maybeAppendHtmlToPath(context);
                        context.with(Globals.PATH, path.replace("//", SLASH));
                    }

                    final String pathBasedContentType = PathUtilities.extractType((String) context.get(Globals.PATH));

                    String templateName = StringUtils.defaultString(context.getTemplateName(),
                            RepositoryManager.DEFAULT_TEMPLATE_NAME);

                    Repository defaultRepository = site.getRepositoryManager().getDefaultRepository();
                    // verify if there is a default repository decided by 3rd party components; controllers, extensions, etc.
                    if (context.getDefaultRepositoryName() != null) {
                        defaultRepository = site.getRepositoryManager()
                                .getRepository(context.getDefaultRepositoryName());
                    }

                    // calculate the Template name
                    View view = (View) context.get(Globals.VIEW);
                    if (view != null && StringUtils.isNotBlank(view.getTemplate())) {
                        templateName = view.getTemplate();
                    } else {
                        view = defaultRepository.getView(path);
                        if (view != null && view.getTemplate() != null) {
                            templateName = view.getTemplate();
                        }
                    }

                    // Render the Default Template. The template will pull out the View, the result being sent out as
                    // the Template body merged with the View's own content. Controllers are executed *before*
                    // rendering the Template *and before* rendering the View, but only if there are any View or Template
                    // Controllers defined by the user.

                    Repository templatesRepository = site.getRepositoryManager().getTemplatesRepository();
                    if (context.getTemplatesRepositoryName() != null) {
                        templatesRepository = site.getRepositoryManager()
                                .getRepository(context.getTemplatesRepositoryName());
                    }

                    if (templatesRepository != null) {
                        String out = templatesRepository.getRepositoryWrapper(context)
                                .get(templateName + pathBasedContentType);

                        response.withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                                .withBody(out);
                    } else {
                        throw new FileNotFoundException(String.format("templates repository: %s", context.getTemplatesRepositoryName()));
                    }
                }

                if (!context.isHalt()) {
                    if (site.getFilterManager() != null) {
                        callFilters(site.getFilterManager().getAfterFilters(), context);
                    }
                }
            }

            return context.getRackResponse().withContentType(getContentType(context));

        } catch (ControllerNotFoundException e) {
            context.with(Globals.ERROR, e);
            return badJuju(context, HttpServletResponse.SC_NO_CONTENT, e);
        } catch (ControllerException e) {
            context.with(Globals.ERROR, e);
            return badJuju(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        } catch (FileNotFoundException e) {
            context.with(Globals.ERROR, e);
            return badJuju(context, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (ViewException e) {
            context.with(Globals.ERROR, e);
            return badJuju(context, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }catch (RedirectException re){
            return context.getRackResponse();
        } catch (Exception e) { // must think more about this one :(
            context.with(Globals.ERROR, e);
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
        log.info("|_| |_| |_|_|\\___|_|  \\___/  (" + Globals.VERSION + ")");
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

        if (welcomeFile.isEmpty() || path.contains(HTML)) {
            return path;
        }

        if (path.lastIndexOf(DOT) == -1) {
            if (!path.endsWith(SLASH)) {
                path = path + SLASH;
            }
            String welcomeFile = StringUtils.defaultString(site.getWelcomeFile(), INDEX + DOT + HTML);
            path = path + welcomeFile;
        }
        context.with(Globals.PATH_INFO, path);
        return path;
    }

    private void callFilters(List<Filter> filters, MicroContext context) {
        if (!filters.isEmpty()) {
            for (Filter filter : filters) {
                try {
                    filter.call(context);
                    if (context.isHalt()) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("Filter: %s, error: %s", filter, e.getMessage()));
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
