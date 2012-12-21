package ca.simplegames.micro;

import bsh.BshClassManager;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.utils.ClassUtils;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;
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
    private Logger log = LoggerFactory.getLogger(getClass());

    FilterConfig filterConfig;
    private SiteContext site;

    @Override
    public Object init(Object config) throws Exception {
        filterConfig = (FilterConfig) config;
        final ServletContext servletContext = filterConfig.getServletContext();
        final String realPath = servletContext.getRealPath("/");
        final File contextPath = new File(realPath);
        final File webInfPath = new File(contextPath, "WEB-INF");

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
        input.with(Globals.MICRO_SITE, site);
        input.with(Rack.RACK_LOGGER, log);
        List<Helper> helpers = site.getHelpers();

        MicroContext context = new MicroContext<String>();
        context.with(Globals.RACK_INPUT, input)
                .with(Globals.MICRO_SITE, site)
                .with(Rack.RACK_LOGGER, log)
                .with(Globals.PATH_INFO, input.get(Rack.PATH_INFO));

        if (!helpers.isEmpty()) {
            for (Helper helper : helpers) {
                try {
                    helper.call(context);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("Helper: %s, error: %s", helper.getName(), e.getMessage()));
                }
            }
        }

        for(Repository repository: site.getRepositoryManager().getRepositories()){
            context.with(repository.getName(), repository.getRepositoryWrapper(context));
        }

        String out = site.getRepositoryManager().getTemplatesRepository()
                .getRepositoryWrapper(context).get("default.html");

        RackResponse response = new RackResponse(RackResponseUtils.ReturnCode.OK)
                .withContentType("text/html;charset=utf-8")
                .withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                .withBody(out);

        return response;
    }

    private void configureBSF() {
        BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", new String[]{"bsh"});
        BSFManager.registerScriptingEngine("groovy", "org.codehaus.groovy.bsf.GroovyEngine",
                new String[]{"groovy", "gy"});
        BSFManager.registerScriptingEngine("jruby19", "org.jruby.embed.bsf.JRubyEngine", new String[]{"ruby", "rb"});
    }

    private void showBanner() {

    }
}
