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

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.container.IOC;
import org.komodo.spi.constants.StringConstants;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventListener;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.resources.CellListResources;
import org.komodo.web.client.services.UiEventService;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * VdbList - contains list of VDBs with add, edit, delete icons
 */
@Dependent
public class VdbList extends Composite implements UiEventListener {

	private PlaceManager placeManager;
    	
    protected VerticalPanel panel = new VerticalPanel();
    protected Label label = new Label();

    private CellList<KomodoObjectBean> cellList;
    private SingleSelectionModel<KomodoObjectBean> listSelectionModel;
    
    private static String VDB_IMG_HTML = AbstractImagePrototype.create(AppResource.INSTANCE.images().filterVdbsImage()).getHTML();

    private KomodoObjectBean vdbForDelete;
    private UiEventService uiEventService;
    
    /**
     * Constructor
     */
    public VdbList() {    	
        initWidget( panel );
        panel.add(createListPanel());
    }
    
    private VerticalPanel createListPanel() {
        placeManager = IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();
        
        uiEventService = UiEventService.get();
        uiEventService.addListener(this);
        
    	VerticalPanel outerPanel = new VerticalPanel();
    	
       	CellList.Resources resources = GWT.create(CellListResources.class);
    	// Create a CellList.
    	CompositeCell compositeCell = createCompositeCell(new VdbCell( ));

    	// Set a key provider that provides a unique key for each contact. If key is
    	// used to identify contacts when fields (such as the name and address)
    	// change.
    	cellList = new CellList<KomodoObjectBean>(compositeCell, resources);
    	cellList.setPageSize(30);
    	cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
    	cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

    	// Add a selection model so we can select cells.
    	listSelectionModel = new SingleSelectionModel<KomodoObjectBean>();
    	cellList.setSelectionModel(listSelectionModel);
    	listSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
    		@Override
			public void onSelectionChange(SelectionChangeEvent event) {
                KomodoObjectBean vdb = listSelectionModel.getSelectedObject();
                fireUiEvent(vdb,UiEventType.KOBJECT_SELECTED);
    		}
    	});

    	outerPanel.add(cellList);
    	return outerPanel;
    }
    
    /**
     * Set the list of VDBs
     * @param data the vdb list
     */
    public void setData (List<KomodoObjectBean> data) {
    	// Push data into the CellList.
    	cellList.setRowCount(data.size(), true);
    	cellList.setRowData(0, data);
    }
    
    /**
     * Get the list of VDBs
     * @return the vdb list
     */
    public List<KomodoObjectBean> getData( ) {
    	return cellList.getVisibleItems();
    }
    
    /**
     * Set the VDB selection
     * @param vdbName the name of the vdb
     */
    public void setSelection(String vdbName) {
    	for(KomodoObjectBean vdb : getData()) {
    		if(vdb.getName().equals(vdbName)) {
    			listSelectionModel.setSelected(vdb, true);
    			break;
    		}
    	}
    }
    
    /**
     * Get the name of the selected VDB
     * @return the VDB Name
     */
    public String getSelection() {    
        KomodoObjectBean vdb = listSelectionModel.getSelectedObject();
        if(vdb!=null) {
        	return vdb.getName();
        }
        return StringConstants.EMPTY_STRING;
    }

    
    /**
     * Fire selection event for a node
     * @param kObj the selected kObj
     */
    private void fireUiEvent(KomodoObjectBean kObj, UiEventType eventType) {
		UiEvent event = new UiEvent(eventType);
		event.setKomodoObject(kObj);
		UiEventService.get().fire(event);
    }
    
    /**
     * The Cell used to render a {@link KomodoObjectBean}.
     */
    static class VdbCell extends AbstractCell<KomodoObjectBean> {

    	public VdbCell( ) {
    	}

    	@Override
    	public void render(Context context, KomodoObjectBean value, SafeHtmlBuilder sb) {
    		// Value can be null, so do a null check..
    		if (value == null) {
    			return;
    		}

    		sb.appendHtmlConstant("<table style=\"width:180px\">");

    		// Add the VDB image.
    		sb.appendHtmlConstant("<tr><td>");
    		sb.appendHtmlConstant(VdbList.VDB_IMG_HTML);
    		sb.appendHtmlConstant("</td>");

    		// Add the VDB name
    		sb.appendHtmlConstant("<td style=\"text-align:left\">");
    		sb.appendEscaped(value.getName());
    		sb.appendHtmlConstant("</td></tr></table>");
    	}
    }
    
    // Construct a CompositeCell, which includes add,edit,delete icons
    private CompositeCell createCompositeCell(VdbCell vdbCell) {
    	// -----------------------------------------------------------------
    	// VDB Cell - icon and name
    	// -----------------------------------------------------------------
    	List<HasCell<KomodoObjectBean, ?>> hasCells = new ArrayList<HasCell<KomodoObjectBean, ?>>();
    	hasCells.add(new HasCell<KomodoObjectBean, KomodoObjectBean>() {

    		private VdbCell cell = new VdbCell( );

    		@Override
			public Cell<KomodoObjectBean> getCell() {
    			return cell;
    		}

    		@Override
			public FieldUpdater<KomodoObjectBean, KomodoObjectBean> getFieldUpdater() {
    			return null;
    		}

    		@Override
			public KomodoObjectBean getValue(KomodoObjectBean object) {
    			return object;
    		}
    	});
    	// -----------------------------------------------------------------
    	// Cell with add, edit, remove icons
    	// -----------------------------------------------------------------
    	hasCells.add(new HasCell<KomodoObjectBean, String>() {

    		private ImagesCell cell = new ImagesCell( );
    		
    		@Override
			public Cell<String> getCell() {
    			return cell;
    		}

    		@Override
			public FieldUpdater<KomodoObjectBean, String> getFieldUpdater() {
    			return new FieldUpdater<KomodoObjectBean, String>() {
    				@Override
    				public void update(int index, KomodoObjectBean object, String value) {
    					if(value.equals(ImagesCell.ADD_VDB)) {
    						// Show confirmation dialog for VDB create
    						showConfirmVdbCreateDialog();
    					} else if(value.equals(ImagesCell.DELETE_VDB)) {
    						// Show confirmation dialog for VDB delete
    						showConfirmVdbDeleteDialog(object);
    					} else if(value.equals(ImagesCell.EDIT_VDB)) {
    						// Do Nothing
    					}
    				}
    			};
    		}

    		@Override
			public String getValue(KomodoObjectBean object) {
    			return StringConstants.EMPTY_STRING;
    		}
    	});
    	
    	// -----------------------------------------------------------------
    	// Composite Cell
    	// -----------------------------------------------------------------
    	CompositeCell compositeCell = new CompositeCell<KomodoObjectBean>(hasCells) {
    		@Override
    		public void render(Context context, KomodoObjectBean value, SafeHtmlBuilder sb) {
    			sb.appendHtmlConstant("<table><tbody><tr>");
    			super.render(context, value, sb);
    			sb.appendHtmlConstant("</tr></tbody></table>");
    		}

    		@Override
    		protected Element getContainerElement(Element parent) {
    			// Return the first TR element in the table.
    			return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
    		}

    		@Override
    		protected <X> void render(Context context, KomodoObjectBean value,SafeHtmlBuilder sb, HasCell<KomodoObjectBean, X> hasCell) {
    			Cell<X> cell = hasCell.getCell();
    			sb.appendHtmlConstant("<td>");
    			cell.render(context, hasCell.getValue(value), sb);
    			sb.appendHtmlConstant("</td>");
    		}
    	};   
    	
    	return compositeCell;
    	
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
     * Shows the confirmation dialog for deleting a VDB
     */
    private void showConfirmVdbDeleteDialog(KomodoObjectBean kObj) {
    	// Remember VDB whose delete is requested.
    	vdbForDelete = kObj;
    	
		// Display the Confirmation Dialog for deletion of an existing VDB
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.CONFIRMATION_DIALOG_ARG_KEY, kObj.getName());
		parameters.put(Constants.CONFIRMATION_DIALOG_TYPE_KEY, Constants.CONFIRMATION_DIALOG_DELETE_VDB);
    	placeManager.goTo(new DefaultPlaceRequest(Constants.CONFIRMATION_DIALOG,parameters));    	
    }
    
	@Override
	public void handleUiEvent(UiEvent uiEvent) {
    	// Create VDB was confirmed
    	if(uiEvent.getType() == UiEventType.VDB_CREATE_CONFIRM_OK) {
    		UiEventService.get().fire(new UiEvent(UiEventType.VDB_CREATE));
        // Delete VDB was confirmed
    	} else if(uiEvent.getType() == UiEventType.VDB_DELETE_CONFIRM_OK) {
    		UiEvent deleteVdbEvent = new UiEvent(UiEventType.VDB_DELETE);
    		deleteVdbEvent.setKomodoObject(vdbForDelete);
    		UiEventService.get().fire(deleteVdbEvent);
    	}
	}
    
}
