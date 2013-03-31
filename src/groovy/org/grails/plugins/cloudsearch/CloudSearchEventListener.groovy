package org.grails.plugins.cloudsearch

import java.util.Map;

import org.codehaus.groovy.grails.orm.hibernate.events.SaveOrUpdateEventListener
import org.hibernate.HibernateException;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostCollectionUpdateEvent;
import org.hibernate.event.PostCollectionUpdateEventListener;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.SaveOrUpdateEvent;
import org.apache.log4j.Logger

class CloudSearchEventListener extends SaveOrUpdateEventListener implements PostDeleteEventListener, PostInsertEventListener, PostUpdateEventListener, FlushEventListener, PostCollectionUpdateEventListener {

    /** Logger */
    private static final Logger LOG = Logger.getLogger(CloudSearchEventListener.class)
	
    /** CS context */
    def cloudSearchContextHolder

    /** List of pending objects to reindex. */
    private static ThreadLocal<Map> pendingObjects = new ThreadLocal<Map>()

    /** List of pending object to delete */
    private static ThreadLocal<Map> deletedObjects = new ThreadLocal<Map>()

	@Override
	public void onPostInsert(PostInsertEvent event) {
		LOG.debug("onPostInsert: ${event}")
        def clazz = event.entity?.class
        if (cloudSearchContextHolder.isRootClass(clazz)) {
            // pushToIndex(event.persister.entityName, event.id, event.entity)
        }
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		LOG.debug("onPostUpdate: ${event}")
        def clazz = event.entity?.class
        if (cloudSearchContextHolder.isRootClass(clazz)) {
            // pushToIndex(event.persister.entityName, event.id, event.entity)
        }
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		LOG.debug("onPostDelete: ${event}")
        def clazz = event.entity?.class
        if (cloudSearchContextHolder.isRootClass(clazz)) {
            // pushToDelete(event.persister.entityName, event.id, event.entity)
        }
	}

	@Override
	public void onFlush(FlushEvent event) throws HibernateException {
		LOG.debug("onFlush: ${event}")
	}

	@Override
	public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
		LOG.debug("onPostUpdateCollection: ${event}")
	}
}
