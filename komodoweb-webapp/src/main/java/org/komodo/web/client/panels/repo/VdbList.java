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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.resources.CellListResources;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;

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
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Composite for display of Table and Procedure names
 */
public class VdbList extends Composite {

    protected VerticalPanel panel = new VerticalPanel();
    protected Label label = new Label();

    private CellList<KomodoObjectBean> cellList;
    private SingleSelectionModel<KomodoObjectBean> listSelectionModel;
    private SelectionModel<KomodoObjectBean> deleteSelectionModel;
    
    public static String vdbImgHtml;
    private String deleteImgHtml;

	@Inject Event<UiEvent> uiEvent;
	
    public VdbList() {
        this.vdbImgHtml = AbstractImagePrototype.create(AppResource.INSTANCE.images().filterVdbsImage()).getHTML();
        this.deleteImgHtml = AbstractImagePrototype.create(AppResource.INSTANCE.images().filterVdbsImage()).getHTML();
    	
        initWidget( panel );
        panel.add(createListPanel());
    }
    
    private VerticalPanel createListPanel() {
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
    		public void onSelectionChange(SelectionChangeEvent event) {
                KomodoObjectBean vdb = listSelectionModel.getSelectedObject();
                fireUiEvent(vdb,UiEventType.KOBJECT_SELECTED);
    		}
    	});

    	outerPanel.add(cellList);
    	return outerPanel;
    }
    
    public void setData (List<KomodoObjectBean> data) {
    	// Push data into the CellList.
    	cellList.setRowCount(data.size(), true);
    	cellList.setRowData(0, data);
    }
    
    public List<KomodoObjectBean> getData( ) {
    	return cellList.getVisibleItems();
    }
    
    /**
     * Set the VDB selection
     * @param vdbName
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
        return Constants.BLANK;
    }

    
    /**
     * Fire selection event for a node
     * @param kObj the selected kObj
     */
    private void fireUiEvent(KomodoObjectBean kObj, UiEventType eventType) {
		UiEvent event = new UiEvent(eventType);
		event.setKomodoObject(kObj);
		uiEvent.fire(event);
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

    		sb.appendHtmlConstant("<table style=\"width:150px\">");

    		// Add the VDB image.
    		sb.appendHtmlConstant("<tr><td rowspan='3'>");
    		sb.appendHtmlConstant(VdbList.vdbImgHtml);
    		sb.appendHtmlConstant("</td>");

    		// Add the VDB name
    		sb.appendHtmlConstant("<td>");
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

    		public Cell<KomodoObjectBean> getCell() {
    			return cell;
    		}

    		public FieldUpdater<KomodoObjectBean, KomodoObjectBean> getFieldUpdater() {
    			return null;
    		}

    		public KomodoObjectBean getValue(KomodoObjectBean object) {
    			return object;
    		}
    	});
    	// -----------------------------------------------------------------
    	// Cell with add, edit, remove icons
    	// -----------------------------------------------------------------
    	hasCells.add(new HasCell<KomodoObjectBean, String>() {

    		private ImagesCell cell = new ImagesCell( );
    		
    		public Cell<String> getCell() {
    			return cell;
    		}

    		public FieldUpdater<KomodoObjectBean, String> getFieldUpdater() {
    			return new FieldUpdater<KomodoObjectBean, String>() {
    				@Override
    				public void update(int index, KomodoObjectBean object, String value) {
    					if(value.equals(ImagesCell.ADD_VDB)) {
    						fireUiEvent(object,UiEventType.VDB_CREATE);
    					} else if(value.equals(ImagesCell.DELETE_VDB)) {
    						fireUiEvent(object,UiEventType.VDB_DELETE);
    					} else if(value.equals(ImagesCell.EDIT_VDB)) {
    						// Do Nothing
    					}
    				}
    			};
    		}

    		public String getValue(KomodoObjectBean object) {
    			return "";
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
    
}
