/*
 * Copyright (c) 2013 the original author or authors.
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

package ca.simplegames.micro.utils;

import org.mortbay.log.Logger;

/**
 * basic logger for the embedded Jetty
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-23 6:42 PM)
 */
public class JettyLogger implements Logger {
    protected static final org.apache.log4j.Logger log =
            org.apache.log4j.Logger.getLogger("org.mortbay.jetty.JettyLog");

    private static final String LOG_FORMAT = "%s, %s, %s";
    private boolean debugEnabled;

    public Logger getLogger(String arg0) {
        return this;
    }

    public void info(String msg, Object arg0, Object arg1) {
        log.info(String.format(LOG_FORMAT, msg, arg0, arg1));
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    @Override
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void warn(String msg, Throwable th) {
        log.warn(msg, th);
    }

    public void warn(String msg, Object arg0, Object arg1) {
        log.warn(String.format(LOG_FORMAT, msg, arg0, arg1));
    }

    public void debug(String msg, Throwable th) {
        log.debug(msg, th);
    }

    public void debug(String msg, Object arg0, Object arg1) {
        log.debug(String.format(LOG_FORMAT, msg, arg0, arg1));
    }
}
