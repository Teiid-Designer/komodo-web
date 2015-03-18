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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Templated("./RepoContentPanel.html")
public class RepoContentPanel extends Composite {
    
    // Repo Content Panel
    @Inject @DataField("panel-repo-content")
    private VerticalPanel repoContentPanel;
    
    @Inject
    private RepoDataSourceContentPanel datasourcePanel;
    @Inject
    private RepoViewContentPanel viewPanel;
    @Inject
    private RepoVdbContentPanel vdbPanel;
 
    private RepoConnectionContent connectionPanel;
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        connectionPanel = new RepoConnectionContent();
    	repoContentPanel.add(connectionPanel);
    	
    	repoContentPanel.add(datasourcePanel);
    	repoContentPanel.add(viewPanel);
    	repoContentPanel.add(vdbPanel);
    }
    
    /**
     * Handles UiEvents from viewEditorPanel
     * @param dEvent
     */
    public void onUiEvent(@Observes UiEvent dEvent) {
    	// change received from viewEditor
		if(dEvent.getType() == UiEventType.REPO_SHOW_VDBS) {
			vdbPanel.setVisible(true);
		} else if(dEvent.getType() == UiEventType.REPO_HIDE_VDBS) {
			vdbPanel.setVisible(false);
		} else if(dEvent.getType() == UiEventType.REPO_SHOW_DATASOURCES) {
			datasourcePanel.setVisible(true);
		} else if(dEvent.getType() == UiEventType.REPO_HIDE_DATASOURCES) {
			datasourcePanel.setVisible(false);
		} else if(dEvent.getType() == UiEventType.REPO_SHOW_VIEWS) {
			viewPanel.setVisible(true);
		} else if(dEvent.getType() == UiEventType.REPO_HIDE_VIEWS) {
			viewPanel.setVisible(false);
		} else if(dEvent.getType() == UiEventType.REPO_SHOW_CONNECTIONS) {
			connectionPanel.setVisible(true);
		} else if(dEvent.getType() == UiEventType.REPO_HIDE_CONNECTIONS) {
			connectionPanel.setVisible(false);
		}
    }
        
}