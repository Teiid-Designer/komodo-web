package org.komodo.web.client.widgets;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.TreeViewModel;

public class RepoTreeDisplayer extends Composite {

    protected HorizontalPanel mainPanel = new HorizontalPanel();
    private HorizontalPanel treePanel;
    private CellTree cellTree;
    
    public RepoTreeDisplayer() {
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
    protected HorizontalPanel createTreePanel() {
        // Create a model for the tree.
        TreeViewModel model = new RepoTreeModel();

        /*
        * Create the tree using the model. We use <code>null</code> 
        * as the default value of the root node. The default value will 
        * be passed to CustomTreeModel#getNodeInfo();
        */
        cellTree = new CellTree(model, null);
     
        cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(cellTree);
        return hPanel;
    }
    	
}
