package org.komodo.web.client.widgets;

import javax.inject.Inject;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class RepoTreeDisplayer extends Composite {

	@Inject
	private RepoTreeModel repoTreeModel;
	
    protected HorizontalPanel mainPanel = new HorizontalPanel();
    private HorizontalPanel treePanel;
    private CellTree cellTree;
    
    public RepoTreeDisplayer() {
    	mainPanel.setWidth("100%");
        initWidget( mainPanel );
    }
    
    /**
     * Create the panel
     * @return the panel widget
     */
    protected HorizontalPanel createTreePanel( ) {
        /*
        * Create the tree using the model. We use <code>null</code> 
        * as the default value of the root node. The default value will 
        * be passed to CustomTreeModel#getNodeInfo();
        */
        cellTree = new CellTree(repoTreeModel, null);
     
        cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(cellTree);
        return hPanel;
    }
    
    public void addTree() {
        treePanel = createTreePanel( );
        treePanel.setWidth("100%");
        mainPanel.add(treePanel);
    }
        	
}
