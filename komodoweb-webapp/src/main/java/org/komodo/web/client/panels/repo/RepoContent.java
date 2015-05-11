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

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.Heading;
import org.komodo.web.client.resources.AppResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel which contains the content accordion panels
 */
@Dependent
public class RepoContent extends Composite {
    
    interface RepoContentBinder extends UiBinder<Widget, RepoContent> {}
    private static RepoContentBinder uiBinder = GWT.create(RepoContentBinder.class);

    @UiField
    Heading contentTitle;
    @UiField(provided=true)
    ToggleButton filterConnectionsButton;
    @UiField(provided=true)
    ToggleButton filterDataSourcesButton;
    @UiField(provided=true)
    ToggleButton filterViewsButton;
    @UiField(provided=true)
    ToggleButton filterVdbsButton;

    @UiField
    RepoConnectionContent connectionPanel;
    @UiField
    RepoDataSourceContent datasourcePanel;
    @UiField
    RepoViewContent viewPanel;
    @UiField
    RepoVdbContent vdbPanel;
    
    /**
     * Constructor
     */
    public RepoContent() {
    	filterConnectionsButton = new ToggleButton();
    	filterDataSourcesButton = new ToggleButton();
    	filterViewsButton = new ToggleButton();
    	filterVdbsButton = new ToggleButton();
		initWidget(uiBinder.createAndBindUi(this));
		init();
    }
    
    /**
     * Called after construction.
     */
    private void init() {
    	contentTitle.setText("Repository Contents");
    	
    	filterConnectionsButton.setStylePrimaryName("filterToggle");
    	filterDataSourcesButton.setStylePrimaryName("filterToggle");
    	filterViewsButton.setStylePrimaryName("filterToggle");
    	filterVdbsButton.setStylePrimaryName("filterToggle");

    	// Initial state is pressed
    	filterConnectionsButton.setDown(true);
    	filterDataSourcesButton.setDown(true);
    	filterViewsButton.setDown(true);
    	filterVdbsButton.setDown(true);

    	// button handlers
    	filterConnectionsButton.addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			if(filterConnectionsButton.isDown()) {
    				connectionPanel.setVisible(true);
    			} else {
    				connectionPanel.setVisible(false);
    			}
    		}
    	}); 
    	filterDataSourcesButton.addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			if(filterDataSourcesButton.isDown()) {
    				datasourcePanel.setVisible(true);
    			} else {
    				datasourcePanel.setVisible(false);
    			}
    		}
    	});                	
    	filterViewsButton.addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			if(filterViewsButton.isDown()) {
    				viewPanel.setVisible(true);
    			} else {
    				viewPanel.setVisible(false);
    			}
    		}
    	});                	
    	filterVdbsButton.addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			if(filterVdbsButton.isDown()) {
    				vdbPanel.setVisible(true);
    			} else {
    				vdbPanel.setVisible(false);
    			}
    		}
    	});
    }
    
}