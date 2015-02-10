package org.komodo.web.client.widgets;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class RepoTreeDisplay extends Composite {

	@Inject
	private KomodoRpcService komodoService;

	@Inject Event<UiEvent> uiEvent;

    protected HorizontalPanel mainPanel = new HorizontalPanel();
    private HorizontalPanel treePanel;
    private Tree tree = new Tree();
    
    public RepoTreeDisplay() {
       	AppResource.INSTANCE.css().komodoTreeStyle().ensureInjected();
       	
    	mainPanel.setWidth("100%");
        initWidget( mainPanel );
        
        treePanel = createTreePanel();
        treePanel.setWidth("100%");
        mainPanel.add(treePanel);
    }
    
    /**
     * Create the panel
     * @return the panel widget
     */
    private HorizontalPanel createTreePanel( ) {

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem ti = event.getSelectedItem();
                KomodoObjectBean node = (KomodoObjectBean) ti.getUserObject();
                fireSelectionEvent(node);
            }
        });
        tree.addOpenHandler(new OpenHandler<TreeItem>() {
            public void onOpen(OpenEvent<TreeItem> event) {
                final TreeItem openedItem = event.getTarget();
                KomodoObjectBean node = (KomodoObjectBean) openedItem.getUserObject();
                String path = node.getPath();
        		komodoService.getKomodoNodes(path, new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
        			@Override
        			public void onReturn(final List<KomodoObjectBean> result) {	
        				if (result.size()>0) {
    						openedItem.removeItems();
        					for (KomodoObjectBean kObject: result) {
        						TreeItem ti = new TreeItem(new RepoTreeNodeRenderer(kObject));
        						//ti.setText(kObject.getName());
        						ti.setState(true);
        						ti.setUserObject(kObject);
        						
    							// If node has children, add temp node so that it can be expanded
        						if(kObject.hasChildren()) {
        							TreeItem tiChild = new TreeItem();
        							tiChild.setText("Temp");
        							ti.addItem(tiChild);
        						}
        						
        						openedItem.addItem(ti);
        					}
        				}
        			}
        			@Override
        			public void onError(Throwable error) {
        				Window.alert("error - "+error.getMessage());
        				//errorMessage = error.getMessage();
        				//refreshCompleteEvent.fire(new UiEvent(UiEventType.QUERY_RESULT_DISPLAYER_REFRESHED_ERROR));
        			}
        		});
            }
        });
        
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(tree);
        return hPanel;
    }
    
    /**
     * Fire selection event for a node
     * @param kObj the selected kObj
     */
    private void fireSelectionEvent(KomodoObjectBean kObj) {
		UiEvent event = new UiEvent(UiEventType.KOBJECT_SELECTED);
		event.setKomodoObject(kObj);
		uiEvent.fire(event);
    }
    
    public void initTree() {
		komodoService.getKomodoNodes(null, new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
			@Override
			public void onReturn(final List<KomodoObjectBean> result) {	
				if (result.size()>0) {
					for (KomodoObjectBean kObject: result) {
						TreeItem ti = new TreeItem(new RepoTreeNodeRenderer(kObject));
						//ti.setText(kObject.getName());
						ti.setUserObject(kObject);
						
						// Add temporary item so that we can expand the node
						TreeItem tiChild = new TreeItem();
						tiChild.setText("Temp");
						ti.addItem(tiChild);
						
						tree.addItem(ti);
						ti.setState(false);
					}
				}
				uiEvent.fire(new UiEvent(UiEventType.REPO_TREE_LOAD_OK));

			}
			@Override
			public void onError(Throwable error) {
				uiEvent.fire(new UiEvent(UiEventType.REPO_TREE_LOAD_ERROR));
			}
		});
    }
            
}
