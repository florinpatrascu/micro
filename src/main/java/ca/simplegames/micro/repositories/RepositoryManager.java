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

import java.io.File;
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
    private SiteContext site;

    @SuppressWarnings("unchecked")
    public RepositoryManager(SiteContext site) {
        this.site = site;

        addRepositories((Map<String, Object>) site.getAppConfig().get("repositories"));
        site.with(Globals.MICRO_REPOSITORY_MANAGER, this);
    }


    @SuppressWarnings("unchecked")
    public void addRepositories(File rootPath, Map<String, Object> repos) {
        try {
            if (!CollectionUtils.isEmpty(repos)) {
                for (String repoName : repos.keySet()) {
                    Map<String, Object> repoConfig = (Map<String, Object>) repos.get(repoName);

                    String repoPathName = (String) repoConfig.get("path");
                    String repoPath = StringUtils.defaultString(
                            rootPath != null && rootPath.exists() ?
                                    new File(rootPath, repoPathName).getAbsolutePath()
                                    : repoPathName, Globals.EMPTY_STRING);

                    Repository repository = new FSRepository(repoName,
                            site.getCacheManager().getCache(StringUtils.defaultString(repoConfig.get("cache"),
                                    Globals.EMPTY_STRING)), site,
                            repoPath,
                            StringUtils.defaultString(
                                    repoConfig.get(Globals.DEFAULT_REPOSITORY_CONFIG_PATH_NAME),
                                    Globals.DEFAULT_REPOSITORY_CONFIG_PATH_NAME),
                            (String) repoConfig.get("engine")
                    );

                    repository.setIsDefault(StringUtils.defaultString(repoConfig.get("default"), "false")
                            .equalsIgnoreCase("true"));

                    if (repository.isDefault()) {
                        defaultRepository = repository;
                    }

                    if (repoName.equalsIgnoreCase(TEMPLATES)) {
                        templatesRepository = repository;
                    }

                    repositories.add(repository);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("defining the repositories; " + e.getMessage());
        }

    }

    /**
     * define a group of repository
     *
     * @param repos a Map containing the repository definitions declared by the
     *              "repositories" element from micro-config.yml
     */
    @SuppressWarnings("unchecked")
    public void addRepositories(Map<String, Object> repos) {
        addRepositories(null, repos);
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
        if (name != null && !repositories.isEmpty()) {
            for (Repository repository : repositories) {
                if (repository.getName().equalsIgnoreCase(name)) {
                    return repository;
                }
            }
        }
        return null;
    }
}
