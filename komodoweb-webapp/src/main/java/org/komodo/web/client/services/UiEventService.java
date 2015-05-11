/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.web.client.services;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventListener;

/**
 * Local service responsible for firing events
 * 
 */
@ApplicationScoped
public class UiEventService {

    @Inject
    private Event<UiEvent> uiEvent;
    
    private Collection<UiEventListener> listeners = new ArrayList<UiEventListener>();

    /**
     * @return the service instance
     */
    public static UiEventService get() {
        Collection<IOCBeanDef<UiEventService>> beans = IOC.getBeanManager().lookupBeans(UiEventService.class);
        IOCBeanDef<UiEventService> beanDef = beans.iterator().next();
        return beanDef.getInstance();
    }
    
	/**
	 * Constructor.
	 */
	public UiEventService() {
	}
	
	/**
	 * Add a Listener
	 * @param listener the UiEventListener to register
	 */
	public void addListener(UiEventListener listener) {
		if(!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}
	
	/**
	 * Remove a Listener
	 * @param listener the UiEventListener to register
	 */
	public void removeListener(UiEventListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Fires a UiEvent
	 * @param event the UiEvent
	 */
	public void fire(UiEvent event) {
		uiEvent.fire(event);
	}
	
	/**
	 * handles UiEvents
	 * @param event the UiEvent
	 */
	public void handleEvent(@Observes UiEvent event) {
		for(UiEventListener listener : listeners) {
			listener.handleUiEvent(event);
		}
	}
}
