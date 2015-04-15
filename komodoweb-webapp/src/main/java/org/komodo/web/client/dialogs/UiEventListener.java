/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.web.client.dialogs;

/**
 * Interface for UiEventListeners
 */
public interface UiEventListener {
	
	/**
	 * Handle UiEvents
	 * @param event the UiEvent
	 */
	public void handleUiEvent(UiEvent event);

}
