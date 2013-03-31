package org.grails.plugins.cloudsearch

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
	
	@Override
	public void onPostDelete(PostDeleteEvent event) {
		LOG.debug("onPostDelete: ${event}")
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		LOG.debug("onPostInsert: ${event}")
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		LOG.debug("onPostUpdate: ${event}")
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
