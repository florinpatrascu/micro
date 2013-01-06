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

package ca.simplegames.micro.repositories;

import ca.simplegames.micro.Globals;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.utils.CollectionUtils;
import ca.simplegames.micro.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-20 6:00 PM)
 */
public class RepositoryManager {
    public static final String TEMPLATES = "templates";
    public static final String DEFAULT_TEMPLATE_NAME = "default";
    private Logger log = LoggerFactory.getLogger(getClass());
    private Repository defaultRepository;
    private Repository templatesRepository;
    private List<Repository> repositories = new ArrayList<Repository>();

    @SuppressWarnings("unchecked")
    public RepositoryManager(SiteContext site) {
        try {
            Map<String, Object> repos = (Map<String, Object>) site.getAppConfig().get("repositories");
            if (!CollectionUtils.isEmpty(repos)) {
                for (String repoName : repos.keySet()) {
                    Map<String,Object>repoConfig = (Map<String, Object>) repos.get(repoName);
                    Repository repository = new FSRepository(repoName,
                            site.getCacheManager().getCache(StringUtils.defaultString(repoConfig.get("cache"),
                                    Globals.EMPTY_STRING)), site,
                            StringUtils.defaultString(repoConfig.get("path"), Globals.EMPTY_STRING),
                            StringUtils.defaultString(
                                    repoConfig.get(Globals.DEFAULT_REPOSITORY_CONFIG_PATH_NAME),
                                    Globals.DEFAULT_REPOSITORY_CONFIG_PATH_NAME)
                    );

                    repository.setIsDefault(StringUtils.defaultString(repoConfig.get("default"),"false")
                            .equalsIgnoreCase("true"));

                    if(repository.isDefault()){
                        defaultRepository = repository;
                    }

                    if(repoName.equalsIgnoreCase(TEMPLATES)){
                        templatesRepository = repository;
                    }

                    repositories.add(repository);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("defining the repositories; " + e.getMessage());
        }

        site.with(Globals.MICRO_REPOSITORY_MANAGER, this);
    }

    public Repository getDefaultRepository() {
        return defaultRepository;
    }

    public Repository getTemplatesRepository() {
        return templatesRepository;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public Repository getRepository(String name) {
        if(name!= null && !repositories.isEmpty()){
          for(Repository repository: repositories){
              if(repository.getName().equalsIgnoreCase(name)){
                  return repository;
              }
          }
        }
        return null;
    }
}
