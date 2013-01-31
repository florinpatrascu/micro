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

package ca.simplegames.micro.route;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.Route;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.controllers.ControllerNotFoundException;
import ca.simplegames.micro.repositories.Repository;
import ca.simplegames.micro.repositories.RepositoryManager;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.PathUtilities;
import ca.simplegames.micro.utils.StringUtils;
import ca.simplegames.micro.viewers.ViewException;
import org.jrack.RackResponse;
import org.jrack.utils.Mime;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-28 11:08 AM)
 */
public class RouteWrapper extends Route {


    /**
     * Constructor
     *
     * @param route  The route route which is used for matching. (e.g. /hello, users/{name})
     * @param config a map containing nodes in a configuration loaded from an external support,
     *               an .yml file for example?!
     */
    public RouteWrapper(String route, Map<String, Object> config) {
        super(route, config);
    }

    /**
     * the View support is not yet finalized; work in progress
     *
     *
     * @param context The micro context created when the Rack calls
     * @return
     * @throws ControllerNotFoundException
     * @throws ControllerException
     * @throws FileNotFoundException
     * @throws ViewException
     */
    @Override
    public RackResponse call(MicroContext context) throws Exception {

        if (context != null) {
            SiteContext site = context.getSiteContext();

            if (!CollectionUtils.isEmpty(getControllers())) {
                for (int i = 0; i < getControllers().size(); i++) {
                    Map<String, Object> controllerMap = getControllers().get(i);
                    site.getControllerManager().execute((String) controllerMap.get(Globals.NAME),
                            context, (Map) controllerMap.get(Globals.OPTIONS));
                    if (context.isHalt()) return context.getRackResponse();
                }
            }

            if (getView() != null && getView().getPath() != null) {
                RackResponse response = context.getRackResponse();
                RepositoryManager repositoryManager = site.getRepositoryManager();
                String repositoryName = StringUtils.defaultString(getView().getRepositoryName(),
                        repositoryManager.getDefaultRepository().getName());

                String path = getView().getPath();

                context.with(Globals.PATH, path);

                String out;
                if (getView().getTemplate() != null) {
                    out = repositoryManager.getTemplatesRepository().getRepositoryWrapper(context)
                            .get(getView().getTemplate() + PathUtilities.extractType(path));
                } else {
                    Repository repository = repositoryManager.getRepository(repositoryName);
                    out = repository.getRepositoryWrapper(context).get(getView().getPath());
                }

                String contentType = Mime.mimeType(PathUtilities.extractType(path));

                if (response.getHeaders().get(Globals.HEADERS_CONTENT_TYPE) != null) {
                    contentType = response.getHeaders().get(Globals.HEADERS_CONTENT_TYPE);
                }

                response.withContentType(contentType)
                        .withContentLength(out.getBytes(Charset.forName(Globals.UTF8)).length)
                        .withBody(out);
            }
        }
        return null;
    }
}
