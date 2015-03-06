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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.client.utils.UiUtils;
import org.komodo.web.share.Constants;
import org.komodo.web.share.CoreConstants;
import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

@Dependent
@Templated("./RepoVdbContentPanel.html")
public class RepoVdbContentPanel extends Composite {
    
	// DeckPanel for content
	@Inject @DataField("list-vdb")
	private DeckPanel deckPanel;
	
    @Inject @DataField("image-vdb")
    private Image vdbImage;
    
    // VDB CellList
    @Inject 
    private VdbList vdbsList;
    
    private List<KomodoObjectBean> currentVdbs = new ArrayList<KomodoObjectBean>();
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	// Deck panel for DataSource list
    	HTMLPanel spinnerPanel = new HTMLPanel(AbstractImagePrototype.create(AppResource.INSTANCE.images().spinnner24x24Image()).getHTML());
    	Label errorLabel = new Label("Error Loading VDBs");
    	UiUtils.setMessageStyle(errorLabel, UiUtils.MessageType.ERROR);

    	this.vdbImage.setResource(AppResource.INSTANCE.images().filterVdbsImage());
    	
    	deckPanel.add(spinnerPanel);
    	deckPanel.add(errorLabel);
    	deckPanel.add(vdbsList);

    	loadVdbs();
    }
    
    public void loadVdbs() {
    	// Show spinner
    	deckPanel.showWidget(0);
    	
		KomodoRpcService.get().getKomodoNodes(null, new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
			@Override
			public void onReturn(final List<KomodoObjectBean> result) {	
				// Successfully got VDBs.  Set data and show the CellList
				currentVdbs.clear();
				currentVdbs.addAll(result);
				
				vdbsList.setData(currentVdbs);
				deckPanel.showWidget(2);
			}
			@Override
			public void onError(Throwable error) {
				// Failure - show failed message
				deckPanel.showWidget(1);
			}
		});
    }
    
    /**
     * Handles UiEvents
     * @param dEvent
     */
    public void onUiEvent(@Observes UiEvent dEvent) {
    	// Tree Loaded OK
    	if(dEvent.getType() == UiEventType.VDB_CREATE) {
        	String newVdbBaseName = "newVdb";
        	String newVdbName = newVdbBaseName + getUniqueSuffix(newVdbBaseName,getCurrentVdbNames());
        	doCreateVdb(newVdbName);
    	} else if(dEvent.getType() == UiEventType.VDB_DELETE) {
    		KomodoObjectBean kObj = dEvent.getKomodoObject();
    		if(kObj.getType()==CoreConstants.RelationalType.VDB) {
    	    	doDeleteVdb(kObj.getName());
    		}
    	}
    }
    
    /**
     * Create a new VDB and select it
     */
    private void doCreateVdb(final String vdbName) {
		KomodoRpcService.get().createVdb(vdbName, new IRpcServiceInvocationHandler<KomodoObjectBean>() {
			@Override
			public void onReturn(final KomodoObjectBean result) {	
				// Successfully got VDBs.  Set data and show the CellList
				currentVdbs.add(result);
				
				vdbsList.setData(currentVdbs);
				deckPanel.showWidget(2);
			}
			@Override
			public void onError(Throwable error) {
				// Failure - show failed message
				deckPanel.showWidget(1);
			}
		});
    }
    
    private void doDeleteVdb(final String vdbName) {
		KomodoRpcService.get().deleteVdb(vdbName, new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
			@Override
			public void onReturn(final List<KomodoObjectBean> result) {	
				// Successfully got VDBs.  Set data and show the CellList
				currentVdbs.clear();
				currentVdbs.addAll(result);
				
				vdbsList.setData(currentVdbs);
				deckPanel.showWidget(2);
			}
			@Override
			public void onError(Throwable error) {
				// Failure - show failed message
				deckPanel.showWidget(1);
			}
		});
    }
    
    public List<String> getCurrentVdbNames() {
    	List<String> names = new ArrayList<String>(currentVdbs.size());
    	for(KomodoObjectBean ko : currentVdbs) {
    		names.add(ko.getName());
    	}
    	return names;
    }
    
	private String getUniqueSuffix(String baseName, List<String> existingNames) {
		// If the name is not contained in existing names, base suffix is ok
		if(!existingNames.contains(baseName)) {
			return Constants.BLANK;
		}
		// Iterate generating new names until a good one is found
		String newName = null;
		boolean success = false;
		int i = 1;
		String suffix = Constants.BLANK;
		while(!success) {
			newName = baseName+i;
			if(!existingNames.contains(newName)) {
				success=true;
				suffix = String.valueOf(i);
			}
			i++;
		}
		return suffix;
	}
    
}