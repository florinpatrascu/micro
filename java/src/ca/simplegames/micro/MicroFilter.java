package ca.simplegames.micro;

import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.ClassUtils;
import org.apache.bsf.BSFManager;
import org.apache.commons.lang3.StringUtils;
import org.jrack.*;
import org.jrack.context.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
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
    public static final String HTML_EXTENSION = ".html";
    private Logger log = LoggerFactory.getLogger(getClass());

    FilterConfig filterConfig;
    private SiteContext site;
    private String welcomeFile;

    @Override
    public Object init(Object config) throws Exception {
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
        showBanner();
        return this;
    }

    public RackResponse call(Context<String> input) {
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
                .with(Globals.CONTEXT, context)
                .with(Globals.SITE, site)
                .with(Globals.PATH_INFO, pathInfo);

        String path = maybeAppendHtmlToPath(context);
        context.with(Globals.PATH, path.replace("//", SLASH));
        log.info(">>>> " + context.get(Globals.PATH));

        for (Repository repository : site.getRepositoryManager().getRepositories()) {
            context.with(repository.getName(), repository.getRepositoryWrapper(context));
        }

        RackResponse response = new RackResponse(RackResponseUtils.ReturnCode.OK);
        context.with(Globals.RACK_RESPONSE, response);

        callHelpers(site.getHelperManager().getBeforeHelpers(), context);
        if (!context.isHalt()) {
            callHelpers(site.getHelperManager().getHelpers(), context);

            if (!context.isHalt()) {
                String out = site.getRepositoryManager().getTemplatesRepository()
                        .getRepositoryWrapper(context).get("default.html");

                response.withContentType("text/html;charset=utf-8")
                        .withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                        .withBody(out);
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

        if (isFilterAddsWelcomeFile() && !path.contains(HTML_EXTENSION)) {
            if (path.lastIndexOf(DOT) == -1) {
                if (!path.endsWith(SLASH)) {
                    path = path + SLASH;
                }
                path = path + INDEX + HTML_EXTENSION;
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
                    log.error(String.format("Helper: %s, error: %s", helper.getName(), e.getMessage()));
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

    }


    public boolean isFilterAddsWelcomeFile() {
        final String aTrue = "true";
        return welcomeFile.equalsIgnoreCase(aTrue);
    }
}
