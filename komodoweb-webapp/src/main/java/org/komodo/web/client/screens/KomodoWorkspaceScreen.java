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
package org.komodo.web.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.spi.constants.StringConstants;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.panels.repo.RepoContent;
import org.komodo.web.client.panels.repo.RepoDefnPanel;
import org.komodo.web.client.panels.vdb.VdbPanel;
import org.komodo.web.client.widgets.KomodoObjectPropertiesPanel;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * KomodoWorkspaceScreen - used for Komodo Workspace demo
 *
 */
@Dependent
@Templated("./KomodoWorkspaceScreen.html#page")
@WorkbenchScreen(identifier = "KomodoWorkspaceScreen")
public class KomodoWorkspaceScreen extends Composite {

    @Inject @DataField("repo-definition-panel")
    protected RepoDefnPanel repoDefinitionPanel;
    
    @Inject @DataField("repo-content-panel")
    protected RepoContent repoContentPanel;

    @Inject @DataField("details-deckpanel")
    protected DeckPanel detailsDeckPanel;
        
    @Inject 
    protected KomodoObjectPropertiesPanel propsPanel;

    @Inject 
    protected VdbPanel vdbPanel;
    
	@Override
    @WorkbenchPartTitle
    public String getTitle() {
      return StringConstants.EMPTY_STRING;
    }
    
    /**
     * @return view
     */
    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
//    	// Deck panel for DataSource list
//    	HTMLPanel spinnerPanel = new HTMLPanel(AbstractImagePrototype.create(AppResource.INSTANCE.images().spinnner24x24Image()).getHTML());
//    	repoTreePanel.add(spinnerPanel);
//    	repoTreePanel.add(repoTree);
//    	Label errorLabel = new Label("Error Loading Tree");
//    	UiUtils.setMessageStyle(errorLabel, UiUtils.MessageType.ERROR);
//    	repoTreePanel.add(errorLabel);
//    	repoTreePanel.showWidget(0);
//    	repoTree.initTree();
    	
        HTMLPanel selectSourcePanel = new HTMLPanel("Choose an item to see its properties");
        
    	// Add properties panel and Select label to deckPanel
    	detailsDeckPanel.add(propsPanel);
    	detailsDeckPanel.add(vdbPanel);
    	detailsDeckPanel.add(selectSourcePanel);
    	detailsDeckPanel.showWidget(2);
    }
    
    /**
     * Handles UiEvents
     * @param uiEvent the UiEvent
     */
    public void onUiEvent(@Observes UiEvent uiEvent) {
    	// Tree Loaded OK
    	if(uiEvent.getType() == UiEventType.REPO_TREE_LOAD_OK) {
        	//repoTreePanel.showWidget(1);
    	// Tree Load Error
    	} else if(uiEvent.getType() == UiEventType.REPO_TREE_LOAD_ERROR) {
        	//repoTreePanel.showWidget(2);
    	} else if(uiEvent.getType() == UiEventType.KOBJECT_SELECTED) {
    		KomodoObjectBean kObj = uiEvent.getKomodoObject();
    		if(kObj.getType()==KomodoType.VDB) {
    			vdbPanel.setKObject(kObj);
    			detailsDeckPanel.showWidget(1);
    		} else {
    			propsPanel.setKObject(kObj);
    			detailsDeckPanel.showWidget(0);
    		}
    	}
    }
                    
}
