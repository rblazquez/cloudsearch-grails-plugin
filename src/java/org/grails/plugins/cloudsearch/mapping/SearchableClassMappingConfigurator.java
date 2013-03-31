/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grails.plugins.cloudsearch.mapping;

import groovy.util.ConfigObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.grails.plugins.cloudsearch.CloudSearchContextHolder;

import com.amazonaws.services.cloudsearch.AmazonCloudSearch;

/**
 * Build searchable mappings, configure CloudSearch indexes,
 * build and install CloudSearch mappings.
 */
public class SearchableClassMappingConfigurator {

    private static final Logger LOG = Logger.getLogger(SearchableClassMappingConfigurator.class);

    private CloudSearchContextHolder cloudSearchContext;
    private GrailsApplication grailsApplication;
    private AmazonCloudSearch cloudSearchClient;
    private ConfigObject config;

    /**
     * Init method.
     */
    public void configureAndInstallMappings() {
        Collection<SearchableClassMapping> mappings = buildMappings();
        installMappings(mappings);
    }

    /**
     * Resolve the CloudSearch mapping from the static "searchable" property (closure or boolean) in domain classes
     * @param mappings searchable class mappings to be install.
     */
    public void installMappings(Collection<SearchableClassMapping> mappings) {
/*        Set<String> installedIndices = new HashSet<String>();
        Map<String, Object> settings = new HashMap<String, Object>();
//        settings.put("number_of_shards", 5);        // must have 5 shards to be Green.
//        settings.put("number_of_replicas", 2);
        settings.put("number_of_replicas", 0);
        // Look for default index settings.
        Map esConfig = (Map) ConfigurationHolder.getConfig().getProperty("cloudSearch");
        if (esConfig != null) {
            @SuppressWarnings({"unchecked"})
            Map<String, Object> indexDefaults = (Map<String, Object>) esConfig.get("index");
            LOG.debug("Retrieved index settings");
            if (indexDefaults != null) {
                for(Map.Entry<String, Object> entry : indexDefaults.entrySet()) {
                    settings.put("index." + entry.getKey(), entry.getValue());
                }
            }
        }
        LOG.debug("Installing mappings...");
        for(SearchableClassMapping scm : mappings) {
            if (scm.isRoot()) {
                Map cloudMapping = CloudSearchMappingFactory.getElasticMapping(scm);

                // todo wait for success, maybe retry.
                // If the index does not exist, create it
                if (!installedIndices.contains(scm.getIndexName())) {
                    LOG.debug("Index " + scm.getIndexName() + " does not exists, initiating creation...");
                    try {
                        // Could be blocked on index level, thus wait.
                        try {
                            LOG.debug("Waiting at least yellow status on " + scm.getIndexName() + " ...");
                            cloudSearchClient.admin().cluster().prepareHealth(scm.getIndexName())
                                    .setWaitForYellowStatus()
                                    .execute().actionGet();
                        } catch (Exception e) {
                            // ignore any exceptions due to non-existing index.
                            LOG.debug("Index health", e);
                        }
                        cloudSearchClient.admin().indices().prepareCreate(scm.getIndexName())
                                .setSettings(settings)
                                .execute().actionGet();
                        installedIndices.add(scm.getIndexName());
                        LOG.debug(elasticMapping.toString());

                        // If the index already exists, ignore the exception
                    } catch (IndexAlreadyExistsException iaee) {
                        LOG.debug("Index " + scm.getIndexName() + " already exists, skip index creation.");
                    } catch (RemoteTransportException rte) {
                        LOG.debug(rte.getMessage());
                    }
                }

                // Install mapping
                // todo when conflict is detected, delete old mapping (this will delete all indexes as well, so should warn user)
                if (LOG.isDebugEnabled()) {
                    LOG.debug("[" + scm.getElasticTypeName() + "] => " + elasticMapping);
                }
                cloudSearchClient.admin().indices().putMapping(
                        new PutMappingRequest(scm.getIndexName())
                                .type(scm.getElasticTypeName())
                                .source(elasticMapping)
                ).actionGet();
            }

        }

        ClusterHealthResponse response = cloudSearchClient.admin().cluster().health(new ClusterHealthRequest().waitForYellowStatus()).actionGet();
        LOG.debug("Cluster status: " + response.getStatus());*/
    }

    private Collection<SearchableClassMapping> buildMappings() {
        List<SearchableClassMapping> mappings = new ArrayList<SearchableClassMapping>();
        for(GrailsClass clazz : grailsApplication.getArtefacts(DomainClassArtefactHandler.TYPE)) {
            GrailsDomainClass domainClass = (GrailsDomainClass) clazz;
            SearchableDomainClassMapper mapper = new SearchableDomainClassMapper(grailsApplication, domainClass, config);
            SearchableClassMapping searchableClassMapping = mapper.buildClassMapping();
            if (searchableClassMapping != null) {
                cloudSearchContext.addMappingContext(searchableClassMapping);
                mappings.add(searchableClassMapping);
            }
        }

        // Inject cross-referenced component mappings.
        for(SearchableClassMapping scm : mappings) {
            for(SearchableClassPropertyMapping scpm : scm.getPropertiesMapping()) {
                if (scpm.isComponent()) {
                    Class<?> componentType = scpm.getGrailsProperty().getReferencedPropertyType();
                    scpm.setComponentPropertyMapping(cloudSearchContext.getMappingContextByType(componentType));
                }
            }
        }

        // Validate all mappings to make sure any cross-references are fine.
        for(SearchableClassMapping scm : mappings) {
            scm.validate(cloudSearchContext);
        }

        return mappings;
    }

    public void setCloudSearchContext(CloudSearchContextHolder cloudSearchContext) {
        this.cloudSearchContext = cloudSearchContext;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    public void setCloudSearchClient(AmazonCloudSearch cloudSearchClient) {
        this.cloudSearchClient = cloudSearchClient;
    }

    public void setConfig(ConfigObject config) {
        this.config = config;
    }
}
