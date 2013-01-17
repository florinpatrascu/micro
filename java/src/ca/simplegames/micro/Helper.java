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

package ca.simplegames.micro;

import java.util.Map;

/**
 * The Helper interface
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2013-01-15 11:08 PM)
 */
public abstract class Helper {
    private String name;
    private String description;
    private SiteContext site;
    private Map<String, Object> cpnfiguration;

    /**
     * The Micro framework calls this method at startup right after the repositories were defined.
     *
     * @param name          the name of the file this extension was loaded from, it is later used by Micro to make this
     *                      helper visible in the context.
     * @param site          the Micro "site" instance, see: {@link ca.simplegames.micro.SiteContext}
     * @param configuration a Map containing a keys and objects useful to initialize this object
     * @return self
     * @throws Exception in case something wrong happens
     */
    public Helper register(String name, SiteContext site, Map<String, Object> configuration) throws Exception{
        this.site = site;
        this.name = name;
        this.cpnfiguration = configuration;
        return this;
    }

    /**
     * this method receives a request context and implements the business logic of the Helper
     *
     * @param context see {@link MicroContext}
     * @return an Object defined by the Developer of this helper
     * @throws Exception
     */
    // ??????????? public Object call(MicroContext context) throws Exception;

    /**
     * @return the name of this helper
     */
    public String getName(){
        return name;
    }

    /**
     * @return the description of this helper
     */
    public String getDescription(){
        return description;
    }

    /**
     * @return the configuration object received at registration
     */
    public Map<String, Object> getConfiguration(){
        return cpnfiguration;
    }
}
