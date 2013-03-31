package org.grails.plugins.cloudsearch

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.grails.plugins.cloudsearch.mapping.SearchableClassMapping

class CloudSearchContextHolder {
    /**
     * The configuration of the CloudSearch plugin
     */
    ConfigObject config

    /**
     * A map containing the mapping to CloudSearch
     */
    Map<String, SearchableClassMapping> mapping = [:]

    /**
     * Setter for dependency injection
     * @param config
     */
    public void setConfig(ConfigObject config) {
        this.config = config
    }

    /**
     * Adds a mapping context to the current mapping holder
     *
     * @param scm The SearchableClassMapping instance to add
     */
    public void addMappingContext(SearchableClassMapping scm) {
        mapping[scm.domainClass.fullName] = scm
    }

    /**
     * Returns the mapping context for a peculiar type
     * @param type
     * @return
     */
    SearchableClassMapping getMappingContext(String type) {
        mapping[type]
    }

    /**
     * Returns the mapping context for a peculiar GrailsDomainClass
     * @param domainClass
     * @return
     */
    SearchableClassMapping getMappingContext(GrailsDomainClass domainClass) {
        mapping[domainClass.fullName]
    }

    /**
     * Returns the mapping context for a peculiar Class
     *
     * @param clazz
     * @return
     */
    SearchableClassMapping getMappingContextByType(Class clazz) {
        mapping.values().find { scm -> scm.domainClass.clazz == clazz }
    }

    /**
     * Determines if a Class is root-mapped by the CloudSearch plugin
     *
     * @param clazz
     * @return A boolean determining if the class is root-mapped or not
     */
    def isRootClass(Class clazz) {
        mapping.values().any { scm -> scm.domainClass.clazz == clazz && scm.root }
    }

    /**
     * Returns the Class that is associated to a specific CloudSearch type
     *
     * @param CloudTypeName
     * @return A Class instance or NULL if the class was not found
     */
    Class findMappedClassByElasticType(String elasticTypeName) {
        mapping.values().find { scm -> scm.elasticTypeName == elasticTypeName }?.domainClass?.clazz
    }
}
