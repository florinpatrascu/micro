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

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Micro as a web server using embedded Jetty.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-11 7:28 PM)
 */
public class WebServer {
    protected static final Logger log = LoggerFactory.getLogger(WebServer.class);

    /**
     * Will start the embedded Jetty server and will initialize the Micro web app. The web app is a
     * standard exploded .war structure
     *
     * @param args expecting the path to the Micro web app and optionally a port number
     */
    public static void main(String[] args) throws Exception {

        if (args != null && args.length > 0) {

            String path = args[0];
            int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

            try {

                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setHost("127.0.0.1"); //the loneliest number
                connector.setPort(port);
                connector.setThreadPool(new QueuedThreadPool(20));
                connector.setMaxIdleTime(1000 * 60 * 60); // this will make debugging easier

                Server server = new Server(port);
                server.setConnectors(new Connector[]{connector});
                WebAppContext webApp = new WebAppContext(path, "/");

                ServletHandler handler = new ServletHandler();
                webApp.setServletHandler(handler);

                server.setHandler(webApp);
                server.start();
                server.join();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            log.error("You must specify the path to the directory containing the Micro web application.");
        }
    }
}