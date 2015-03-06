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
package org.komodo.web.client.panels.repo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.resources.AppResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ToggleButton;

@Dependent
@Templated("./RepoDefinitionPanel.html")
public class RepoDefinitionPanel extends Composite {
    
    @Inject @DataField("textarea-repo")
    protected TextArea repoTextArea;
    
    @Inject @DataField("panel-filter-buttons")
    protected FlowPanel filterButtonsPanel;
    
    @Inject @DataField("btn-add-repo")
    protected Button addRepoButton;

    @Inject @DataField("btn-remove-repo")
    protected Button removeRepoButton;
    
	@Inject Event<UiEvent> toggleEvent;
	
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	AppResource.INSTANCE.css().filterToggleStyle().ensureInjected();
    	
    	// Repo list area
    	repoTextArea.setText("Local Repository");
    	
    	// Create the filter toggle buttons
        ImageResource filterConnectionsImg = AppResource.INSTANCE.images().filterConnectionsImage();
        final ToggleButton filterConnectionsButton = new ToggleButton(new Image(filterConnectionsImg));
        filterConnectionsButton.setStylePrimaryName("filterToggle");
    	DOM.setStyleAttribute(filterConnectionsButton.getElement(), "cssFloat", "left");
    	DOM.setStyleAttribute(filterConnectionsButton.getElement(), "margin", "5px");
    	DOM.setStyleAttribute(filterConnectionsButton.getElement(), "padding", "0px");
    	
        ImageResource filterViewsImg = AppResource.INSTANCE.images().filterViewsImage();
        final ToggleButton filterViewsButton = new ToggleButton(new Image(filterViewsImg));
        filterViewsButton.setStylePrimaryName("filterToggle");
    	DOM.setStyleAttribute(filterViewsButton.getElement(), "cssFloat", "left");
    	DOM.setStyleAttribute(filterViewsButton.getElement(), "margin", "5px");
    	DOM.setStyleAttribute(filterViewsButton.getElement(), "padding", "0px");

        ImageResource filterVdbsImg = AppResource.INSTANCE.images().filterVdbsImage();
        final ToggleButton filterVdbsButton = new ToggleButton(new Image(filterVdbsImg));
        filterVdbsButton.setStylePrimaryName("filterToggle");
    	DOM.setStyleAttribute(filterVdbsButton.getElement(), "cssFloat", "left");
    	DOM.setStyleAttribute(filterVdbsButton.getElement(), "margin", "5px");
    	DOM.setStyleAttribute(filterVdbsButton.getElement(), "padding", "0px");
    	
    	// Initial state is pressed
    	filterConnectionsButton.setDown(true);
    	filterViewsButton.setDown(true);
    	filterVdbsButton.setDown(true);
    	
    	// button handlers
        filterConnectionsButton.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if(filterConnectionsButton.isDown()) {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_SHOW_CONNECTIONS));
    			} else {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_HIDE_CONNECTIONS));
    		    }
    		}
    	}); 
        filterViewsButton.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if(filterViewsButton.isDown()) {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_SHOW_VIEWS));
    			} else {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_HIDE_VIEWS));
    		    }
    		}
    	});                	
        filterVdbsButton.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if(filterVdbsButton.isDown()) {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_SHOW_VDBS));
    			} else {
            		toggleEvent.fire(new UiEvent(UiEventType.REPO_HIDE_VDBS));
    		    }
    		}
    	});
        
        filterButtonsPanel.add(filterConnectionsButton);
        filterButtonsPanel.add(filterViewsButton);
        filterButtonsPanel.add(filterVdbsButton);
    }
    
    /**
     * Event handler that fires when the user clicks the Add Repo button
     * @param event
     */
    @EventHandler("btn-add-repo")
    public void onAddRepoButtonClick(ClickEvent event) {
    	doAddRepo();
    }

    /**
     * Event handler that fires when the user clicks the Remove Repo button
     * @param event
     */
    @EventHandler("btn-remove-repo")
    public void onRemoveRepoButtonClick(ClickEvent event) {
    	doRemoveRepo();
    }
    
    private void doAddRepo() {
    	Window.alert("Add a repository");
    }

    private void doRemoveRepo() {
    	Window.alert("Remove a repo");
    }
    
}