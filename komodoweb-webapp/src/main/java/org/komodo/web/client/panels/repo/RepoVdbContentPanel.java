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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.spi.constants.StringConstants;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.messages.ClientMessages;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.client.utils.UiUtils;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 *
 */
@Dependent
@Templated("./RepoVdbContentPanel.html")
/**
 * Accordion panel for VDB Content display
 */
public class RepoVdbContentPanel extends Composite {
    
    @Inject
	private PlaceManager placeManager;
    
	@Inject
    private ClientMessages i18n;
	
	// DeckPanel for content
	@Inject @DataField("list-vdb")
	private DeckPanel deckPanel;
	
    @Inject @DataField("image-vdb")
    private Image vdbImage;
    
    @Inject @DataField("btn-create-vdb")
    private Button createVdbButton;
    
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
    	Label errorLabel = new Label(i18n.format("RepoVdbContentPanel.error-loading-msg"));
    	UiUtils.setMessageStyle(errorLabel, UiUtils.MessageType.ERROR);

    	this.vdbImage.setResource(AppResource.INSTANCE.images().filterVdbsImage());
    	
    	createVdbButton.setHTML(AbstractImagePrototype.create(AppResource.INSTANCE.images().addIconImage()).getSafeHtml());
    	
    	deckPanel.add(spinnerPanel);
    	deckPanel.add(errorLabel);
    	deckPanel.add(vdbsList);

    	loadVdbs();
    }
    
    /**
     * Load the VDBs via the KomodoService
     */
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
     * Event handler that fires when the user clicks the New VDB button
     * @param event the click event
     */
    @EventHandler("btn-create-vdb")
    public void onCreateVdbButtonClick(ClickEvent event) {
    	showConfirmVdbCreateDialog();
    }
    
    /**
     * Shows the confirmation dialog for creating a new VDB
     */
    private void showConfirmVdbCreateDialog() {
		// Display the Confirmation Dialog for creation of a new VDB
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.CONFIRMATION_DIALOG_TYPE_KEY, Constants.CONFIRMATION_DIALOG_CREATE_VDB);
    	placeManager.goTo(new DefaultPlaceRequest(Constants.CONFIRMATION_DIALOG,parameters));    	
    }
        
    /**
     * Handles UiEvents
     * @param uiEvent the UiEvent
     */
    public void onUiEvent(@Observes UiEvent uiEvent) {
    	// Tree Loaded OK
    	if(uiEvent.getType() == UiEventType.VDB_CREATE) {
        	String newVdbName = Constants.NEW_VDB_BASENAME + getUniqueSuffix(Constants.NEW_VDB_BASENAME,getCurrentVdbNames());
        	doCreateVdb(newVdbName);
    	} else if(uiEvent.getType() == UiEventType.VDB_DELETE) {
    		KomodoObjectBean kObj = uiEvent.getKomodoObject();
    		if(kObj.getType()==KomodoType.VDB) {
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
    
    /**
     * Get the current VDB names
     * @return the list of VDB names
     */
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
			return StringConstants.EMPTY_STRING;
		}
		// Iterate generating new names until a good one is found
		String newName = null;
		boolean success = false;
		int i = 1;
		String suffix = StringConstants.EMPTY_STRING;
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